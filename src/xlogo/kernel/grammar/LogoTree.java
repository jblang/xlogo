package xlogo.kernel.grammar;

import java.util.Vector;

public class LogoTree {
	private Vector <LogoTree> children;
	private LogoTree parent;
	private LogoType value;
	private boolean isRoot=false;
	private boolean isProcedure=false;
	private boolean isPrimitive=false;
	private boolean isLeaf=false;
	LogoTree(){
		children=new Vector<LogoTree>();
	}
	protected void setParent(LogoTree lt){
		this.parent=lt;
	}
	protected LogoTree  getParent(){
		return parent;
	}
	protected boolean isRoot(){
		return isRoot;
	}
	
	protected void addChild(LogoTree child){
		children.add(child);
	}
	protected void setValue(LogoType value) {
		this.value=value;
	}
	protected LogoType getValue(){
		return value;
	}
	protected boolean isLeaf(){
		return isLeaf;
	}
	LogoType evaluate(){
		Vector<LogoType> args=new Vector<LogoType>();
		for(int i=0;i<children.size();i++){
			LogoTree child=children.get(i);
			if(child.isLeaf())  args.add(child.getValue()); 
			else args.add(child.evaluate());
		}
		return null;
	}
}
