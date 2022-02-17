/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
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
    private Process p;
    /**
     * The temporary folder which contains all files to start XLogo
     */
    private File tmpFolder = null;
    private File[] files;
    private File mainJar;
    private int memory = 64;

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
        if (mem > 63) memory = mem;

        // extract application in the java.io.tmp directory
        extractApplication();
        try {
            String[] command = new String[7 + args.length];
            int i = 0;
            command[i++] = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            command[i++] = "-Xmx" + memory + "m";
            // Workaround for Java3D incompatibility with Java 9+
            // see: https://jogamp.org/bugzilla/show_bug.cgi?id=1317#c9
            command[i++] = "--add-exports=java.base/java.lang=ALL-UNNAMED";
            command[i++] = "--add-exports=java.desktop/sun.awt=ALL-UNNAMED";
            command[i++] = "--add-exports=java.desktop/sun.java2d=ALL-UNNAMED";
            command[i++] = "-jar";
            command[i++] = mainJar.getAbsolutePath();
            for (String arg: args) {
                command[i++] = arg;
            }
            System.out.println("Starting XLogo");
            System.out.println(String.join(" ", command) + "\n\n");
            p = Runtime.getRuntime().exec(command);
            // Recept Message from the Process
            new Thread(() -> {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
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
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
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
            p.waitFor();
        } catch (InterruptedException | IOException | SecurityException e) {
            e.printStackTrace();
        }
        // Delete tmp files
        for (int i = 0; i < files.length; i++) {
            if (null != files[i]) files[i].delete();
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
            for (int i = 0; i < files.length; i++) {
                try {
                    if (files[i].getName().startsWith("xlogo-nested")) {
                        long fileTime = files[i].lastModified();
                        long time = Calendar.getInstance().getTimeInMillis();
                        // Delete file if it's more than 24 hours old
                        if (time - fileTime > 24 * 3600 * 1000) {
                            if (files[i].isDirectory()) deleteDirectory(files[i]);
                            files[i].delete();

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
            b = copyFile(src, file);
            System.out.println("Copying " + jar + " - success: " + b);
        }
    }

    /**
     * This method reads the Memory needed by XLogo in the file .xlogo
     */
    private void readConfig() {
        try {
            // Try to read XML format (new config file)
            FileInputStream fr = new FileInputStream(System.getProperty("user.home") + File.separator + ".xlogo");
            BufferedInputStream bis = new BufferedInputStream(fr);
            InputStreamReader isr = new InputStreamReader(bis, StandardCharsets.UTF_8);
            try {
                XMLReader saxReader = XMLReaderFactory.createXMLReader();
                saxReader.setContentHandler(new MemoryContentHandler());
                saxReader.parse(new InputSource(isr));
            } catch (SAXException e) {
                // Read the old config file format
                String s = "";
                FileReader ifr = new FileReader(System.getProperty("user.home") + File.separator + ".xlogo");
                while (ifr.ready()) {
                    char[] b = new char[64];
                    int i = ifr.read(b);
                    if (i == -1) break;
                    s += new String(b);
                }
                StringTokenizer st = new StringTokenizer(s, "\n");
                while (st.hasMoreTokens()) {
                    String element = st.nextToken();
                    if (element.equals("# memoire")) {
                        element = st.nextToken();
                        memory = Integer.parseInt(element);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No File \".xlogo\". Will create one...");
        } catch (IOException e) {
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
        // Declaration des flux
        java.io.FileOutputStream destinationFile = null;
        try {
            // Création du fichier :
            destination.createNewFile();
            // Ouverture des flux

            destinationFile = new java.io.FileOutputStream(destination);
            // Lecture par segment de 0.5Mo
            byte[] buffer = new byte[512 * 1024];
            int bytesRead;
            while ((bytesRead = src.read(buffer)) != -1) {
                destinationFile.write(buffer, 0, bytesRead);
            }
            // Copie réussie
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Quoi qu'il arrive, on ferme les flux
            try {
                src.close();
            } catch (Exception e) {
            }
            try {
                destinationFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        result &= deleteDirectory(files[i]);
                    } else {
                        result &= files[i].delete();
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
                } catch (NumberFormatException e) {
                }
            }
        }
        return memory;
    }

    /** this class is a very basic XML parser
     **/
    class MemoryContentHandler implements ContentHandler {
        //   private Locator locator;

        public MemoryContentHandler() {
            super();
            // We define default Locator
            //          locator = new LocatorImpl();
        }

        public void setDocumentLocator(Locator value) {
            //        locator =  value;
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startPrefixMapping(String prefix, String URI) throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        // Open new Tag
        public void startElement(String nameSpaceURI, String localName, String rawName, Attributes attributs) throws SAXException {
            analyzeTag(localName, attributs);
	/*                   System.out.println("Ouverture de la balise: "+localName);
	                  for (int index = 0; index < attributs.getLength(); index++) { // on parcourt la liste des attributs
	                          System.out.println("     - " +  attributs.getLocalName(index) + " = " + attributs.getValue(index));
	                  }*/
        }

        // Close tag
        public void endElement(String nameSpaceURI, String localName, String rawName) throws SAXException {
	       /*           System.out.print("Fermeture de la balise : " + localName);

	                  if ( ! "".equals(nameSpaceURI)) { // name space non null
	                          System.out.print("appartenant a l'espace de nommage : " + localName);
	                  }

	                  System.out.println();*/
        }

        public void characters(char[] ch, int start, int end) throws SAXException {
        }

        public void ignorableWhitespace(char[] ch, int start, int end) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void skippedEntity(String arg0) throws SAXException {
        }

        private void analyzeTag(String tag, Attributes attributs) {
            if (tag.equals("memory")) {
                memory = Integer.parseInt(attributs.getValue(0));
            }
        }

    }
}