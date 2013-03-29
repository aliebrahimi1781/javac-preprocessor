/*
 * Copyright 2013 Stanley Shyiko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javac.preprocessor.spi;

import java.io.IOException;
import java.lang.String;

/**
 * Java Compiler preprocessor contract.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface Preprocessor {

    /**
     * @return supported file extensions (e.g. "java", "coffee")
     */
    String[] supportedExtensions();

    /**
     * @param fileName name of the file
     * @param payload file content
     * @return transformed file content
     * @throws IOException if anything goes wrong during payload processing
     */
    CharSequence transform(String fileName, CharSequence payload) throws IOException;
}