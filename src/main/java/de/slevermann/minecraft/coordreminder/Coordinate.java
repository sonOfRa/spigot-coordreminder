package de.slevermann.minecraft.coordreminder;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.*;

import java.util.UUID;
import java.util.logging.Level;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {

    @Expose
    private int x;
    @Expose
    private int y;
    @Expose
    private int z;
    @Expose
    private UUID worldId;

    private transient World world;

    @Expose(serialize = false)
    private World.Environment environment;

    public Coordinate(UUID worldId, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldId = worldId;
        if (worldId != null) {
            this.world = Bukkit.getWorld(worldId);
        }
        if (world != null) {
            this.environment = world.getEnvironment();
        }
    }

    public Coordinate(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        World w = location.getWorld();
        if (w == null) {
            Bukkit.getLogger().log(Level.WARNING, "World is null for some reason. Continuing with null as environment");
            this.worldId = null;
        } else {
            this.worldId = w.getUID();
            this.world = w;
            this.environment = world.getEnvironment();
        }
    }

    @Override
    public String toString() {
        return "World: " + this.environment + "X: " + x + ", Y: " + y + ", Z: " + z;
    }

    public String coloredString() {
        return "" + ChatColor.AQUA + "World: " + this.environment + "; " + ChatColor.RED + "X: " + x + ChatColor.RESET +
                ", " + ChatColor.GREEN + "Y: " + y + ChatColor.RESET + ", " + ChatColor.BLUE + "Z: " + z;
    }

    public Location getLocation() {
        return new Location(this.getWorld(), x, y, z);
    }

    public World getWorld() {
        if (this.world == null) {
            synchronized (this) {
                if (this.world == null) {
                    if (this.worldId == null) {
                        return null;
                    }
                    this.world = Bukkit.getWorld(this.worldId);
                }
            }
        }
        return this.world;
    }
}
