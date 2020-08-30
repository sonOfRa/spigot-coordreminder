# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.6.1] 2020-08-30

### Changed
- Upgrade to 1.16.2


## [1.6.0] 2020-08-09

### Changed
- Update for Minecraft 1.16

## [1.5.0] 2019-10-02

### Added
- Players can now teleport to locations they have saved

## [1.4.0] 2019-09-25

### Added
- Enable coordinate sharing to chat for all players
- Coordinates in the current world can now be set with X, Y, Z coordinates

## [1.3.0] 2019-08-27

### Added
- Autocompletions for commands and coordinate names

## [1.2.0] 2019-08-12

### Added

- Backup functionality. A config key specifies how many backup coordinate files are
saved. If there are too many files, the oldest backup file is deleted.

## [1.1.0] 2019-08-12

### Changed

- JSON backend is now GSON, which is a dependency of Spigot anyhow,
 saving a few Megabytes in plugin size

## [1.0.0] 2019-08-12

### Added

- Coordinate management via ```/coord``` command
- Coordinate saving to JSON file
