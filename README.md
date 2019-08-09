# Remembering coordinates in spigot

Simply build the plugin with ```mvn package```
and move the resulting jar to your plugins folder.

This plugin stores a list of coordinates per player and saves it between
server restarts and player disconnects

## Usage
- ```/coord``` shows the current coordinates
- ```/coord list``` shows saved coordinates for the current player
- ```/coord set [name]``` stores the current coordinates as the given name
- ```/coord get [name]``` gets the coordinates for the given name
- ```/coord delete [name]``` deletes the coordinates for the given name
