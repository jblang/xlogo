/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
package xlogo.kernel;
import java.math.BigDecimal;
/**
 * This class saves all Loop Properties (repeat, while, for) such as increment, end integer ....
 * @author loic
 */
public class LoopProperties {
	/**
	 * Counter: The counter value for the current loop
	 * End: The end value for the loop
	 * Increment the increment between two values
	 */
	private BigDecimal counter,end,increment;
	/**
	 * The Instruction to execute on each iteration
	 */
	String instr;
/**
 * The super constructor for all loops
 * @param counter The beginning integer
 * @param fin The end integer
 * @param increment The increment between two values
 * @param instr The instruction to execute each loop
 */
	LoopProperties(BigDecimal counter,BigDecimal end,BigDecimal increment,String instr){
		this.counter=counter;
		this.end=end;
		this.increment=increment;
		this.instr=instr;
	}
	

	/**
	 * Adds the increment to the variable counter
	 */
	
	protected void incremente(){
		counter=counter.add(increment);
		counter=new BigDecimal(MyCalculator.eraseZero(counter));
	}
	/**
	 * This method returns the Loop Id
	 * @return the Loop Id (TYPE_FOR, TYPE_WHILE...)
	 */
/*	protected int getId(){
		return id;
	}*/
	/**
	 * This method the current value for the counter 
	 * @return The variable counter
	 */
	protected BigDecimal getCounter(){
		return counter;
	}
	/**
	 * This method returns the end Value
	 * @return the end value for the loop
	 */
	protected BigDecimal getEnd(){ 
		return end;
	}
	/**
	 * this method returns the increment for the loop
	 * @return The variable increment
	 */
	protected BigDecimal getIncrement(){
		return increment;
	}
	/**
	 * This method returns the instructions to execute each loop
	 * @return the instruction block
	 */
	protected String getInstr(){
		return instr;	
	}
	/**
	 * This method returns a loop description
	 */
	public String toString(){
		return(counter+" "+end+" "+increment+"\n"+instr+"\n");
	}	

	protected boolean isFor(){
		return false;
	}
	protected boolean isWhile(){
		return false;
	}
	protected boolean isForEach(){
		return false;
	}
	protected boolean isRepeat(){
		return false;
	}
	protected boolean isForEver(){
		return true;
	}
	protected boolean isFillPolygon(){
		return false;
	}
}
