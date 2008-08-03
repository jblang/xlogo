package xlogo.kernel;

import java.math.MathContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Stack;

import xlogo.Application;
import xlogo.Logo;
import xlogo.utils.Utils;
import xlogo.utils.myException;
public class MyCalculator {
	private final BigDecimal tenth=new BigDecimal(0.1);
	
	private  MathContext mc=null;
	

	// If precision is lesser than 16 (operation for double) 
	private boolean lowPrecision=true;
	
	/**
	 * Indicates if the log table have been created
	 */
	private boolean initLogTable;
	/**
	 * This is a table containing all BigDecimal
	 * ln(1+10^ (-k) ), k in {0,1....,digits-1}
	 * This are constants for the Cordic method
	 * to calculate ln, exp
	 */
	private BigDecimal[] logTable;
	/**
	 * Indicates if the trigonometric table have been created
	 */
	private boolean initCosTable;
	/**
	 * This is a table containing all BigDecimal
	 * arctan 10^ (-k) , k in {0,1....,digits-1}
	 * This are constants for the Cordic method
	 * to calculate trigonometric functions
	 */
	private BigDecimal[] cosTable;
	private Application app;
	private static int digits;
	protected MyCalculator(int digits,Application app){
		MyCalculator.digits=digits;
		this.app=app;
		initLogTable=false;
		initCosTable=false;
		if (digits<16) {
			mc=new MathContext(16);
			lowPrecision=true;
			logTable=new BigDecimal[16];
			cosTable=new BigDecimal[16];
		}
		else {
			mc=new MathContext(digits);
			lowPrecision=false;
			logTable=new BigDecimal[digits];
			cosTable=new BigDecimal[digits];
		}		
	}
	
	/**
	 * Return The exponential of s according to matContext Precision
	 * @param s The number 
	 * @return Exp(s)
	 * @throws myException if s isn't a number
	 */
	
	protected String exp(String s) throws myException{
		if (lowPrecision){
			double nombre = numberDouble(s);
			return teste_fin_double(Math.exp(nombre));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return expBD(bd).toPlainString();
		}
	}
	/**
	 * Return The logarithm of s according to matContext Precision
	 * @param s The number 
	 * @return log(s)
	 * @throws myException if s isn't a number or negative
	 */
	
	protected String log(String s) throws myException{
		if (lowPrecision){
			double nombre = numberDouble(s);
			if (nombre < 0 || nombre == 0) {
				String log=Utils.primitiveName("arithmetic.log");
				throw new myException(app, log + " "
						+ Logo.messages.getString("attend_positif"));
			}
			return teste_fin_double(Math.log(nombre));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			if (bd.signum()!=1) {
				String log=Utils.primitiveName("arithmetic.log");
				throw new myException(app, log + " "
						+ Logo.messages.getString("attend_positif"));
			}
			return logBD(bd).toPlainString();
		}
	}
	/**
	 * Return The square root of s according to matContext Precision
	 * @param s The number 
	 * @return sqrt(s)
	 * @throws myException if s isn't a number or negative
	 */
	
	protected String sqrt(String s) throws myException{
		if (lowPrecision){
			double number = numberDouble(s);
			if (number < 0) {
				String sqrt=Utils.primitiveName("arithmetic.racine");
				throw new myException(app, sqrt + " "
						+ Logo.messages.getString("attend_positif"));
			}
			return teste_fin_double(Math.sqrt(number));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			if (bd.signum()==-1) {
				String sqrt=Utils.primitiveName("arithmetic.racine");
				throw new myException(app, sqrt + " "
						+ Logo.messages.getString("attend_positif"));
			}
			return sqrtBD(bd).toPlainString();
		}
	}
	/**
	 * Return the product of all elements in stack param
	 * @param param The stack of operands
	 * @return The product
	 */
	protected String multiply(Stack<String> param) {
		int size = param.size();
		BigDecimal product = BigDecimal.ONE;
		BigDecimal a;
		try {
			for (int i = 0; i < size; i++) {
				a = numberDecimal(param.get(i));
				product = product.multiply(a,mc);
			}

		} catch (myException e) {
		}
		return product.stripTrailingZeros().toPlainString();
	}

