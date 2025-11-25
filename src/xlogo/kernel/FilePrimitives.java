/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * File I/O primitives: loading, saving, reading/writing file streams.
 */
public class FilePrimitives extends PrimitiveGroup {

    public FilePrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("file.appendlineflow", 2, false, this::fileAppendLine),
            new Primitive("file.closeflow", 1, false, this::closeFile),
            new Primitive("file.directory", 0, false, this::getDirectory),
            new Primitive("file.endflow?", 1, false, this::isEndOfFile),
            new Primitive("file.files", 0, false, this::getFiles),
            new Primitive("file.listflow", 0, false, this::getOpenFiles),
            new Primitive("file.load", 1, false, this::load),
            new Primitive("file.openflow", 2, false, this::openFile),
            new Primitive("file.readcharflow", 1, false, this::fileReadChar),
            new Primitive("file.readlineflow", 1, false, this::fileReadLine),
            new Primitive("file.save", 2, false, this::save),
            new Primitive("file.saved", 1, false, this::saveAll),
            new Primitive("file.setdirectory", 1, false, this::setDirectory),
            new Primitive("file.writelineflow", 2, false, this::fileWriteLine)
        );
    }

    void fileAppendLine(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            String list = getFinalList(param.get(1));
            if (index == -1) throw new LogoException(app, Logo.getString("flow.error.unknown") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowWriter flowWriter;
            // If the flow is a readable flow, throw an error
            if (flow.isReader()) throw new LogoException(app, Logo.getString("flow.error.write"));
            // Else if the flow is a writable flow, convert to FlowWriter
            else if (flow.isWriter()) flowWriter = (FlowWriter) flow;
            // Else the flow isn't defined yet, initialize
            else flowWriter = new FlowWriter(flow);
            // Write the line
            flowWriter.append(Utils.unescapeString(list));
            kernel.flows.set(index, flowWriter);
        } catch (IOException | LogoException ignored) {
        }
    }

    void closeFile(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.getString("flow.error.unknown") + " " + ident);
            Flow flow = kernel.flows.get(index);
            // If the flow is a readable flow
            if (flow.isReader()) ((FlowReader) flow).close();
            // Else if it's a writable flow
            else if (flow.isWriter()) ((FlowWriter) flow).close();
            kernel.flows.remove(index);
        } catch (IOException | LogoException ignored) {
        }
    }

    void openFile(Stack<String> param) {
        try {
            String word = getWord(param.get(1));
            if (null == word) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            String path = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + Utils.unescapeString(word);
            int ident = kernel.getCalculator().getInteger(param.get(0));
            if (kernel.flows.search(ident) == -1) kernel.flows.add(new Flow(ident, path, false));
            else throw new LogoException(app, ident + " " + Logo.getString("flow.error.open"));
        } catch (LogoException ignored) {
        }
    }

    void isEndOfFile(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.getString("flow.error.unknown") + " " + ident);
            else {
                Flow flow = kernel.flows.get(index);
                FlowReader flowReader = null;
                // If the flow isn't defined yet, initialize
                if (!flow.isWriter() && !flow.isReader()) {
                    flowReader = new FlowReader(flow);
                } else if (flow.isReader()) flowReader = (FlowReader) flow;
                if (null != flowReader) {
                    if (flow.isFinished()) {
                        setReturnValue(true);
                        pushResult(Logo.getString("control.true"));
                    } else {
                        int read = flowReader.isReadable();
                        setReturnValue(true);
                        if (read == -1) {
                            pushResult(Logo.getString("control.true"));
                            flow.setFinished(true);
                        } else {
                            pushResult(Logo.getString("control.false"));
                        }
                    }
                } else throw new LogoException(app, Logo.getString("flow.error.read"));
            }
        } catch (IOException | LogoException ignored) {
        }
    }

    void fileWriteLine(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            String list = getFinalList(param.get(1));
            if (index == -1) throw new LogoException(app, Logo.getString("flow.error.unknown") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowWriter flowWriter;
            // If the flow is a readable flow, throw an error
            if (flow.isReader()) throw new LogoException(app, Logo.getString("flow.error.write"));
            // Else if the flow is a writable flow, convert to FlowWriter
            else if (flow.isWriter()) flowWriter = (FlowWriter) flow;
            // Else the flow isn't defined yet, initialize
            else flowWriter = new FlowWriter(flow);
            // Write the line
            flowWriter.write(Utils.unescapeString(list));
            kernel.flows.set(index, flowWriter);
        } catch (IOException | LogoException ignored) {
        }
    }

    void fileReadChar(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.getString("flow.error.unknown") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowReader flowReader;
            // If the flow is a writable flow, throw error
            if (flow.isWriter()) throw new LogoException(app, Logo.getString("flow.error.read"));
            // else if the flow is reader, convert to FlowReader
            else if (flow.isReader()) {
                flowReader = ((FlowReader) flow);
            }
            // else the flow isn't yet defined, initialize
            else flowReader = new FlowReader(flow);

            if (flowReader.isFinished())
                throw new LogoException(app, Logo.getString("flow.error.eof") + " " + ident);

            int character = ((FlowReader) flow).readChar();
            if (character == -1) {
                flow.setFinished(true);
                throw new LogoException(app, Logo.getString("flow.error.eof") + " " + ident);
            }
            setReturnValue(true);
            String car = String.valueOf(character);
            if (car.equals("\\")) car = "\\\\";
            pushResult(car);
            kernel.flows.set(index, flowReader);
        } catch (FileNotFoundException e1) {
            try {
                throw new LogoException(app, Logo.getString("file.error.read"));
            } catch (LogoException ignored) {
            }
        } catch (IOException | LogoException ignored) {
        }
    }

    void fileReadLine(Stack<String> param) {
        try {
            int ident = kernel.getCalculator().getInteger(param.get(0));
            int index = kernel.flows.search(ident);
            if (index == -1) throw new LogoException(app, Logo.getString("flow.error.unknown") + " " + ident);
            Flow flow = kernel.flows.get(index);
            FlowReader flowReader;
            // If the flow is a writable flow, throw error
            if (flow.isWriter()) throw new LogoException(app, Logo.getString("flow.error.read"));
            // else if the flow is a readable flow, convert to FlowReader
            else if (flow.isReader()) {
                flowReader = ((FlowReader) flow);
            }
            // else the flow isn't yet defined, initialize
            else flowReader = new FlowReader(flow);

            if (flowReader.isFinished())
                throw new LogoException(app, Logo.getString("flow.error.eof") + " " + ident);
            // Reading line
            String line = flowReader.readLine();
            if (null == line) {
                flow.setFinished(true);
                throw new LogoException(app, Logo.getString("flow.error.eof") + " " + ident);
            }
            setReturnValue(true);
            pushResult("[ " + Utils.formatCode(line.trim()) + " ] ");
            kernel.flows.set(index, flowReader);
        } catch (FileNotFoundException e1) {
            try {
                throw new LogoException(app, Logo.getString("file.error.read"));
            } catch (LogoException ignored) {
            }
        } catch (IOException | LogoException ignored) {
        }
    }

    void getOpenFiles(Stack<String> param) {
        StringBuilder list = new StringBuilder("[ ");
        for (Flow flow : kernel.flows) {
            list.append("[ ").append(flow.getId()).append(" ").append(flow.getPath()).append(" ] ");
        }
        list.append("] ");
        setReturnValue(true);
        pushResult(list.toString());
    }

    void load(Stack<String> param) {
        try {
            String word = getWord(param.get(0));
            if (null == word) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            String path = Utils.unescapeString(Logo.config.getDefaultFolder()) + File.separator + word;
            try {
                String txt = Utils.readLogoFile(path);
                app.editor.appendText(txt);
            } catch (IOException e1) {
                throw new LogoException(app, Logo.getString("file.error.read"));
            }
            try {
                app.editor.parseProcedures();
            } catch (Exception e) {
                e.printStackTrace();
            }
            app.editor.clearText();
        } catch (LogoException ignored) {
        }
    }

    void saveAll(Stack<String> param) {
        try {
            String word = getWord(param.get(0));
            if (null == word) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            interpreter.saveProcedures(word, null);
        } catch (LogoException ignored) {
        }
    }

    void save(Stack<String> param) {
        try {
            String word = getWord(param.get(0));
            if (null == word) throw new LogoException(app, Logo.getString("interpreter.error.wordRequired"));
            String list = getFinalList(param.get(1));
            StringTokenizer st = new StringTokenizer(list);
            Stack<String> stack = new Stack<>();
            while (st.hasMoreTokens()) stack.push(st.nextToken());
            interpreter.saveProcedures(word, stack);
        } catch (LogoException ignored) {
        }
    }

    void getDirectory(Stack<String> param) {
        setReturnValue(true);
        pushResult("\"" + Logo.config.getDefaultFolder());
    }

    void setDirectory(Stack<String> param) {
        try {
            String word = getWord(param.get(0));
            if (null == word) throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            String path = Utils.unescapeString(word);
            if ((new File(path)).isDirectory() && !path.startsWith("..")) {
                Logo.config.setDefaultFolder(Utils.escapeString(path));
            } else throw new LogoException(app, word + " " + Logo.getString("interpreter.error.invalidDirectory"));
        } catch (LogoException ignored) {
        }
    }

    void getFiles(Stack<String> param) {
        String str = Utils.unescapeString(Logo.config.getDefaultFolder());
        File f = new File(str);
        StringBuilder fileList = new StringBuilder();
        StringBuilder directoryList = new StringBuilder();
        int directoryCount = 0;
        int fileCount = 0;
        String[] list = f.list();
        for (String s : Objects.requireNonNull(list)) {
            if ((new File(str + File.separator + s)).isDirectory()) {
                directoryCount++;
                if (directoryCount % 5 == 0) directoryList.append(s).append("\n");
                else directoryList.append(s).append(" ");
            } else {
                fileCount++;
                if (fileCount % 5 == 0) fileList.append(s).append("\n");
                else fileList.append(s).append(" ");
            }
        }
        String text = "";
        if (!directoryList.toString().isEmpty())
            text += Logo.getString("interpreter.message.folders") + ":\n" + directoryList + "\n";
        if (!fileList.toString().isEmpty())
            text += Logo.getString("interpreter.message.files") + ":\n" + fileList + "\n";
        app.updateHistory("commentaire", text);
    }
}
