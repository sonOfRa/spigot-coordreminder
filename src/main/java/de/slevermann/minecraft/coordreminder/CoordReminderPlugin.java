package de.slevermann.minecraft.coordreminder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class CoordReminderPlugin extends JavaPlugin {

    private CoordReminderCommand cmd = null;

    private static final String DATA_LOCATION = "plugins/CoordReminder/";

    private static final String DATA_FILENAME = "coords.json";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onEnable() {
        if (createDataDir()) {
            File dataFile = new File(DATA_LOCATION + DATA_FILENAME);

            if (!dataFile.exists()) {
                getLogger().log(Level.WARNING, "Data file not found");
            } else {
                try {
                    ConcurrentHashMap<UUID, Map<String, Coordinate>> savedCoords = objectMapper.readValue(dataFile,
                            new TypeReference<ConcurrentHashMap<UUID, Map<String, Coordinate>>>() {
                            });
                    cmd = new CoordReminderCommand(savedCoords);
                    getLogger().log(Level.INFO, "Loaded data successfully");
                } catch (IOException e) {
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
            try {
                objectMapper.writeValue(new File(DATA_LOCATION + DATA_FILENAME), cmd.getSavedCoordinates());
            } catch (IOException e) {
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
