def arguments = getProperty('args') as String[]
String name = arguments && arguments.length >= 2 && (arguments[0] == '-n' || arguments[0] == '--name') ?
    arguments[1] : null

println("Hello ${name}")