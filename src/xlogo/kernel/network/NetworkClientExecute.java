package xlogo.kernel.network;
/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

import xlogo.gui.Application;
import xlogo.Config;
import xlogo.Logo;
import xlogo.kernel.Kernel;
import xlogo.utils.LogoException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class NetworkClientExecute {
    private final InetAddress ip;
    private final String cmd;
    private final Application app;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private final Kernel kernel;

    public NetworkClientExecute(Application app, String ip, String cmd) throws LogoException {
        this.app = app;
        this.kernel = app.getKernel();
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new LogoException(app, Logo.messages.getString("no_host") + " " + ip);
        }
        this.cmd = cmd;
        init();
    }

    private void init() throws LogoException {
        try {
            socket = new Socket(ip, Config.tcpPort);
            java.io.OutputStream os = socket.getOutputStream();
            BufferedOutputStream b = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(b, StandardCharsets.UTF_8);
            out = new PrintWriter(osw, true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String wp = NetworkServer.EXECUTETCP + "\n";
            wp += kernel.getWorkspace().toString();
            wp += NetworkServer.END_OF_FILE;
            out.println(wp);
            String input;
            while ((input = in.readLine()) != null) {
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
        } catch (IOException e) {
            throw new LogoException(app, Logo.messages.getString("no_host") + ip.getHostAddress());
        }
    }
}
