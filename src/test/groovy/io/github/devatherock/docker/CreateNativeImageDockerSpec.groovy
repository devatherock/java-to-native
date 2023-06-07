package io.github.devatherock.docker

import java.nio.file.Files
import java.nio.file.Paths

import io.github.devatherock.util.ProcessUtil

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Test class to test the built docker images
 */
class CreateNativeImageDockerSpec extends Specification {

    @Shared
    def config = [
            'drone': [
                    'envPrefix': 'PLUGIN_'
            ],
            'vela' : [
                    'envPrefix': 'PARAMETER_'
            ]
    ]

    void setupSpec() {
        System.setProperty('java.util.logging.SimpleFormatter.format', '%5$s%n')
    
        ProcessUtil.executeCommand('docker pull devatherock/java-to-native:latest')
        ProcessUtil.executeCommand([
            'docker', 'run', '--rm',
               '-v', "${System.properties['user.dir']}:/work",
               '-w', '/work',
               '-e', 'PARAMETER_SCRIPT_PATH=/work/src/test/resources/Hello.groovy',
               '-e', 'PARAMETER_OUTPUT_FILE=/work/build/Hello.jar',
               '-e', 'PARAMETER_STATIC_COMPILE=true',
               "devatherock/${System.env.SCRIPTJAR_IMAGE ?: 'vela-groovy-script-to-jar'}:${System.env.SCRIPTJAR_VERSION ?: '0.6.2'}"
       ])
    }

    void cleanup() {
        Files.deleteIfExists(Paths.get("${System.properties['user.dir']}/Hello"))
    }

    void cleanupSpec() {
        Files.deleteIfExists(Paths.get("${System.properties['user.dir']}/build/Hello.jar"))
    }

    @Unroll
    void 'test convert groovy script to native - ci: #ci'() {
        when:
        def dockerOutput = ProcessUtil.executeCommand([
            'docker', 'run', '--rm',
               '-v', "${System.properties['user.dir']}:/work",
               '-w', '/work',
               '-e', "${config[ci].envPrefix}JAR_NAME=/work/build/Hello.jar",
               '-e', "${config[ci].envPrefix}CONFIG_FILE=/work/src/test/resources/graal.yml",
               'devatherock/java-to-native:latest'
        ])
               
        then:
        dockerOutput[0] == 0

        when:
        def output = ProcessUtil.executeCommand([
            "${System.properties['user.dir']}/Hello", '-n', 'World'])

        then:
        output[0] == 0
        output[1].contains('Hello World')

        where:
        ci << ['drone', 'vela']
    }
}
