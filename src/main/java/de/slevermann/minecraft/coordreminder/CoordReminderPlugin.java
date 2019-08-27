package de.slevermann.minecraft.coordreminder;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class CoordReminderPlugin extends JavaPlugin {

    private CoordReminderCommand cmd = null;

    private static final String DATA_FILENAME = "coords.json";

    private static final Type TYPE = new TypeToken<ConcurrentHashMap<UUID, Map<String, Coordinate>>>() {
    }.getType();

    private Gson gson = new Gson();


    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (createDataDir()) {
            File dataFile = new File(getDataFolder().getAbsolutePath() + File.separator + DATA_FILENAME);
            if (!dataFile.exists()) {
                getLogger().log(Level.WARNING, "Data file not found");
            } else {
                try (FileReader fr = new FileReader(dataFile);
                     JsonReader reader = new JsonReader(fr)) {
                    ConcurrentHashMap<UUID, Map<String, Coordinate>> savedCoords = gson.fromJson(reader, TYPE);
                    if (savedCoords == null) {
                        cmd = new CoordReminderCommand();
                    } else {
                        cmd = new CoordReminderCommand(savedCoords);
                    }
                    getLogger().log(Level.INFO, "Loaded data successfully");
                } catch (IOException | JsonIOException e) {
                    getLogger().log(Level.WARNING, "Failed to read saved coordinate data", e);
                }
            }
        }

        if (cmd == null) {
            cmd = new CoordReminderCommand();
            getLogger().log(Level.INFO, "Loaded plugin without saved data");
        }
        this.getCommand("coord").setExecutor(cmd);
        this.getCommand("coord").setTabCompleter(cmd);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (createDataDir()) {
            boolean dataWritten = false;
            try (FileWriter fw = new FileWriter(new File(getDataFolder().getAbsolutePath() +
                    File.separator + DATA_FILENAME));
                 JsonWriter writer = new JsonWriter(fw)) {
                gson.toJson(cmd.getSavedCoordinates(), TYPE, writer);
                dataWritten = true;
            } catch (JsonIOException | IOException e) {
                getLogger().log(Level.WARNING, "Failed to write saved coordinate data", e);
            }
            int backupCount = getConfig().getInt("backups");
            if (dataWritten && backupCount > 0) {
                File backupDirectory = new File(getDataFolder().getAbsolutePath() + File.separator +
                        "backups");
                List<File> backups = Arrays.asList(backupDirectory.listFiles((file, s) -> s.startsWith("backup-coords")
                        && s.endsWith(".json")));
                deleteOldest(backups, backupCount - 1);
                Path source = Paths.get(getDataFolder().getAbsolutePath() + File.separator + DATA_FILENAME);
                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd-HHmmss");
                String fileName = "backup-coords-" + sdf.format(new Date()) + ".json";
                Path target = backupDirectory.toPath().resolve(fileName);
                try {
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "Failed to write coordinate backup", e);
                }
            }
        } else {
            getLogger().log(Level.INFO, "Data directory does not exist, not saving data.");
        }
        super.onDisable();
    }

    /**
     * Attempt to create the data directory if it does not exist.
     *
     * @return true if the data dir exists (before or after trying to create it)
     */
    private boolean createDataDir() {
        File dataDirectory = new File(getDataFolder().getAbsolutePath() + File.separator +
                "backups");
        boolean success = false;
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                getLogger().log(Level.WARNING, "Failed to create data directory");
            } else {
                success = true;
            }
        } else {
            success = true;
        }

        return success;
    }

    /**
     * Given a list of files, delete as many old files as necessary to retain only the most recent files
     *
     * @param files    input list of files
     * @param maxCount how many files to retain
     */
    private void deleteOldest(List<File> files, int maxCount) {
        if (files.size() > maxCount) {
            files.sort(Comparator.comparing(File::lastModified));
            int deleteCount = files.size() - maxCount;
            for (int i = 0; i < deleteCount; i++) {
                if (!files.get(i).delete()) {
                    getLogger().log(Level.WARNING, "Failed to delete backup file " + files.get(i).getAbsolutePath());
                }
            }
        }
    }
}
