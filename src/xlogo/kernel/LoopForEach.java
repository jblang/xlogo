package xlogo.kernel;

import java.math.BigDecimal;
import java.util.Vector;

public class LoopForEach extends LoopFor{
	private Vector<String> vec;
	/**
	 * Constructor Loop: For
	 * @param counter The beginning integer
	 * @param end The end integer
	 * @param increment The increment between two values
	 * @param instr The instruction to execute between two values
	 * @param var The name of the variable
	 * @param vec The Vec with all value for the variable
	 */
	LoopForEach(BigDecimal counter,BigDecimal end,BigDecimal increment,String instr,String var,Vector<String> vec){
		super(counter,end,increment,instr,var);
		this.vec=vec;
	}
	protected boolean isForEach(){
		return true;
	}
	protected boolean isForEver(){
		return false;
	}
	/**
	 * This method affects the variable counter the correct value 
	 * @param first boolean that indicates if it is the first affectation
	 */
	protected void AffecteVar(boolean first){
		String element=vec.get(getCounter().intValue());
		if (Interprete.locale.containsKey(var)){
			if (first) conserver=true;
			Interprete.locale.put(var, element);
		} 
		else {
			Interprete.locale.put(var,element);
		}
	}
}
