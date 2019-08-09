package de.slevermann.minecraft.coordreminder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coordinate {
    private int x;
    private int y;
    private int z;

    public Coordinate(Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }
}
