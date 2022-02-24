package xlogo.kernel;

import xlogo.utils.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FlowWriter extends Flow {
    BufferedWriter bfw;

    FlowWriter(Flow flow) {
        super(flow);
    }

    void append(String line) throws IOException {
        if (null == bfw) bfw = new BufferedWriter(new FileWriter(getPath(), true));
        PrintWriter pw = new PrintWriter(bfw);
        pw.println(Utils.unescapeString(line));
    }

    void write(String line) throws IOException {
        if (null == bfw) bfw = new BufferedWriter(new FileWriter(getPath()));
        PrintWriter pw = new PrintWriter(bfw);
        pw.println(Utils.unescapeString(line));
    }

    boolean isWriter() {
        return true;
    }

    void close() throws IOException {
        if (null != bfw) bfw.close();
    }
}
