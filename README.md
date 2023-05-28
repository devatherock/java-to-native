[![CircleCI](https://circleci.com/gh/devatherock/java-to-native.svg?style=svg)](https://circleci.com/gh/devatherock/java-to-native)
[![Version](https://img.shields.io/docker/v/devatherock/java-to-native?sort=date)](https://hub.docker.com/r/devatherock/java-to-native/)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/java-to-native.svg)](https://hub.docker.com/r/devatherock/java-to-native/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/java-to-native.svg?sort=date)](https://hub.docker.com/r/devatherock/java-to-native/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# java-to-native
CI plugin to convert a java program into a [graalvm native image](https://www.graalvm.org/docs/reference-manual/native-image/).

## Plugin config
The following parameters can be set to configure the plugin.

* **debug** - Flag to enable debug logs. Optional, by default, debug logs are disabled
* **jar_name** - Executable jar file to convert into native binary
* **config_file** - Path to a yaml configuration file
* **classpath** - Path containing class files and jars to use as class path. Defaults to `build/libs/*:build/classes`
* **reflection_path** - Path to the reflection configuration if it already exists. If not specified, it will be generated at `build/native/graal-config/`

## Usage
### Docker

Execute from the working directory:

```
docker run --rm \
  -v path/to/jar:/work \
  -w=/work \
  -e PLUGIN_JAR_NAME=/work/build/native/libs/YamlValidator.jar \
  -e PLUGIN_CONFIG_FILE=/work/config/graal.yml \
  devatherock/java-to-native:1.0.0
```

### Circle CI

```yaml
java_to_native:
  docker:
    - image: devatherock/java-to-native:1.0.0
  working_directory: ~/drone-yaml-validator
  environment:
    PLUGIN_JAR_NAME: build/native/libs/YamlValidator.jar
    PLUGIN_CONFIG_FILE: config/graal.yml
  steps:
    - checkout
    - attach_workspace:
        at: ~/drone-yaml-validator
    - run: sh /scripts/entry-point.sh
    - persist_to_workspace:
        root: ~/drone-yaml-validator
        paths:
          - YamlValidator
```

### vela

```yaml
steps:
  - name: java_to_native
    ruleset:
      branch: master
      event: push
    image: devatherock/java-to-native:1.0.0
    parameters:
      jar_name: build/native/libs/YamlValidator.jar
      config_file: config/graal.yml
```

## native-image config
A YAML configuration file can be provided to supply additional arguments to the `native-image` command and to specify 
a list of arguments with which to run the main class or the jar to generate reflection config

### Sample
```yaml
native-image:
  build:
    # Additional arguments to pass to the native image build
    additional-args:
      - "-H:IncludeResourceBundles=net.sourceforge.argparse4j.internal.ArgumentParserImpl"
    # If specified, only these arguments will be passed to the image build
    #override-args:
    #  - "--no-server"
  agent:
    args:
      - ['--debug', 'false', '--path', 'src/test/resources/data/valid']
      - ['--debug', 'false', '--path', 'src/test/resources/data/invalid']
      - ['--debug', 'false', '--path', 'src/test/resources/data/invalid2']
      - ['--debug', 'true', '--path', 'src/test/resources/data/valid']
      - ['--debug', 'true', '--path', 'src/test/resources/data/invalid']
      - ['--debug', 'true', '--path', 'src/test/resources/data/invalid2']
      - []
      - ['--help']
```
