package com.sun.tools.javac.main;

import com.sun.tools.javac.util.Options;

import javax.lang.model.SourceVersion;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class JDK6SourceFileOption extends JavacOption.HiddenOption {

    private Main main;
    private Set<String> extensions;

    public JDK6SourceFileOption(Main main, Set<String> additionalExtensions) {
        super(OptionName.SOURCEFILE);
        this.main = main;
        this.extensions = new HashSet<String>(additionalExtensions);
        this.extensions.add("java");
    }

    private boolean endsWithSupportedExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex > -1 && extensions.contains(filename.substring(dotIndex + 1));
    }

    public boolean matches(String name) {
        return endsWithSupportedExtension(name) || SourceVersion.isName(name);
    }

    public boolean process(Options options, String name) {
        if (endsWithSupportedExtension(name)) {
            File localFile = new File(name);
            if (!localFile.exists()) {
                main.error("err.file.not.found", localFile);
                return true;
            }
            if (!localFile.isFile()) {
                main.error("err.file.not.file", localFile);
                return true;
            }
            main.filenames.append(localFile);
        } else {
            main.classnames.append(name);
        }
        return false;
    }
}
