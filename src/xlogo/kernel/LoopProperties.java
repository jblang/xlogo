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
	 * REPEAT loop id
	 */
	protected static final int TYPE_REPEAT=0;
	/**
	 * WHILE loop id
	 */
	protected static final int TYPE_WHILE=1;
	/**
	 * FOR loop id
	 */
	protected static final int TYPE_FOR=2;
	private boolean conserver=false;
	private String var="";
	private BigDecimal counter,fin,increment;
	private String instr;
	private int id;
/**
 * This constructor Loops: repeat or while
 * @param counter The beginning integer
 * @param fin The end integer
 * @param increment The increment between two values
 * @param instr The instruction to execute each loop
 * @param id The type for the loop: TYPE_REPEAT or TYPE_WHILE
 */
	LoopProperties(BigDecimal counter,BigDecimal fin,BigDecimal increment,String instr, int id){
		this.counter=counter;
		this.fin=fin;
		this.increment=increment;
		this.instr=instr;
		this.id=id;
	}
	/**
	 * Constructor Loop: For
	 * @param counter The beginning integer
	 * @param fin The end integer
	 * @param increment The incerment between two values
	 * @param instr The instruction to execute between two values
	 * @param id The type for the loop: TYPE_FOR
	 * @param var The name of the variable
	 */

	LoopProperties(BigDecimal counter,BigDecimal fin,BigDecimal increment,String instr, int id,String var){
		this.counter=counter;
		this.fin=fin;
		this.increment=increment;
		this.instr=instr;
		this.id=id;
		this.var=var;
	}
	/**
	 * This method returns the variable name, loop TYPE_FOR
	 * @return the variable name 
	 */
	
	protected String gerVar(){
		return var;
	}
	/**
	 * Adds the increment to the variable counter
	 */
	
	protected void incremente(){
		counter=counter.add(increment);
		counter=new BigDecimal(LaunchPrimitive.eraseZero(counter));
	//	System.out.println(counter);
	}
	/**
	 * This method returns the Loop Id
	 * @return the Loop Id (TYPE_FOR, TYPE_WHILE...)
	 */
	protected int getId(){
		return id;
	}
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
	protected BigDecimal getFin(){ 
		return fin;
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
		return(counter+" "+fin+" "+increment+"\n"+instr+"\n"+id);
	}	
	/**
	 * This method affects the variable counter the correct value 
	 * @param first boolean that indicates if it is the first affectation
	 */
	protected void AffecteVar(boolean first){
		String element=String.valueOf(counter);
		if (element.endsWith(".0")) element=element.substring(0,element.length()-2) ;
        if (element.startsWith(".")||element.equals("")) element="0"+element;

		if (Interprete.locale.containsKey(var)){
			if (first) conserver=true;
			Interprete.locale.put(var, element);
		} 
		else {
			Interprete.locale.put(var,element);
		}
	}
	/**
	 * This method deletes the variable var from the local stack variable
	 */
	void DeleteVar(){
		if (!conserver){
			if (Interprete.locale.containsKey(var)){
				Interprete.locale.remove(var);
			}
/*			else {
				id = Application.globale.search(var);
				Application.valeur.remove(Application.valeur.size() - id);
				Application.globale.remove(Application.globale.size() - id);
			}*/
		}
	}

}