	protected String divide(Stack<String> param) throws myException{
			if (lowPrecision){
				double a = numberDouble(param.get(0));
				double b = numberDouble(param.get(1));
				if (b == 0)
				throw new myException(app, Logo.messages
						.getString("division_par_zero"));
				return(teste_fin_double(a / b));
		} 
			else{
				BigDecimal a=new BigDecimal(param.get(0),mc);
				BigDecimal b=new BigDecimal(param.get(1),mc);
				if (b.signum()==0)				throw new myException(app, Logo.messages
						.getString("division_par_zero"));
				return (a.divide(b, mc).stripTrailingZeros().toPlainString());
			}			
	}
	/**
	 * Return the sum of all elements in stack param
	 * @param param The stack of operands
	 * @return The sum
	 */
	protected String add(Stack<String> param) {
		int size = param.size();
		BigDecimal sum = BigDecimal.ZERO;		
		try {
			BigDecimal a;
			for (int i = 0; i < size; i++) {
				a = numberDecimal(param.get(i));
				sum = sum.add(a,mc);
			}
		} catch (myException e) {
		}
		return sum.stripTrailingZeros().toPlainString();
	}

	protected String substract(Stack<String> param) throws myException{
			BigDecimal a = numberDecimal(param.get(0));
			BigDecimal b = numberDecimal(param.get(1));
			return a.subtract(b,mc).stripTrailingZeros().toPlainString();
	}
	
	/**
	 * Returns the opposite of s
	 * @param s 
	 * @return
	 */
	protected String minus(String s) throws myException{
			BigDecimal a = numberDecimal(s);
			return a.negate(mc).stripTrailingZeros().toPlainString();
	}
	protected String remainder(String a , String b) throws myException{
		if (lowPrecision){
			int aa = getInteger(a);
			int bb = getInteger(b);
			if (bb == 0)
			throw new myException(app, Logo.messages
					.getString("division_par_zero"));
			return teste_fin_double(aa % bb);			
		}
		else {
			BigDecimal aa = getBigInteger(a);
			BigDecimal bb = getBigInteger(b);
			if (bb.signum() == 0)
			throw new myException(app, Logo.messages
					.getString("division_par_zero"));
			return aa.remainder(bb, mc).stripTrailingZeros().toPlainString();
			
		}
	}

	protected String quotient(String a , String b) throws myException{
		if (lowPrecision){
			double aa = numberDouble(a);
			double bb = numberDouble(b);
			if (bb == 0)
				throw new myException(app, Logo.messages
						.getString("division_par_zero"));
			return String.valueOf((int)(aa/bb));
		}
		else{
			BigDecimal aa = numberDecimal(a);
			BigDecimal bb = numberDecimal(b);
			if (bb.signum() == 0)
			throw new myException(app, Logo.messages
					.getString("division_par_zero"));
			return aa.divideToIntegralValue(bb, mc).stripTrailingZeros().toPlainString();			
			
		}
	}
	protected String truncate(String a) throws myException{
		BigDecimal ent = numberDecimal(a);
		return ent.toBigInteger().toString();
	}
	protected String abs(String a) throws myException{
		BigDecimal e=numberDecimal(a);
		return e.abs().stripTrailingZeros().toPlainString();
	}
	
	protected String power(String a,String b) throws myException{
		if (lowPrecision){
			double p = Math.pow(numberDouble(a),numberDouble(b));
		// Bug pr power -1 0.5
		Double p1=new Double(p);
		if (p1.equals(Double.NaN)) throw new myException(app,Utils.primitiveName("arithmetic.puissance")+" "+Logo.messages.getString("attend_positif"));
		// End Bug
			return teste_fin_double(p);
		}
		else{
			// if the exposant is an integer
			try {
				int n=Integer.parseInt(b);			
				BigDecimal aa=numberDecimal(a);
				return aa.pow(n, mc).toPlainString();			
			}
			catch(NumberFormatException e){
				BigDecimal aa=numberDecimal(a);
				BigDecimal bb=numberDecimal(b);
				if (aa.signum()==1){
					return expBD(bb.multiply(logBD(aa), mc)).toPlainString();
				}
				else if (aa.signum()==0)
					return "0";	
				else return String.valueOf(getInteger(b));
			}
		}
	}
	protected String log10(String s) throws myException{
		Stack<String> tmp=new Stack<String>();
		tmp.push(log(s));
		tmp.push(log("10"));
		return divide(tmp);
	}
	protected String pi() {
		if (lowPrecision){
			return 	String.valueOf(Math.PI);
		}
		else {
			return piBD().toPlainString();
		} 
	}
	
