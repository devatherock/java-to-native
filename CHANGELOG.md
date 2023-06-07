# Changelog

## [Unreleased]
### Added
- [#3](https://github.com/devatherock/java-to-native/issues/3): Functional tests

### Changed
- [#5](https://github.com/devatherock/java-to-native/issues/5): Used latest `scriptjar`

## [2.2.0] - 2023-06-06
### Changed
- Updated base image with upx binary of correct architecture

## [2.1.0] - 2023-06-06
### Changed
- Used custom graalvm base image that contains gzip and tar

## [2.0.0] - 2023-06-06
### Changed
- Updated dockerhub readme in CI pipeline
- [#7](https://github.com/devatherock/java-to-native/issues/7): Merged contents of `DOCS.md` into `README.md`
- [#4](https://github.com/devatherock/java-to-native/issues/4): Built a multi-arch docker image
- Upgraded graalvm from `22.1.0` to `22.3.2`

## [1.0.0] - 2022-05-17
### Changed
- [#1](https://github.com/devatherock/java-to-native/issues/1): Upgraded base image to graalvm 22.x

## [0.1.3] - 2020-06-21
### Changed
- Fixed the bug that was making config file mandatory

## [0.1.2] - 2020-06-21
### Changed
- Stopped using environment variable `VELA`

## [0.1.1] - 2020-06-20
### Added
- `--static` and `--no-fallback` options as default

## [0.1.0] - 2020-06-20
### Added
- Initial version. Converts a jar or class into a native image, generating reflection configuration if required