package xlogo.kernel;

import java.math.BigDecimal;

public class LoopRepeat extends LoopProperties{
	LoopRepeat(BigDecimal counter,BigDecimal end,BigDecimal increment,String instr){
		super(counter,end,increment,instr);
	}
	protected boolean isRepeat(){
		return true;
	}
	protected boolean isForEver(){
		return false;
	}
}
