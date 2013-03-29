# javac-preprocessor

Missing preprocessor SPI for javac (1.6+).

## Quickstart

### Creating a preprocessor

1. Include com.github.shyiko.javac-preprocessor:javac-preprocessor-spi:LATEST_VERSION dependency.
2. Create implementation(s) of javac.preprocessor.spi.Preprocessor.
3. List implementation(s) from the previous step inside META-INF/services/javac.preprocessor.spi.Preprocessor file.
4. Assemble the jar (including com.github.shyiko.javac-preprocessor:javac-preprocessor-loader:LATEST_VERSION with transitive dependencies)

### Hooking preprocessor into the javac

Add `-J-Xbootclasspath/p:<location of preprocessor jar.jar>:$JAVA_HOME/lib/tools.jar` to the list of command line parameters.

## Example

See [sample-maven-project](https://github.com/shyiko/javac-preprocessor/tree/master/supplement/string-interpolation-example).

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)