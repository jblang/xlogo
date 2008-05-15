package xlogo.kernel;

public class MyFlow {
	private String path;
	private int id;
	private boolean finished;
	MyFlow(){}
	String getPath(){
		return path;
	}
	void setPath(String p){
		path=p;
	}
	int getId(){
		return id;
	}
	void setId(int i){
		id=i;
	}
	boolean isFinished(){
		return finished;
	}
	void setFinished(boolean b){
		finished=b;
	}
	boolean isReader(){
		return false;
	}
	boolean isWriter(){
		return false;
	}
}
