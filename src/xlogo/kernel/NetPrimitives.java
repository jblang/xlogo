/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.kernel.network.NetworkClientChat;
import xlogo.kernel.network.NetworkClientExecute;
import xlogo.kernel.network.NetworkClientSend;
import xlogo.kernel.network.NetworkServer;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Network-related primitives: TCP client/server communication.
 */
public class NetPrimitives extends PrimitiveGroup {

    public NetPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("net.chattcp", 2, false, this::chatTcp),
            new Primitive("net.executetcp", 2, false, this::executeTcp),
            new Primitive("net.listentcp", 0, false, this::listenTcp),
            new Primitive("net.sendtcp", 2, false, this::sendTcp)
        );
    }

    void chatTcp(Stack<String> param) {
        String mot;
        String liste;
        setReturnValue(false);
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            } catch (LogoException ignored) {
            }
        }
        mot = Objects.requireNonNull(mot).toLowerCase();
        try {
            liste = getFinalList(param.get(1));
            new NetworkClientChat(app, mot, liste);
        } catch (LogoException ignored) {
        }
    }

    void executeTcp(Stack<String> param) {
        String mot;
        String liste;
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            } catch (LogoException ignored) {
            }
        }
        mot = Objects.requireNonNull(mot).toLowerCase();
        try {
            liste = getFinalList(param.get(1));
            new NetworkClientExecute(app, mot, liste);
        } catch (LogoException ignored) {
        }
    }

    void listenTcp(Stack<String> param) {
        setReturnValue(false);
        if (null == interpreter.savedWorkspace) interpreter.savedWorkspace = new Stack<>();
        interpreter.savedWorkspace.push(getWorkspace());
        new NetworkServer(app);
    }

    void sendTcp(Stack<String> param) {
        String liste;
        String mot;
        setReturnValue(true);
        mot = getWord(param.get(0));
        if (null == mot) {
            try {
                throw new LogoException(app, param.get(0) + " " + Logo.getString("interpreter.error.wordRequired"));
            } catch (LogoException ignored) {
            }
        }
        mot = Objects.requireNonNull(mot).toLowerCase();
        try {
            liste = getFinalList(param.get(1));
            NetworkClientSend ncs = new NetworkClientSend(app, mot, liste);
            pushResult("[ " + ncs.getAnswer() + " ] ");
        } catch (LogoException ignored) {
        }
    }
}
