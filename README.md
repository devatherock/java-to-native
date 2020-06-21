[![CircleCI](https://circleci.com/gh/devatherock/java-to-native.svg?style=svg)](https://circleci.com/gh/devatherock/java-to-native)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/java-to-native.svg)](https://hub.docker.com/r/devatherock/java-to-native/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/java-to-native.svg?sort=date)](https://hub.docker.com/r/devatherock/java-to-native/)
[![Docker Image Layers](https://img.shields.io/microbadger/layers/devatherock/java-to-native.svg)](https://microbadger.com/images/devatherock/java-to-native)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# java-to-native
CI plugin to convert a java program into a [graalvm native image](https://www.graalvm.org/docs/reference-manual/native-image/). 
For a listing of available options and usage samples, please take a look at the [docs](DOCS.md).

## Usage

Execute from the working directory:

```
docker run --rm \
  -e PLUGIN_JAR_NAME=YamlValidator.jar \
  -e PLUGIN_CONFIG_FILE=config/graal.yml \
  devatherock/java-to-native:0.1.3
```

## Configuration
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