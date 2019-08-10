package de.slevermann.minecraft.coordreminder;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CoordReminderCommand implements CommandExecutor {

    private ConcurrentHashMap<UUID, Map<String, Coordinate>> savedCoordinates;

    public CoordReminderCommand() {
        this.savedCoordinates = new ConcurrentHashMap<>();
    }

    public CoordReminderCommand(ConcurrentHashMap<UUID, Map<String, Coordinate>> savedCoordinates) {
        this.savedCoordinates = savedCoordinates;
    }

    public ConcurrentHashMap<UUID, Map<String, Coordinate>> getSavedCoordinates() {
        return savedCoordinates;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            UUID uuid = sender.getUniqueId();

            // Ensure that there is a Map for the command sender to avoid null checking later on
            Map<String, Coordinate> coordinatesForSender = savedCoordinates.get(uuid);
            if (coordinatesForSender == null) {
                Map<String, Coordinate> newCoords = new HashMap<>();
                savedCoordinates.put(uuid, newCoords);
                coordinatesForSender = newCoords;
            }

            Coordinate currentCoordinate = new Coordinate(sender.getLocation());
            if (args.length == 0) {
                sender.sendMessage("Current coordinates: " + currentCoordinate.coloredString());
                return true;
            } else if (args.length == 1) {
                if (args[0].equals("list")) {
                    // List coordinate names saved for player
                    sender.sendMessage("List of saved coordinates: ");

                    for (Map.Entry<String, Coordinate> entry : coordinatesForSender.entrySet()) {
                        sender.sendMessage(entry.getKey() + ": " + entry.getValue().coloredString());
                    }
                    return true;
                } else if (args[0].equals("clear")) {
                    sender.sendMessage("Deleting all saved coordinates");
                    coordinatesForSender.clear();
                    return true;
                }
            } else if (args.length == 2) {
                String name = args[1];
                Coordinate coord = coordinatesForSender.get(name);
                // Either "get name", "set name" or "delete name"
                switch (args[0]) {
                    case "get":
                        if (coord == null) {
                            sender.sendMessage("No coordinate saved under that name");
                        } else {
                            sender.sendMessage(coord.coloredString());
                        }
                        return true;
                    case "set":
                        if (coord != null) {
                            sender.sendMessage("There already is a coordinate under that name. Please delete first.");
                        } else {
                            coordinatesForSender.put(name, currentCoordinate);
                            sender.sendMessage("Saved current location as '" + name + "':");
                            sender.sendMessage(currentCoordinate.coloredString());
                        }
                        return true;
                    case "delete":
                        if (coord == null) {
                            sender.sendMessage("No coordinate saved under that name");
                        } else {
                            coordinatesForSender.remove(name);
                            sender.sendMessage("Removed coordinate '" + name + "'");
                        }
                        return true;
                }
            }
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Command can only be used by players");
            return true;
        }
        return false;
    }
}
