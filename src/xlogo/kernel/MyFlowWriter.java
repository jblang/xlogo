package xlogo.kernel;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import xlogo.utils.Utils;

public class MyFlowWriter extends MyFlow {
	BufferedWriter bfw;
	MyFlowWriter(MyFlow flow){
		super(flow);
	}
	void append(String line) throws FileNotFoundException,IOException{
		if (null == bfw) bfw = new BufferedWriter(new FileWriter(getPath(),true));
		PrintWriter pw = new PrintWriter(bfw);
		pw.println(Utils.SortieTexte(line));
	}

	void write(String line) throws FileNotFoundException,IOException{
		if (null == bfw) bfw = new BufferedWriter(new FileWriter(getPath()));
		PrintWriter pw = new PrintWriter(bfw);
		pw.println(Utils.SortieTexte(line));
	}
	boolean isWriter(){
		return true;
	}
	void close() throws IOException {
		if (null!=bfw) bfw.close();
	}
}
