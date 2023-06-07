docker_tag=latest
scriptjar_image=scriptjar
scriptjar_version=2.0.0

clean:
	./gradlew clean
test:
	SCRIPTJAR_IMAGE=$(scriptjar_image) SCRIPTJAR_VERSION=$(scriptjar_version) ./gradlew spotlessApply test -x compileGroovy -Dtest.logs=true $(additional_gradle_args)
jar-build:
	docker run --rm \
	-v $(CURDIR):/work \
	-w=/work \
	-e PARAMETER_SCRIPT_PATH=CreateNativeImage.groovy \
	devatherock/$(scriptjar_image):$(scriptjar_version)
docker-build:
	docker build -t devatherock/java-to-native:$(docker_tag) .