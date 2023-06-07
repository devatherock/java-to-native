docker_tag=latest

clean:
	./gradlew clean
test:
	./gradlew spotlessApply test
jar-build:
	docker run --rm \
	-v $(CURDIR):/work \
	-w=/work \
	-e PARAMETER_SCRIPT_PATH=CreateNativeImage.groovy \
	devatherock/vela-groovy-script-to-jar:0.6.2
docker-build:
	docker build -t devatherock/java-to-native:$(docker_tag) .