	protected String sin(String s) throws myException{
		if (lowPrecision){
			return teste_fin_double(Math.sin(Math
					.toRadians(numberDouble(s))));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return sinBD(bd).toPlainString();
			
		}
	}
	protected String cos(String s)throws myException{
		if (lowPrecision){
			return teste_fin_double(Math.cos(Math
					.toRadians(numberDouble(s))));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return cosBD(bd).toPlainString();
			
		}
	}
	protected String tan(String s)throws myException{
		if (lowPrecision){
			return teste_fin_double(Math.tan(Math
					.toRadians(numberDouble(s))));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return tanBD(bd).toPlainString();			
		}
	}
	protected String atan(String s)throws myException{
		if (lowPrecision){
			return teste_fin_double(Math.toDegrees(Math
					.atan(numberDouble(s))));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return toDegree(atanBD(bd)).toPlainString();			
		}
	}
	protected String acos(String s)throws myException{
		if (lowPrecision){
			return teste_fin_double(Math.toDegrees(Math.acos(numberDouble(s))));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return toDegree(acosBD(bd)).toPlainString();			
		}
	}
	protected String asin(String s)throws myException{
		if (lowPrecision){
			return teste_fin_double(Math.toDegrees(Math.asin(numberDouble(s))));
		}
		else {
			BigDecimal bd=numberDecimal(s);
			return toDegree(asinBD(bd)).toPlainString();			
		}
	}
	/**
	 * This method returns the exp of bd
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal expBD(BigDecimal bd){
		if (!initLogTable){
			initLogTable();
		}
		int signum=bd.signum();
		if (signum==-1){
			BigDecimal exp=expCordic(bd.negate(mc));
			exp=BigDecimal.ONE.divide(exp,mc);
			return exp;
		}
		else if (signum==0)	return BigDecimal.ONE;
		else {
			return expCordic(bd);
		}
	}
	
	private BigDecimal expCordic(BigDecimal bd){
		int i=0;
		BigDecimal y=BigDecimal.ONE;
		while (i<mc.getPrecision()){
			while(logTable[i].subtract(bd).signum()==-1){
				bd=bd.subtract(logTable[i],mc);
				y=y.add(y.multiply(tenth.pow(i,mc),mc),mc);
			}
			i++;
		}
		y=y.multiply(bd.add(BigDecimal.ONE,mc), mc);
		return y;
		
	}

	/**
	 * This method returns the log of bd
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal logBD(BigDecimal bd){
		if (!initLogTable){
			initLogTable();
		}
		// If bd > 1
		int signum=bd.subtract(BigDecimal.ONE, mc).signum();
		if (signum==1){
			bd=bd.subtract(BigDecimal.ONE, mc);
			return logCordic(bd);
		}
		else if (signum==0) return BigDecimal.ZERO;
		else {
			bd=BigDecimal.ONE.divide(bd,mc).subtract(BigDecimal.ONE, mc);
			return logCordic(bd).negate(mc);
		}	
	}
	
	private BigDecimal logCordic(BigDecimal bd){
		int i=0;
		BigDecimal y=BigDecimal.ZERO;
		while (i<mc.getPrecision()){
			BigDecimal tenthi=tenth.pow(i,mc);
			while(bd.subtract(tenthi, mc).signum()>0){
				bd=bd.subtract(tenthi,mc).divide(BigDecimal.ONE.add(tenthi,mc),mc);
				y=y.add(logTable[i],mc);
			}
			i++;
		}
		y=y.add(bd,mc).subtract(bd.pow(2,mc).multiply(new BigDecimal(0.5), mc),mc);
		return y;
		
	}
	
	/**
	 * This method returns the sqrt of bd
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal sqrtBD(BigDecimal bd){
		if (bd.signum()==0) return BigDecimal.ZERO;
		BigDecimal three=new BigDecimal(3);
		BigDecimal half=new BigDecimal(0.5);
		BigDecimal x=BigDecimal.ZERO;
		BigDecimal y=BigDecimal.ONE.min(BigDecimal.ONE.divide(bd,mc));
		while(x.compareTo(y)==-1){
			x=y;
			// y=(3x-bd*x^3)/2
			y=x.multiply(three, mc).subtract(bd.multiply(x.pow(3, mc), mc), mc).multiply(half, mc);
		}
		return BigDecimal.ONE.divide(y, mc); 
	}
	/**
	 * This method returns the cos of bd
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal cosBD(BigDecimal bd){
		// bd is in degree
		BigDecimal period=new BigDecimal(360);
		BigDecimal a90=new BigDecimal(90);
		BigDecimal a135=new BigDecimal(135);
		BigDecimal a180=new BigDecimal(180);
		BigDecimal a225=new BigDecimal(225);
		BigDecimal a270=new BigDecimal(270);
		BigDecimal a315=new BigDecimal(315);
		
		bd=bd.remainder(period, mc);
		if (bd.signum()==-1) bd=bd.add(period,mc);
		BigDecimal quarterpi=new BigDecimal(45);
		// Now bd between 0 and 360.
		if (bd.compareTo(quarterpi)==-1){
			// Between 0 and 45
			return cosCordic(toRadian(bd));
		}
		else if (bd.compareTo(a90)==-1){
				// Between 45 and 90
				return sinCordic(toRadian(a90.subtract(bd,mc)));
		}  
		else if (bd.compareTo(a135)==-1){
			// Between 90 and 135
			return sinCordic(toRadian(bd.subtract(a90,mc))).negate(mc);
		}
		else if (bd.compareTo(a180)==-1){
			// Between 135 and 180
			return cosCordic(toRadian(a180.subtract(bd,mc))).negate(mc);
		}	
		else if (bd.compareTo(a225)==-1){
			// Between 180 and 225
			return cosCordic(toRadian(bd.subtract(a180,mc))).negate(mc);
		}
		else if (bd.compareTo(a270)==-1){
			// Between 225 and 270
			return sinCordic(toRadian(a270.subtract(bd,mc))).negate(mc);
		}
		else if (bd.compareTo(a315)==-1){
			// Between 270 and 315
			return sinCordic(toRadian(bd.subtract(a270,mc)));
		}
		else {
			return cosCordic(toRadian(new BigDecimal(360).subtract(bd,mc)));
		}
	}
	/**
	 * This method returns the cos of bd with 0<bd<pi/4
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal cosCordic(BigDecimal bd){
		return BigDecimal.ONE.divide(sqrtBD(tanCordic(bd).pow(2, mc).add(BigDecimal.ONE,mc)),mc);
	}
	
	/**
	 * This method returns the cos of bd
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal sinBD(BigDecimal bd){
		BigDecimal a90=new BigDecimal(90);
		return cosBD(a90.subtract(bd, mc));
		
	}
	/**
	 * This method returns the sin of bd with 0<bd<pi/4
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal sinCordic(BigDecimal bd){
		BigDecimal tan=tanCordic(bd);
		return tan.divide(sqrtBD(tan.pow(2, mc).add(BigDecimal.ONE,mc)),mc);
	}
	/**
	 * This method returns the tan of bd (in degree)
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal tanBD(BigDecimal bd){
		// bd is in degree
		BigDecimal pi=new BigDecimal(180);
		BigDecimal halfpi=new BigDecimal(90);
		BigDecimal quarterpi=new BigDecimal(45);
		bd=bd.remainder(pi, mc);
		if (bd.compareTo(halfpi.negate(mc))==-1) bd=bd.add(pi,mc);
		if (bd.compareTo(halfpi)==1) bd=bd.subtract(pi,mc);			
		// Now bd is in -90;+90 degrees
		
		if (bd.compareTo(quarterpi)==1) {
			BigDecimal x=toRadian(new BigDecimal(0.5).multiply(bd, mc));
			return new BigDecimal(2).multiply(tanCordic(x), mc).divide(BigDecimal.ONE.subtract(tanCordic(x).pow(2, mc), mc), mc);
		}			
		else if (bd.signum()==1){
			return tanCordic(toRadian(bd));
		}
		else if (bd.compareTo(quarterpi.negate(mc))==1){
			return tanCordic(toRadian(bd.negate(mc))).negate(mc);
		}
		else {
			BigDecimal x=toRadian(new BigDecimal(0.5).multiply(bd, mc)).negate(mc);
			return new BigDecimal(2).multiply(tanCordic(x), mc).divide(BigDecimal.ONE.subtract(tanCordic(x).pow(2, mc), mc), mc).negate(mc);			
		}
	}
	
	
	/**
	 * This method returns the tan of bd with 0<bd<pi/4
	 * based on the Cordic algorithm
	 * @param bd The first BigDecimal
	 * @return The result
	 */
	private BigDecimal tanCordic(BigDecimal bd){
		if (!initCosTable) initCosTable();
		BigDecimal three=new BigDecimal(3);
		int k=1;
		BigDecimal x=BigDecimal.ONE;
		BigDecimal y=BigDecimal.ZERO;
		BigDecimal tenthk=tenth;
		while(k<mc.getPrecision()){
			while(cosTable[k].compareTo(bd)==-1){
				bd=bd.subtract(cosTable[k],mc);
				BigDecimal tmp=x;
				x=x.subtract(tenthk.multiply(y, mc),mc);
				y=y.add(tenthk.multiply(tmp, mc), mc);
			}
			tenthk=tenthk.multiply(tenth, mc);
			k++;
		}
		BigDecimal tmp=bd.pow(3, mc).add(three.multiply(bd, mc), mc);
		// return (3*y+(3t+t^3)*x)/(3x-(3t+t^3)*y
		return three.multiply(y,mc).add(x.multiply(tmp, mc), mc).divide(three.multiply(x,mc).subtract(y.multiply(tmp, mc),mc), mc);
	}	
	private BigDecimal piBD(){
		if (!initCosTable) {
			initCosTable();
		}		
		return cosTable[0].multiply(new BigDecimal(4),mc);
	}
	/**
	 * This method creates the log Table using
	 * log h=(h-1)-(h-1)^2/2+(h-1)^3/3-.....
	 * @param bd The first BigDecimal
	 * @return The result
	 */	
	private void initLogTable(){
		initLogTable=true;
		// calculate ln 2
		// Using ln 2=2*(x+x^3/3+x^5/5+....) with x=1/3 
		
		BigDecimal sum=BigDecimal.ZERO;
		BigDecimal previous=BigDecimal.ONE;
		BigDecimal i=BigDecimal.ONE;
		BigDecimal nine=new BigDecimal(9);
		BigDecimal two=new BigDecimal(2);
		BigDecimal power=new BigDecimal(3);
		while (sum.subtract(previous, mc).abs(mc).compareTo(BigDecimal.ZERO)!=0){
			previous=sum;
			sum=sum.add(BigDecimal.ONE.divide(i.multiply(power,mc),mc),mc);
			i=i.add(two,mc);
			power=power.multiply(nine,mc);
		}
		logTable[0]=sum.multiply(two,mc);
		
		// Calculate ln (1+10^-j) j in 1 ... digits-1
		
		
		for (int j=1;j<mc.getPrecision();j++){
		//	count=0;
			sum=BigDecimal.ZERO;
			previous=BigDecimal.ONE;
			i=BigDecimal.ONE;
			// 10^(-j)
			BigDecimal bd=tenth.pow(j,mc);
			power=bd;
			while (sum.subtract(previous, mc).abs(mc).compareTo(BigDecimal.ZERO)!=0){
				previous=sum;
				sum=sum.add(power.divide(i,mc),mc);
				if (i.signum()==1) i=i.add(BigDecimal.ONE,mc).negate(mc);
				else i=i.subtract(BigDecimal.ONE,mc).negate(mc);
				power=power.multiply(bd,mc);
			//	count++;
			}
			logTable[j]=sum;
		}
	}
	/**
	 * This method creates the cos Table using
	 * arctan h=x-x^3/3+x^5/5-x^7/7...
	 * @param bd The first BigDecimal
	 * @return The result
	 */	
	private void initCosTable(){
		initCosTable=true;
		// calculate pi/4
		
		cosTable[0]=calcPI().multiply(new BigDecimal(0.25), mc);
		
		// Calculate arctan (10^-j) j in 1 ... digits-1
		
		for (int j=1;j<mc.getPrecision();j++){
			// 10^(-j)
			BigDecimal bd=tenth.pow(j,mc);
			cosTable[j]=arctanSE(bd);			
			//System.out.println(cosTable[j].toPlainString());
		}
	}		
	// Using  PI = 16arctg(1/5) - 4arctg(1/239)
	private BigDecimal calcPI(){
		return new BigDecimal(16).multiply(arctanSE(new BigDecimal("0.2")), mc)
		.subtract(new BigDecimal(4).multiply(arctanSE(BigDecimal.ONE.divide(new BigDecimal(239), mc)), mc), mc);
	}
	
