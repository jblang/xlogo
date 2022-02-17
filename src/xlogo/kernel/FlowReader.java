package xlogo.kernel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class FlowReader extends Flow {
    BufferedReader bfr;

    FlowReader(Flow flow) {
        super(flow);
    }

    boolean isReader() {
        return true;
    }

    String readLine() throws IOException {
        if (null == bfr) bfr = new BufferedReader(new FileReader(getPath()));
        String line = bfr.readLine();
        return line;
    }

    int readChar() throws IOException {
        if (null == bfr) bfr = new BufferedReader(new FileReader(getPath()));
        int character = bfr.read();
        return character;
    }

    int isReadable() throws IOException {
        if (null == bfr) bfr = new BufferedReader(new FileReader(getPath()));
        bfr.mark(2);
        int id = bfr.read();
        bfr.reset();
        return id;
    }

    void close() throws IOException {
        if (null != bfr) bfr.close();
    }
}
