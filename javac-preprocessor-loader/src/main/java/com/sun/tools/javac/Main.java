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
package com.sun.tools.javac;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.6") || version.startsWith("1.7")) {
            Class.forName("javac.preprocessor.jdk" + version.charAt(2) + ".Main").
                    getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});
        } else {
            throw new java.lang.Error("Unsupported JVM (version " + version + ")");
        }
    }
}