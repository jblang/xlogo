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

public class NetworkClientSend {
	private InetAddress ip;
	private String data;
	private Application app;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;
	private String answer;
	public NetworkClientSend(Application app,String ip, String data)throws myException{
		this.app=app;
		try{
			this.ip=InetAddress.getByName(ip);
		}
		catch(UnknownHostException e){
			throw new myException(app,Logo.messages.getString("no_host")+" "+ip);
		}
		this.data=data;
		init();
	}
	public String getAnswer(){
		return answer;
	}
	private void init() throws myException{
		try{
			socket=new Socket(ip,Config.TCP_PORT);
			in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
			java.io.OutputStream os =socket.getOutputStream();
			BufferedOutputStream b = new BufferedOutputStream(os);	
			OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");
			out=new PrintWriter(osw,true);
			out.println(data);
			answer="";
		  	answer=in.readLine();
			out.close();
			socket.close();
		}
		catch(IOException e){
			throw new myException(app,Logo.messages.getString("no_host")+ip.getHostAddress());
		}		
	}
}
