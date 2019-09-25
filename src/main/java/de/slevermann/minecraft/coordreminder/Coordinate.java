package de.slevermann.minecraft.coordreminder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.logging.Level;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
    private int x;
    private int y;
    private int z;
    private World.Environment environment;

    public Coordinate(World.Environment environment, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.environment = environment;
    }

    public Coordinate(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        World w = location.getWorld();
        if (w == null) {
            Bukkit.getLogger().log(Level.WARNING, "World is null for some reason. Continuing with null as environment");
            this.environment = null;
        } else {
            this.environment = w.getEnvironment();
        }
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }

    public String coloredString() {
        return "" + ChatColor.AQUA + "World: " + environment + "; " + ChatColor.RED + "X: " + x + ChatColor.RESET +
                ", " + ChatColor.GREEN + "Y: " + y + ChatColor.RESET + ", " + ChatColor.BLUE + "Z: " + z;
    }
}