	private BigDecimal arctanSE2(BigDecimal bd){
		BigDecimal i=BigDecimal.ONE;
//		BigDecimal j=new BigDecimal(3);
		BigDecimal two=new BigDecimal(2);
		BigDecimal square=bd.multiply(bd, mc);
		BigDecimal power=bd.divide(square.add(BigDecimal.ONE,mc), mc);
		BigDecimal cst=new BigDecimal(4).multiply(square,mc).divide(square.add(BigDecimal.ONE,mc), mc);
		BigDecimal previous=BigDecimal.ZERO;
		BigDecimal sum=power;
		int count=0;
		while (sum.subtract(previous, mc).abs(mc).compareTo(BigDecimal.ZERO)!=0){
			previous=sum;
			power=power.multiply(cst,mc);
			power=power.multiply(i.pow(2,mc),mc);
			BigDecimal doublei=two.multiply(i, mc);
			doublei=doublei.multiply(doublei.add(BigDecimal.ONE, mc),mc);
			power=power.divide(doublei, mc);
			sum=sum.add(power,mc);
			i=i.add(BigDecimal.ONE);
			count++;
		}
		System.out.println("Itérations "+count);
		return sum;
		
	}
	private BigDecimal atanBD(BigDecimal bd){
		if (bd.signum()==-1) return atanBD(bd.negate(mc)).negate(mc);
		if (bd.compareTo(BigDecimal.ONE)==1) 
			// pi/2 -arctan (1/x)
			return piBD().multiply(new BigDecimal(0.5),mc).subtract(arctanSE(BigDecimal.ONE.divide(bd, mc)), mc);
		else if (bd.compareTo(BigDecimal.ONE)==0) return piBD().multiply(new BigDecimal("0.25"), mc);
		else return arctanSE(bd);
	}
	private BigDecimal acosBD(BigDecimal bd){
		if (bd.compareTo(new BigDecimal("-1"))==0){return piBD();}
		// acos x= 2 atan (sqrt(1-x^2)/1+x
		else {
			return new BigDecimal("2").multiply(atanBD(sqrtBD(BigDecimal.ONE.subtract(bd.pow(2, mc), mc)).divide(BigDecimal.ONE.add(bd,mc), mc)),mc);
			
		}
	}
	private BigDecimal asinBD(BigDecimal bd){
		// acos x= 2 atan (x/(1+sqrt(1-x^2))
			return new BigDecimal("2").multiply(atanBD(bd.divide(BigDecimal.ONE.add(sqrtBD(BigDecimal.ONE.subtract(bd.pow(2, mc), mc)),mc),mc)),mc);			
	}	
	// arctan h=x-x^3/3+x^5/5-x^7/7...
	private BigDecimal arctanSE(BigDecimal bd){
		BigDecimal i=BigDecimal.ONE;
		BigDecimal two=new BigDecimal(2,mc);
		BigDecimal square=bd.multiply(bd, mc);
		BigDecimal sum=BigDecimal.ZERO;
		BigDecimal previous=BigDecimal.ONE;
		while (sum.subtract(previous, mc).abs(mc).compareTo(BigDecimal.ZERO)!=0){
			previous=sum;
			sum=sum.add(bd.divide(i,mc),mc);
			if (i.signum()==1) i=i.add(two,mc).negate(mc);
			else i=i.subtract(two,mc).negate(mc);
			bd=bd.multiply(square,mc);
		}
		return sum;
	}
	
	
	private BigDecimal toRadian(BigDecimal n){
		return n.multiply(piBD(), mc).divide(new BigDecimal(180), mc);
	}
	private BigDecimal toDegree(BigDecimal n){
		return n.multiply(new BigDecimal(180), mc).divide(piBD(), mc);
	}
	/**
	 * This method converts st to double
	 * 
	 * @param st
	 *            The String
	 * @return The double corresponding to st
	 * @throws myException
	 *             If st can't be convert
	 */

