package xlogo.kernel;

import xlogo.Logo;
import xlogo.gui.Editor;
import xlogo.gui.MessageTextArea;

import javax.swing.*;
import java.util.ArrayList;

public class SyntaxException extends Exception {   // à générer en cas d'errreur dans la structure
    Editor editor;

    SyntaxException(Workspace workspace, String message) {        // et des variables
        this.editor = workspace.app.editor;
        MessageTextArea jt = new MessageTextArea(message);
        JOptionPane.showMessageDialog(this.editor, jt, Logo.messages.getString("erreur"), JOptionPane.ERROR_MESSAGE);
        for (int i = 0; i < workspace.getNumberOfProcedure(); i++) { // On remémorise les anciennes définitions de procédures
            Procedure pr = workspace.getProcedure(i);
            pr.variable = new ArrayList<>(pr.variable_sauve);
            pr.instr = pr.instr_sauve;
            pr.instruction = pr.instruction_sauve;
            pr.nbparametre = pr.variable.size();
        }
        editor.toFront();
        editor.focusTextZone();
    }
}
