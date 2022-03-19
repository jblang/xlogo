package xlogo.kernel.network;

import xlogo.gui.GraphFrame;
import xlogo.Logo;
import xlogo.utils.LogoException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */

public class NetworkClientChat {
    ChatFrame cf;
    private final InetAddress ip;
    private final String st;
    private final GraphFrame graphFrame;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public NetworkClientChat(GraphFrame graphFrame, String ip, String st) throws LogoException {
        this.graphFrame = graphFrame;
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new LogoException(graphFrame, Logo.messages.getString("no_host") + " " + ip);
        }
        this.st = st;
        init();
    }

    private void init() throws LogoException {
        try {
            socket = new Socket(ip, Logo.config.getTcpPort());
            java.io.OutputStream os = socket.getOutputStream();
            BufferedOutputStream b = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(b, StandardCharsets.UTF_8);
            out = new PrintWriter(osw, true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String cmd = NetworkServer.CHATTCP + "\n";
            cmd += st + "\n";
            cmd += NetworkServer.END_OF_FILE;
            cf = new ChatFrame(out, graphFrame);
            cf.append("local", st);
            out.println(cmd);
            while ((cmd = in.readLine()) != null) {
                if (cmd.equals(NetworkServer.END_OF_FILE)) {
                    cf.append("local", Logo.messages.getString("stop_chat"));
                    break;
                }
                cf.append("distant", cmd);
            }
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            throw new LogoException(graphFrame, Logo.messages.getString("no_host") + ip.getHostAddress());
        }

    }

}