	protected double numberDouble(String st) throws myException { // Si un nombre est
															// un double
		try {
			return (Double.parseDouble(st));
		} catch (NumberFormatException e) {
			throw new myException(app, st + " "
					+ Logo.messages.getString("pas_nombre"));
		}
	}
	/**
	 * If a double ends with the suffix ".0", remove it
	 * @param d
	 * @return
	 */
	
	static protected String teste_fin_double(double d) {
		String st = String.valueOf(d);
		if (st.endsWith(".0"))
			st = st.substring(0, st.length() - 2);
		return st;
	}

	/**
	 * Converts st to BigDecimal number
	 * 
	 * @param st
	 *            The String to convert
	 * @return The BigDecimal Number
	 * @throws myException
	 *             if st isn't a number
	 */

	protected BigDecimal numberDecimal(String st) throws myException {
		/*if (null==mc){
			try {
				BigDecimal bd = new BigDecimal(st).setScale(16,
						BigDecimal.ROUND_HALF_EVEN);
				return (new BigDecimal(eraseZero(bd)));

			} catch (NumberFormatException e) {
				throw new myException(app, st + " "
						+ Logo.messages.getString("pas_nombre"));
			}
		}
		else {*/
		try{
				return new BigDecimal(st,mc);
		} 
		catch (NumberFormatException e) {
				throw new myException(app, st + " "
						+ Logo.messages.getString("pas_nombre"));
			}
//		}
		
	}
	/**
	 * Erase unused Zeros in decimal Format
	 * 
	 * @param bd
	 *            The decimal number
	 * @return The formatted number
	 */
	static protected String eraseZero(BigDecimal bd) {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		DecimalFormat df = new DecimalFormat("#####.################", dfs);
		String st = df.format(bd);
		return st;

	}
	/**
	 * Test if the number contained in st is an integer
	 * 
	 * @param st
	 *            The Object to convert
	 * @return The integer corresponding to st
	 * @throws myException
	 *             If it isn't an integer
	 */

