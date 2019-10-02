package de.slevermann.minecraft.coordreminder;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.UUID;

public class CoordinateDeserializer implements JsonDeserializer<Coordinate> {
    @Override
    public Coordinate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int x = jsonObject.getAsJsonPrimitive("x").getAsInt();
        int y = jsonObject.getAsJsonPrimitive("y").getAsInt();
        int z = jsonObject.getAsJsonPrimitive("z").getAsInt();

        JsonElement environment = jsonObject.get("environment");

        if (environment == null) {
            String worldId = jsonObject.get("worldId").getAsString();
            UUID worldUuid = UUID.fromString(worldId);
            return new Coordinate(worldUuid, x, y, z);
        } else {
            World.Environment env = World.Environment.valueOf(environment.getAsString());
            World world = Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == env).findFirst().orElse(null);
            Location loc = new Location(world, x, y, z);
            return new Coordinate(loc);
        }
    }
}
