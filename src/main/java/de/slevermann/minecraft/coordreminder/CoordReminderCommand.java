package de.slevermann.minecraft.coordreminder;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CoordReminderCommand implements TabExecutor {

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
                // Either "get name", "set name", "delete name" or "share name"
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
                    case "share":
                        if (coord == null) {
                            sender.sendMessage("No coordinate saved under that name");
                        } else {
                            Bukkit.broadcastMessage(((Player) commandSender).getDisplayName() + " sent coordinate " + name + ":");
                            Bukkit.broadcastMessage(coord.coloredString());
                        }
                        return true;
                }
            } else if (args.length == 5) {
                String name = args[1];
                if (args[0].equals("set")) {
                    if (coordinatesForSender.containsKey(name)) {
                        sender.sendMessage("There already is a coordinate under that name. Please delete first.");
                    } else {
                        int x, y, z;
                        try {
                            x = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("Invalid X coordinate");
                            return true;
                        }
                        try {
                            y = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("Invalid Y coordinate");
                            return true;
                        }
                        try {
                            z = Integer.parseInt(args[4]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("Invalid Z coordinate");
                            return true;
                        }

                        Coordinate coord = new Coordinate(sender.getLocation().getWorld().getEnvironment(), x, y, z);
                        coordinatesForSender.put(name, coord);
                        sender.sendMessage("Saved location as '" + name + "':");
                        sender.sendMessage(coord.coloredString());
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

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;
            UUID uuid = sender.getUniqueId();

            List<String> commands = ImmutableList.of("clear", "delete", "get", "list", "set", "share");

            // Ensure that there is a Map for the command sender to avoid null checking later on
            Map<String, Coordinate> coordinatesForSender = savedCoordinates.get(uuid);
            if (coordinatesForSender == null) {
                Map<String, Coordinate> newCoords = new HashMap<>();
                savedCoordinates.put(uuid, newCoords);
                coordinatesForSender = newCoords;
            }

            if (args.length == 0) {
                return commands;
            }

            if (args.length == 1) {
                String arg = args[0];
                if (arg.equals("get") || arg.equals("delete") || arg.equals("share")) {
                    return new ArrayList<>(coordinatesForSender.keySet());
                } else if (!commands.contains(arg)) {
                    // We don't have a full command, so try to find out if we have a valid partial command
                    List<String> completions = new ArrayList<>();
                    for (String c : commands) {
                        if (c.startsWith(arg)) {
                            completions.add(c);
                        }
                    }
                    return completions;
                }
            }

            if (args.length == 2) {
                String commandName = args[0];
                if (commandName.equals("get") || commandName.equals("delete") || commandName.equals("share")) {
                    String partialCoordinate = args[1];

                    List<String> completions = new ArrayList<>();

                    for (String coordinateName : coordinatesForSender.keySet()) {
                        if (coordinateName.startsWith(partialCoordinate)) {
                            completions.add(coordinateName);
                        }
                    }
                    return completions;
                }
            }
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }
}
