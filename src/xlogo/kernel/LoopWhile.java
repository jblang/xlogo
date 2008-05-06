package xlogo.kernel;

import java.math.BigDecimal;

public class LoopWhile extends LoopProperties{
	
	LoopWhile(BigDecimal counter,BigDecimal end,BigDecimal increment,String instr){
		super(counter,end,increment,instr);
	}
	
	
	
	protected boolean isWhile(){
		return true;
	}
}
