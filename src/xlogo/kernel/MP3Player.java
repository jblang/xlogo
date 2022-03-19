package xlogo.kernel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import xlogo.gui.GraphFrame;
import xlogo.Logo;
import xlogo.utils.LogoException;
import xlogo.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class MP3Player extends Thread {
    private AdvancedPlayer player;

    MP3Player(GraphFrame graphFrame, String path) throws LogoException {
        try {
            // Build absolutePath
            String absolutePath = Utils.unescapeString(Logo.config.getDefaultFolder())
                    + File.separator + Utils.unescapeString(path);
            player = new AdvancedPlayer(new FileInputStream(absolutePath));
        } catch (FileNotFoundException e) {
            // tentative fichier réseau
            try {
                URL url = new java.net.URL(path);
                java.io.InputStream fr = url.openStream();
                player = new AdvancedPlayer(fr);
            } catch (java.net.MalformedURLException e1) {

                throw new LogoException(graphFrame, Logo.messages.getString("error.iolecture"));


            } catch (IOException e2) {
            } catch (JavaLayerException e3) {
            }
        } catch (JavaLayerException e4) {
        }

    }

    public void run() {
        try {
            player.play();
        } catch (JavaLayerException e) {
        }
    }

    protected AdvancedPlayer getPlayer() {
        return player;
    }

}
