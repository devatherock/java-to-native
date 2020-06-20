ENABLE_DEBUG=false
if [ "$PLUGIN_DEBUG" = "true" ] || [ "$PARAMETER_DEBUG" = "true" ]; then
  ENABLE_DEBUG=true
fi

MAIN_CLASS=$PLUGIN_MAIN_CLASS
JAR_NAME=$PLUGIN_JAR_NAME
CONFIG_FILE=$PLUGIN_CONFIG_FILE
CLASSPATH=$PLUGIN_CLASSPATH
REFLECTION_PATH=$PLUGIN_REFLECTION_PATH

if [ "$VELA" = "true" ]; then
  if [ ! -z "$PARAMETER_MAIN_CLASS" ]; then
    MAIN_CLASS=$PARAMETER_MAIN_CLASS
  fi

  if [ ! -z "$PARAMETER_JAR_NAME" ]; then
    JAR_NAME=$PARAMETER_JAR_NAME
  fi

  if [ ! -z "$PARAMETER_CONFIG_FILE" ]; then
    CONFIG_FILE=$PARAMETER_CONFIG_FILE
  fi

  if [ ! -z "$PARAMETER_CLASSPATH" ]; then
    CLASSPATH=$PARAMETER_CLASSPATH
  fi

  if [ ! -z "$PARAMETER_REFLECTION_PATH" ]; then
    REFLECTION_PATH=$PARAMETER_REFLECTION_PATH
  fi
fi

if [ ! -z "$MAIN_CLASS" ]; then
   ALL_OPTS="$ALL_OPTS -m $MAIN_CLASS"
fi

if [ ! -z "$JAR_NAME" ]; then
   ALL_OPTS="$ALL_OPTS -j $JAR_NAME"
fi

if [ ! -z "$CONFIG_FILE" ]; then
   ALL_OPTS="$ALL_OPTS -c $CONFIG_FILE"
fi

if [ ! -z "$CLASSPATH" ]; then
   ALL_OPTS="$ALL_OPTS -cp $CLASSPATH"
fi

if [ ! -z "$REFLECTION_PATH" ]; then
   ALL_OPTS="$ALL_OPTS -rp $REFLECTION_PATH"
fi

if [ "$ENABLE_DEBUG" = "true" ]; then
   ALL_OPTS="$ALL_OPTS -d"
fi

java -jar /scripts/CreateNativeImage.jar $ALL_OPTS