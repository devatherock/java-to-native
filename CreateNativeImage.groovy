@Grab(group = 'org.yaml', module = 'snakeyaml', version = '1.25')
@Grab(group = 'org.codehaus.groovy', module = 'groovy-cli-commons', version = '2.5.7')

import groovy.cli.commons.CliBuilder
import groovy.transform.Field
import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Level
import java.util.logging.Logger

@Field static final String CONFIG_AGENT_ARGS = 'native-image.agent.args'
@Field static final String CONFIG_BUILD_ADDITIONAL_ARGS = 'native-image.build.additional-args'
@Field static final String CONFIG_BUILD_OVERRIDE_ARGS = 'native-image.build.override-args'
@Field static final String ARG_GRAPE_DISABLE = '-Dgroovy.grape.enable=false'
@Field static final String REGEX_ENV_VARIABLE = '\\$\\{([A-Z_]+)\\}'

System.setProperty('java.util.logging.SimpleFormatter.format',
        '%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS.%1$tL%1$tz %4$s %5$s%6$s%n')
@Field Logger logger = Logger.getLogger('CreateNativeImage.log')

def cli = new CliBuilder(usage: 'groovy CreateNativeImage.groovy [options]')
cli.m(longOpt: 'main-class-name', args: 1, argName: 'main-class-name', 'The name of the main class')
cli.j(longOpt: 'jar-name', args: 1, argName: 'jar-name', 'The jar containing the main class')
cli.c(longOpt: 'config', args: 1, argName: 'config', 'File containing configuration for the native image build')
cli.cp(longOpt: 'classpath', args: 1, argName: 'classpath', 'Classpath for the native image build')
cli.rp(longOpt: 'reflection-config-path', args: 1, argName: 'reflection-config-path', 'Path in which to generate the reflection config')
cli.d(longOpt: 'debug', args: 0, argName: 'debug', 'Enable debug logs')

def options = cli.parse(args)
if (!(options.m || options.j)) {
    cli.usage()
    System.exit(1)
}

// Enable debug logs
if (options.d) {
    Logger root = Logger.getLogger('')
    root.setLevel(Level.FINE)
    root.getHandlers().each { it.setLevel(Level.FINE) }
}

// Read config file
String configFile = options.c
def config = [:]
if (configFile) {
    Yaml yaml = new Yaml()
    config.putAll(getFlattenedMap('', yaml.load(new File(configFile).text)))
}
logger.fine({ String.valueOf(config) })

// Generate reflection config
String classPath = options.cp ?: 'build/native/libs/*:build/native/classes'
String reflectConfigPath = options.rp ?: 'build/native/graal-config/'
Files.createDirectories(Paths.get(reflectConfigPath))

def firstCommand = ['java', "-agentlib:native-image-agent=config-output-dir=${reflectConfigPath}",
                    '-cp', classPath, ARG_GRAPE_DISABLE]
def baseCommand = ['java', "-agentlib:native-image-agent=config-merge-dir=${reflectConfigPath}",
                   '-cp', classPath, ARG_GRAPE_DISABLE]
def executable = []
if (options.m) {
    executable.add(options.m)
} else {
    executable.add('-jar')
    executable.add(options.j)
}
firstCommand.addAll(executable)
baseCommand.addAll(executable)

if (config[CONFIG_AGENT_ARGS]) {
    boolean first = true
    def commandWithArgs = []

    config[CONFIG_AGENT_ARGS].each { arguments ->
        commandWithArgs.clear()

        if (first) {
            commandWithArgs.addAll(firstCommand)
            first = false
        } else {
            commandWithArgs.addAll(baseCommand)
        }

        arguments.each { argument ->
            // If argument is an environment variable, replace it with its value if present
            if (argument =~ REGEX_ENV_VARIABLE) {
                String envVariableName = argument.find(REGEX_ENV_VARIABLE) { matchedText, group ->
                    return group
                }
                String envVariableValue = System.getenv(envVariableName)
                commandWithArgs.add(envVariableValue ?: argument)
            } else {
                commandWithArgs.add(argument)
            }
        }
        executeCommand(commandWithArgs)
    }
} else {
    executeCommand(firstCommand)
}

/** Build native image **/
def nativeImageCommand = ['native-image', '-cp', classPath]

// If override arguments are specified, add only those
if (config[CONFIG_BUILD_OVERRIDE_ARGS]) {
    nativeImageCommand.addAll(config[CONFIG_BUILD_OVERRIDE_ARGS])
} else {
    nativeImageCommand.addAll([ARG_GRAPE_DISABLE, '--no-server', '--static', '--allow-incomplete-classpath',
                               '--no-fallback', '--report-unsupported-elements-at-runtime', '--initialize-at-build-time',
                               '--enable-url-protocols=http,https', "-H:ConfigurationFileDirectories=${reflectConfigPath}"])

    if (config[CONFIG_BUILD_ADDITIONAL_ARGS]) {
        nativeImageCommand.addAll(config[CONFIG_BUILD_ADDITIONAL_ARGS])
    }
}
nativeImageCommand.addAll(executable)
executeCommand(nativeImageCommand)


/**
 * Executes a command
 *
 * @param command
 */
void executeCommand(def command) {
    logger.fine({ String.valueOf(command) })
    Process process = command.execute()
    process.consumeProcessOutput(System.out, System.err)
    process.waitFor()
}

/**
 * Converts the YAML configuration into a flat map
 *
 * @param prefix
 * @param map
 * @return
 */
Map getFlattenedMap(String prefix, Map map) {
    Map flatMap = [:]

    map.each { key, value ->
        if (value instanceof Map) {
            flatMap.putAll(getFlattenedMap(prefix + key + '.', value))
        } else {
            flatMap[(prefix + key)] = value
        }
    }

    return flatMap
}