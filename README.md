# Remembering coordinates in spigot

Simply build the plugin with ```mvn package```
and move the resulting jar to your plugins folder.

This plugin stores a list of coordinates per player and saves it between
server restarts and player disconnects

## Usage
- ```/coord``` shows the current coordinates
- ```/coord clear``` deletes all coordinates for the current player
- ```/coord delete [name]``` deletes the coordinates for the given name
- ```/coord get [name]``` gets the coordinates for the given name
- ```/coord list``` shows saved coordinates for the current player
- ```/coord set [name]``` stores the current coordinates as the given name
- ```/coord set [name] [x] [y] [z]``` stores coordinate x, y, z as name
- ```/coord share [name]``` shares the coordinates for the given name to chat
- ```/coord tp [name]``` teleports to the given coordinates

## Configuration
There is a single configuration key, named ``backups``. It determines how many
backup coordinate files are kept around (one is created on every server shutdown).
If set to 0, no backups are kept. The default value is 2. This can be adjusted at
build time with ```mvn -Dbackups=n package``` to store ``n`` backups instead.
