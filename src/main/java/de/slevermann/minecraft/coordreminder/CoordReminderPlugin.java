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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class CoordReminderPlugin extends JavaPlugin {

    private CoordReminderCommand cmd = null;

    private static final String DATA_LOCATION = "plugins/CoordReminder/";

    private static final String DATA_FILENAME = "coords.json";

    private static final Type TYPE = new TypeToken<ConcurrentHashMap<UUID, Map<String, Coordinate>>>() {
    }.getType();

    private Gson gson = new Gson();


    @Override
    public void onEnable() {
        if (createDataDir()) {
            File dataFile = new File(DATA_LOCATION + DATA_FILENAME);

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
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (createDataDir()) {
            try (FileWriter fw = new FileWriter(new File(DATA_LOCATION + DATA_FILENAME));
                 JsonWriter writer = new JsonWriter(fw)) {
                gson.toJson(cmd.getSavedCoordinates(), TYPE, writer);
            } catch (JsonIOException | IOException e) {
                getLogger().log(Level.WARNING, "Failed to write saved coordinate data", e);
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
        File dataDirectory = new File(DATA_LOCATION);
        if (!dataDirectory.exists()) {
            if (!dataDirectory.mkdirs()) {
                getLogger().log(Level.WARNING, "Failed to create data directory");
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }
}
