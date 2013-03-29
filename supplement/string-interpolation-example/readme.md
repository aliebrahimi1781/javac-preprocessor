# string-interpolation-example

## Overview

[src/main/java/string/interpolation/example/JwisPreprocessor.java](https://github.com/shyiko/javac-preprocessor/tree/master/supplement/string-interpolation-example/src/main/java/string/interpolation/example/JwisPreprocessor.java) adds support for *.jwis (java with interpolated strings) files. Each `"some string #{expression} some another string"` within such files are going to be replaced with `"some string " + expression + " some another string"`.

## Usage

    mvn clean package
    mkdir -p target/output
    javac -J-Xbootclasspath/p:target/string-interpolation-example.jar:$JAVA_HOME/lib/tools.jar -d target/output \
        src/test/resources/Echo.java \
        src/test/resources/EchoInJava.java \
        src/test/resources/EchoInJwis.jwis
    java -cp target/output Echo 42
        # output:
        # echo in java: #{java.util.Arrays.toString(args)}
        # echo in jwis: [42]

> javac-preprocessor may need to be "mvn clean install"ed before building string-interpolation-example.