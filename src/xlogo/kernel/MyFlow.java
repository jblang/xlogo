package xlogo.kernel;

public class MyFlow {
	private String path;
	private int id;
	private boolean finished;
	MyFlow(int id,String path,boolean finished){
		this.id=id;
		this.path=path;
		this.finished=finished;
	}
	MyFlow(MyFlow flow){
		this.id=flow.getId();
		this.path=flow.getPath();
		this.finished=flow.isFinished();
	}
	
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
