package xlogo.kernel.network;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo 
 * 						programming language
 * @author Loïc Le Coq
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import xlogo.utils.myException;
import xlogo.Application;
import xlogo.Logo;
import xlogo.Config;
import xlogo.kernel.Workspace;
import xlogo.kernel.Kernel;
public class NetworkServer {
	public static boolean isActive;
	private ServerSocket serverSocket;
	private Application app;
	private PrintWriter out;
	private BufferedReader in;
	private ChatFrame cf;

	protected static final String EXECUTETCP="executetcp"+"\u001B";
	protected static final String CHATTCP="chattcp"+"\u001B";
	protected static final String END_OF_FILE="*/EOF\\*"+"\u001B";
	private Kernel kernel;
	public NetworkServer(Application app){
		isActive=true;
		this.app=app;
		this.kernel=app.getKernel();
		try{
			init();
		}
		catch(myException e){}
	
	}
	private void init()throws myException{
		try {
			serverSocket=new ServerSocket(Config.TCP_PORT);
		}
		catch(IOException e){
			throw(new myException(app,Logo.messages.getString("pb_port")+Config.TCP_PORT));
		}
		Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
		java.io.OutputStream os =clientSocket.getOutputStream();
		BufferedOutputStream b = new BufferedOutputStream(os);	
		OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");
		out=new PrintWriter(osw,true);
        in = new BufferedReader(
				new InputStreamReader(
				clientSocket.getInputStream(),"UTF8"));
        String inputLine="";
         inputLine = in.readLine();
        // ******************* Executetcp **************************
        if (inputLine.equals(NetworkServer.EXECUTETCP)){
            StringBuffer workspace=new StringBuffer();
        	while((inputLine=in.readLine())!=null){
            	if (inputLine.equals(NetworkServer.END_OF_FILE)) break;
        		workspace.append(inputLine);
        		workspace.append("\n");
        	}
            Workspace wp=new Workspace(app);
            kernel.setWorkspace(wp);
            wp.setWorkspace(app,workspace.toString());
            // We say the workspace is fully created
            out.println("OK");
            // What is the command to execute?
            inputLine = in.readLine();
      //     System.out.println("a exécuter: "+inputLine);
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
           
            kernel.execute(new StringBuffer(inputLine+ "\\x "));

        }
        // ******************* Chattcp **************************
        else if(inputLine.equals(NetworkServer.CHATTCP)){
            String sentence="";
        	while((inputLine=in.readLine())!=null){
            	if (inputLine.equals(NetworkServer.END_OF_FILE)) break;
        		sentence=inputLine;
        	}
        	cf=new ChatFrame(out,app);
        	cf.append("distant",sentence);
        	while ((sentence=in.readLine())!=null) {
        		if (sentence.equals(NetworkServer.END_OF_FILE)) {
        			cf.append("local",Logo.messages.getString("stop_chat"));
        			break;
        			}
        		cf.append("distant",sentence);
        	}
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
        // ******************* Envoietcp **************************
        else{
        	app.ecris("normal",inputLine);
        	out.println(Logo.messages.getString("pref.ok"));
        	out.close();
        	in.close();
        	clientSocket.close();
        	serverSocket.close();

        }
        } catch (IOException e) {
            throw(new myException(app,Logo.messages.getString("erreur_tcp")));
        }
        isActive=false;
    }
	public static void stopServer(){ 
		String st=NetworkServer.EXECUTETCP+"\n"+NetworkServer.END_OF_FILE+"\n\n";
		try{
			Socket socket=new Socket("127.0.0.1",Config.TCP_PORT);
			PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
			out.println(st);
		}
		catch(IOException e){
		}
	}
}
