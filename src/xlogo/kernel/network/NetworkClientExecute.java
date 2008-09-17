package xlogo.kernel.network;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */

import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import xlogo.utils.myException;
import java.net.InetAddress;
import xlogo.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.kernel.Kernel;
public class NetworkClientExecute {
	private InetAddress ip;
	private String cmd;
	private Application app;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private Kernel kernel;
	public NetworkClientExecute(Application app,String ip, String cmd)throws myException{
		this.app=app;
		this.kernel=app.getKernel();
		try{
			this.ip=InetAddress.getByName(ip);
		}
		catch(UnknownHostException e){
			throw new myException(app,Logo.messages.getString("no_host")+" "+ip);
		}
		this.cmd=cmd;
		init();
	}
	private void init() throws myException{
		try{
			socket=new Socket(ip,Config.TCP_PORT);
			java.io.OutputStream os =socket.getOutputStream();
			BufferedOutputStream b = new BufferedOutputStream(os);	
			OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");
			out=new PrintWriter(osw,true);
			in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String wp=NetworkServer.EXECUTETCP+"\n";
			wp+=kernel.getWorkspace().toString();
			wp+=NetworkServer.END_OF_FILE;
			out.println(wp);
			String input;
			while ((input=in.readLine())!=null){
//				System.out.println("je vais envoyer: OK");
				if (input.equals("OK")) {
//					System.out.println("chargement reussi");
					break;
				}
			}
			out.println(cmd);
			out.close();
			in.close();
			socket.close();
		}
		catch(IOException e){
			throw new myException(app,Logo.messages.getString("no_host")+ip.getHostAddress());
		}
	}
}
