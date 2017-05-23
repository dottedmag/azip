# Change Log
All notable changes to this project will be documented in this file. This change
log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Changed

## 0.2.0 - 2017-05-24
### Added
- `down?` predicate to check the possibility of navigation down via a specific
  key. `(azip/down? z :key)` is faster than `(some? #(= % :key) (azip/keys z))`.

## 0.1.0 - 2017-05-23
### Added
- Initial release.

[Unreleased]: https://github.com/your-name/azip/compare/0.1.1...HEAD
[0.1.1]: https://github.com/your-name/azip/compare/0.1.0...0.1.1
