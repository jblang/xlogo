package xlogo.kernel.network;

import xlogo.Logo;
import xlogo.gui.Application;
import xlogo.kernel.LogoException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class NetworkClientSend {
    private final InetAddress ip;
    private final String data;
    private final Application app;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private String answer;

    public NetworkClientSend(Application app, String ip, String data) throws LogoException {
        this.app = app;
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            throw new LogoException(app, Logo.getString("no_host") + " " + ip);
        }
        this.data = data;
        init();
    }

    public String getAnswer() {
        return answer;
    }

    private void init() throws LogoException {
        try {
            socket = new Socket(ip, Logo.config.getTcpPort());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            java.io.OutputStream os = socket.getOutputStream();
            BufferedOutputStream b = new BufferedOutputStream(os);
            OutputStreamWriter osw = new OutputStreamWriter(b, StandardCharsets.UTF_8);
            out = new PrintWriter(osw, true);
            out.println(data);
            answer = "";
            answer = in.readLine();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new LogoException(app, Logo.getString("no_host") + ip.getHostAddress());
        }
    }
}
