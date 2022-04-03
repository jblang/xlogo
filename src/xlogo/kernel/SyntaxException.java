package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Editor;
import xlogo.gui.MessageTextArea;

import javax.swing.*;
import java.util.ArrayList;

public class SyntaxException extends Exception {   // to generate in case of error in the structure
    Editor editor;

    SyntaxException(Workspace workspace, String message) {        // and variables
        this.editor = workspace.app.editor;
        MessageTextArea jt = new MessageTextArea(message);
        JOptionPane.showMessageDialog(this.editor, jt, Logo.getString("application.error"), JOptionPane.ERROR_MESSAGE);
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) { // We remember the old definitions of procedures
            Procedure pr = workspace.getProcedure(i);
            pr.variables = new ArrayList<>(pr.backupVariables);
            pr.formattedBody = pr.backupFormattedBody;
            pr.body = pr.backupBody;
            pr.arity = pr.variables.size();
        }
        editor.toFront();
        editor.focusTextArea();
    }
}
