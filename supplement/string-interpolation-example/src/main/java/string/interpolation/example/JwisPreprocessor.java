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
package string.interpolation.example;

import javac.preprocessor.spi.Preprocessor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Preprocessor which replaces all #{expression} within *.jwis files.
 *
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class JwisPreprocessor implements Preprocessor {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("#\\{([^}]+)\\}" );

    @Override
    public String[] supportedExtensions() {
        return new String[] {"jwis"};
    }

    @Override
    public CharSequence transform(String fileName, CharSequence payload) throws IOException {
        StringBuffer result = new StringBuffer();
        Matcher matcher = EXPRESSION_PATTERN.matcher(payload);
        while (matcher.find()) {
            matcher.appendReplacement(result, "\" + " + matcher.group(1) + " + \"");
        }
        matcher.appendTail(result);
        return result;
    }

}