	protected int getInteger(String st) throws myException { // Si c'est un
															// entier
		try {
				return Integer.parseInt(st);
		} catch (NumberFormatException e) {
			throw new myException(app, st + " "
					+ Logo.messages.getString("pas_entier"));
		}
	}
	/**
	 * Test if the number contained in st is an integer
	 * 
	 * @param st
	 *            The Object to convert
	 * @return The integer corresponding to st
	 * @throws myException
	 *             If it isn't an integer
	 */

	protected BigDecimal getBigInteger(String st) throws myException { // Si c'est un
															// entier
		try {
			return new BigDecimal(new BigInteger(st));
		} catch (NumberFormatException e) {
			throw new myException(app, st + " "
					+ Logo.messages.getString("pas_entier"));
		}
	}
	
	protected int getDigits(){
		if (digits<0) return -1;
		else return mc.getPrecision();
	}
	public static String getOutputNumber(String s){
		try{
				if (digits>=0&&digits<16)  {
					BigDecimal bd=new BigDecimal(s);
					s=bd.toPlainString();
					// is it a decimal number?
					int index=s.indexOf(".");
					if (index!=-1){
						if (digits==0) return s.substring(0,index); 
						else if (s.length()>index+digits){
							s=s.substring(0,index+digits+1);
							int a=Integer.parseInt(String.valueOf(s.charAt(s.length()-1)));
							if (a>4) {
								a++;
								s=s.substring(0, s.length()-1)+a;
							}
							return s;
						}
					}
					else return s;	
				}
			}
		catch(NumberFormatException e){}
		return s;
	}
}
