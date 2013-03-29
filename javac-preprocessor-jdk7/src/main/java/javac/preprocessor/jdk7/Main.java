package javac.preprocessor.jdk7;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JDK7SourceFileOption;
import com.sun.tools.javac.main.JavacOption;
import com.sun.tools.javac.main.OptionName;
import com.sun.tools.javac.util.Context;
import javac.preprocessor.spi.Preprocessor;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] paramArrayOfString) throws Exception {
        if ((paramArrayOfString.length > 0) && (paramArrayOfString[0].equals("-Xjdb"))) {
            String[] arrayOfString = new String[paramArrayOfString.length + 2];
            Class localClass = Class.forName("com.sun.tools.example.debug.tty.TTY");
            Method localMethod = localClass.getDeclaredMethod("main", new Class[]{paramArrayOfString.getClass()});
            localMethod.setAccessible(true);
            System.arraycopy(paramArrayOfString, 1, arrayOfString, 3, paramArrayOfString.length - 1);
            arrayOfString[0] = "-connect";
            arrayOfString[1] = "com.sun.jdi.CommandLineLaunch:options=-esa -ea:com.sun.tools...";
            arrayOfString[2] = "com.sun.tools.javac.Main";
            localMethod.invoke(null, new Object[]{arrayOfString});
        } else {
            System.exit(compile(paramArrayOfString));
        }
    }

    public static int compile(String[] paramArrayOfString) {
        com.sun.tools.javac.main.Main localMain = new com.sun.tools.javac.main.Main("javac");
        return compile(localMain, paramArrayOfString);
    }

    public static int compile(String[] paramArrayOfString, PrintWriter paramPrintWriter) {
        com.sun.tools.javac.main.Main localMain = new com.sun.tools.javac.main.Main("javac", paramPrintWriter);
        return compile(localMain, paramArrayOfString);
    }

    private static int compile(com.sun.tools.javac.main.Main localMain, String[] paramArrayOfString) {
        final Context localContext = new Context();
        final Map<String, List<Preprocessor>> preprocessorsGroupedByExtension = getPreprocessorsGroupedByExtension();
        extendSourceTypesSupport(localMain, preprocessorsGroupedByExtension.keySet());
        if (Arrays.asList(paramArrayOfString).contains("-verbose")) {
            if (preprocessorsGroupedByExtension.isEmpty()) {
                System.out.println("No instances of " + Preprocessor.class.getName() + " have been found.");
            } else {
                for (Map.Entry<String, List<Preprocessor>> entry : preprocessorsGroupedByExtension.entrySet()) {
                    System.out.println("*." + entry.getKey() + " files are going to be preprocessed with " +
                            entry.getValue());
                }
            }
        }
        localContext.put(JavaFileManager.class, new Context.Factory<JavaFileManager>() {

            @Override
            public JavaFileManager make(Context context) {
                return new Main.FileManager(localContext, preprocessorsGroupedByExtension);
            }
        });
        return localMain.compile(paramArrayOfString, localContext);
    }

    private static void extendSourceTypesSupport(com.sun.tools.javac.main.Main localMain, Set<String> extensions) {
        Field recognizedOptionsField;
        try {
            recognizedOptionsField = localMain.getClass().getDeclaredField("recognizedOptions");
        } catch (NoSuchFieldException e) {
            throw new AssertionError(e.getMessage());
        }
        recognizedOptionsField.setAccessible(true);
        JavacOption.Option[] recognizedOptions;
        try {
            recognizedOptions = (JavacOption.Option[]) recognizedOptionsField.get(localMain);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        for (int i = recognizedOptions.length - 1; i > -1; i--) {
            JavacOption.Option recognizedOption = recognizedOptions[i];
            if (recognizedOption.getName() == OptionName.SOURCEFILE) {
                recognizedOptions[i] = new JDK7SourceFileOption(localMain, extensions);
                break;
            }
        }
    }

    private static Map<String, List<Preprocessor>> getPreprocessorsGroupedByExtension() {
        ServiceLoader<Preprocessor> preprocessors = ServiceLoader.load(Preprocessor.class);
        Map<String, List<Preprocessor>> preprocessorsByExtension = new HashMap<String, List<Preprocessor>>();
        for (Preprocessor preprocessor : preprocessors) {
            for (String extension : preprocessor.supportedExtensions()) {
                List<Preprocessor> group = preprocessorsByExtension.get(extension);
                if (group == null) {
                    preprocessorsByExtension.put(extension, group = new LinkedList<Preprocessor>());
                }
                group.add(preprocessor);
            }
        }
        return preprocessorsByExtension;
    }

    private static class FileManager extends JavacFileManager {

        private final Map<String, List<Preprocessor>> preprocessors;

        private FileManager(Context context, Map<String, List<Preprocessor>> preprocessors) {
            super(context, true, null);
            this.preprocessors = preprocessors;
        }

        @Override
        public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> iterable) {
            return decorate(super.getJavaFileObjectsFromFiles(iterable));
        }

        private Iterable<? extends JavaFileObject> decorate(Iterable<? extends JavaFileObject> iterable) {
            Set<String> supportedExtensions = preprocessors.keySet();
            List<JavaFileObject> result = new ArrayList<JavaFileObject>();
            for (JavaFileObject javaFileObject : iterable) {
                String name = javaFileObject.getName(),
                        extension = name.substring(name.lastIndexOf(".") + 1);
                result.add(supportedExtensions.contains(extension) ?
                        new FileObject(javaFileObject, extension, preprocessors.get(extension)) : javaFileObject);
            }
            return result;
        }
    }

    private static class FileObject extends SimpleJavaFileObject {

        private JavaFileObject javaFileObject;
        private String extension;
        private Iterable<? extends Preprocessor> preprocessors;

        private FileObject(JavaFileObject javaFileObject, String extension,
                           Iterable<? extends Preprocessor> preprocessors) {
            super(javaFileObject.toUri(), Kind.SOURCE);
            this.javaFileObject = javaFileObject;
            this.extension = extension;
            this.preprocessors = preprocessors;
        }

        @Override
        public boolean isNameCompatible(String simpleName, Kind kind) {
            String baseName = simpleName + "." + extension;
            return kind.equals(getKind())
                    && (baseName.equals(toUri().getPath())
                    || toUri().getPath().endsWith("/" + baseName));
        }

        @Override
        public long getLastModified() {
            return javaFileObject.getLastModified();
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            CharSequence result = javaFileObject.getCharContent(ignoreEncodingErrors);
            for (Preprocessor preprocessor : preprocessors) {
                result = preprocessor.transform(javaFileObject.getName(), result);
            }
            return result;
        }
    }
}