package xlogo.kernel.network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import xlogo.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.utils.myException;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */

public class NetworkClientChat {
	private InetAddress ip;
	private String st;
	private Application app;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	ChatFrame cf;
	public NetworkClientChat(Application app,String ip, String st)throws myException{
		this.app=app;
		try{
			this.ip=InetAddress.getByName(ip);
		}
		catch(UnknownHostException e){
			throw new myException(app,Logo.messages.getString("no_host")+" "+ip);
		}
		this.st=st;
		init();
	}
	private void init() throws myException{
		try{
			socket=new Socket(ip,Config.TCP_PORT);
			java.io.OutputStream os =socket.getOutputStream();
			BufferedOutputStream b = new BufferedOutputStream(os);	
			OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");
			out=new PrintWriter(osw,true);
			in=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF8"));
			String cmd=NetworkServer.CHATTCP+"\n";
			cmd+=st+"\n";
			cmd+=NetworkServer.END_OF_FILE;
			cf=new ChatFrame(out,app);
			cf.append("local",st);
			out.println(cmd);
        	while ((cmd=in.readLine())!=null) {
        		if (cmd.equals(NetworkServer.END_OF_FILE)) {
        			cf.append("local",Logo.messages.getString("stop_chat"));
        			break;
        			}
        		cf.append("distant",cmd);
        	}
			out.close();
			in.close();
			socket.close();
		}
		catch(IOException e){
			throw new myException(app,Logo.messages.getString("no_host")+ip.getHostAddress());
		}
		
	}

}
