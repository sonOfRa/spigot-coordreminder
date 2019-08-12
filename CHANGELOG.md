# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
