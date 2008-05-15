package xlogo.kernel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MyFlowReader extends MyFlow {
	BufferedReader bfr;
	boolean isReader(){
		return true;
	}

	MyFlowReader(MyFlow flow){
		super(flow);
	}
	String readLine() throws FileNotFoundException,IOException{
		if (null==bfr) bfr = new BufferedReader(new FileReader(getPath()));
		String line = bfr.readLine();
		return line;
	}
	int readChar() throws FileNotFoundException,IOException{
		if (null==bfr) bfr = new BufferedReader(new FileReader(getPath()));
		int character = bfr.read();
		return character;
	}
	int isReadable() throws FileNotFoundException,IOException{
		if (null==bfr) bfr = new BufferedReader(new FileReader(getPath()));
		bfr.mark(2);
		int id=bfr.read();
		bfr.reset();
		return id;
	}
	void close() throws IOException {
		if (null!=bfr) bfr.close();
	}
}
