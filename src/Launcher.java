/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

import xlogo.Config;

import java.beans.XMLDecoder;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.jar.JarFile;

/**
 * @author loic
 *
 * This class extracts the file tmp_xlogo.jar from the main archive
 * in the temporary file's directory
 * and then launch the command:
 * <br>
 * java -D-jar -Xmx64m -Djava.library.path=path_to_tmp -cp path_to_tmp tmp_xlogo.jar<br>
 * <br>
 * XLogo executes with a predefined heap size for the Virtual Machine (in the example 64M)
 */
public class Launcher {
    /**
     * The process which contains the XLogo application
     */
    private Process process;
    /**
     * The temporary folder which contains all files to start XLogo
     */
    private File tmpFolder = null;
    private File[] files;
    private File mainJar;
    private int memory = 256;

    /**
     * Constructs Launcher with arguments args<br>
     * @param args The arguments for the class Launcher (path to file in lgo format)
     */
    Launcher(String[] args) {
        // Look for old files from XLogo crash
        cleanTmp();
        // Look from file .xlogo for the memory allocated to the JVM heap size
        readConfig();
        // Look from command line for the memory allocated to the JVM heap size
        // And overwrite the existing value
        int mem = readMemoryFromCommandLine(args);
        if (mem > memory) memory = mem;

        // extract application in the java.io.tmp directory
        extractApplication();
        try {
            var jvm = Paths.get(System.getProperty("java.home"), "bin", "java").toFile();
            var xmx = "-Xmx" + memory + "m";
            var myArgs = new String[] {
                    jvm.getAbsolutePath(),
                    xmx,
                    "--add-exports=java.base/java.lang=ALL-UNNAMED",
                    "--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
                    "--add-exports=java.desktop/sun.java2d=ALL-UNNAMED",
                    "-jar",
                    mainJar.getAbsolutePath()
            };
            var command = new String[myArgs.length + args.length];
            System.arraycopy(myArgs, 0, command, 0, myArgs.length);
            System.arraycopy(args, 0, command, myArgs.length, args.length);
            System.out.println("Starting XLogo");
            System.out.println(String.join(" ", command) + "\n\n");
            process = Runtime.getRuntime().exec(command);
            // Recept Message from the Process
            new Thread(() -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Traitement du flux de sortie de l'application si besoin est
                            System.out.println(line);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }).start();
            // Recept Error Message from the Process
            new Thread(() -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Traitement du flux de sortie de l'application si besoin est
                            System.out.println(line);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }).start();
            process.waitFor();
        } catch (InterruptedException | IOException | SecurityException e) {
            e.printStackTrace();
        }
        // Delete tmp files
        for (File file : files) {
            if (null != file) file.delete();
        }
        tmpFolder.delete();
        System.out.println("Closing XLogo. Cleaning tmp file");
    }

    /**
     * Main method
     * @param args The path toward "lgo" files
     */
    public static void main(String[] args) {
        new Launcher(args);
    }

    /**
     * This method checks for unused old XLogo files in temporary directory<br>
     * If it found files older than 24 hours with the prefix tmp_xlogo, these files are deleted.
     */
    private void cleanTmp() {
        String path = System.getProperty("java.io.tmpdir");
        File f = new File(path);
        File[] files = f.listFiles();
        if (null != files) {
            for (File file : files) {
                try {
                    if (file.getName().startsWith("xlogo-nested")) {
                        long fileTime = file.lastModified();
                        long time = Calendar.getInstance().getTimeInMillis();
                        // Delete file if it's more than 24 hours old
                        if (time - fileTime > 24 * 3600 * 1000) {
                            if (file.isDirectory()) deleteDirectory(file);
                            file.delete();

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method extracts the file xlogo-nested.jar from the archive and copy it into the temporary directory.
     */
    private void extractApplication() {
        // Create in the "java.io.tmpdir" a directory called xlogo-nested
        int i = 0;
        String tmpPath = System.getProperty("java.io.tmpdir") + File.separator + "xlogo-nested";
        while (true) {
            tmpFolder = new File(tmpPath + i);
            if (!tmpFolder.exists()) break;
            else i++;
        }
        boolean b = tmpFolder.mkdir();
        System.out.println("Creating temp directory - success: " + b);

        var jars = new ArrayList<String>();
        var myJar = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            var files = new JarFile(myJar).entries();
            while (files.hasMoreElements()) {
                var file = files.nextElement().getName();
                if (file.endsWith(".jar")) {
                    jars.add(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        i = 0;
        files = new File[jars.size()];
        for (String jar: jars) {
            InputStream src = Launcher.class.getResourceAsStream(jar);
            File file = new File(tmpFolder.getAbsolutePath() + File.separator + jar);
            if (jar.equals("xlogo-main.jar")) mainJar = file;
            files[i++] = file;
            b = copyFile(Objects.requireNonNull(src), file);
            System.out.println("Copying " + jar + " - success: " + b);
        }
    }

    /**
     * This method reads the Memory needed by XLogo in the file .xlogo
     */
    private void readConfig() {
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.home") + File.separator + ".xlogo");
            XMLDecoder dec = new XMLDecoder(fis);
            Config config = (Config) dec.readObject();
            memory = config.getMemoryLimit();
            dec.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("No File \".xlogo\". Will create one...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method copies the file xlogo-main.jar from the archive to the file Destination.
     * @param destination The output file
     * @return true if success, false otherwise
     */
    private boolean copyFile(InputStream src, File destination) {
        boolean result = false;
        java.io.FileOutputStream destinationFile;
        try (src) {
            destination.createNewFile();
            destinationFile = new FileOutputStream(destination);
            byte[] buffer = new byte[512 * 1024];
            int bytesRead;
            while ((bytesRead = src.read(buffer)) != -1) {
                destinationFile.write(buffer, 0, bytesRead);
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (result);
    }

    /**
     * Delete a the directory created by Logo in /tmp
     * @param path The Directory path
     * @return true if success
     */
    private boolean deleteDirectory(File path) {
        boolean result = true;
        if (path.exists()) {
            File[] files = path.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        result &= deleteDirectory(file);
                    } else {
                        result &= file.delete();
                    }
                }
            }
        }
        result &= path.delete();
        return (result);
    }

    /**
     * This method returns the memory passed from the command line <br>
     * syntax: java -jar xlogo.jar -memory 128 -lang en file1.lgo
     *
     * @param args All arguments
     * @return The memory in Mb
     */
    private int readMemoryFromCommandLine(String[] args) {
        int memory = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-memory")) {
                try {
                    if (i + 1 < args.length) {
                        memory = Integer.parseInt(args[i + 1]);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return memory;
    }
}