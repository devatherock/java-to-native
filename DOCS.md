## Config

The following parameters can be set to configure the plugin.

* **debug** - Flag to enable debug logs. Optional, by default, debug logs are disabled
* **jar_name** - Executable jar file to convert into native binary
* **config_file** - Path to a yaml configuration file
* **classpath** - Path containing class files and jars to use as class path. Defaults to `build/libs/*:build/classes`
* **reflection_path** - Path to the reflection configuration if it already exists. If not specified, it will be generated at `build/native/graal-config/`

## Examples
### Circle CI
```yaml
java_to_native:
  docker:
    - image: devatherock/java-to-native:0.1.3
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
    image: devatherock/java-to-native:0.1.3
    parameters:
      jar_name: build/native/libs/YamlValidator.jar
      config_file: config/graal.yml
```