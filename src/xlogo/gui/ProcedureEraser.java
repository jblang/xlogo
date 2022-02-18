/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo
 * programming language
 *
 * @author Loïc Le Coq
 */
package xlogo.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import xlogo.Logo;
import xlogo.kernel.Procedure;
import xlogo.kernel.Workspace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ProcedureEraser extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JScrollPane scroll;
    private JButton deleteButton;
    private JButton up;
    private JButton down;
    private JList procedureList;
    private JLabel allProcedureLabel;
    private final ImageIcon iup =  new FlatSVGIcon("xlogo/icons/moveUp.svg");
    private final ImageIcon idown =  new FlatSVGIcon("xlogo/icons/moveDown.svg");
    private JPanel buttonPanel;
    private Vector<String> list;
    private final Workspace wp;
    private int startupFiles = 0;

    public ProcedureEraser(Application app) throws HeadlessException {
        super(app);
        this.wp = app.getKernel().getWorkspace();
        initGui();
    }

    private void initGui() {
        up = new JButton(iup);
        down = new JButton(idown);
        up.setActionCommand("up");
        down.setActionCommand("down");
        up.addActionListener(this);
        down.addActionListener(this);
        buttonPanel = new JPanel();
        buttonPanel.add(up);
        buttonPanel.add(down);

        list = new Vector<String>();

        for (int i = 0; i < wp.getNumberOfProcedure(); i++) {
            Procedure proc = wp.getProcedure(i);
            if (proc.affichable) {
                if (list.size() == 0) startupFiles = i;
                list.add(proc.getName());
            }
        }
        procedureList = new JList(list);
        deleteButton = new JButton(Logo.messages.getString("enlever"));
        allProcedureLabel = new JLabel(Logo.messages.getString("procedure_list"));
        scroll = new JScrollPane(procedureList);

        getContentPane().setLayout(new BorderLayout());
        deleteButton.setActionCommand("delete");
        deleteButton.addActionListener(this);
        getContentPane().add(allProcedureLabel, BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);
        getContentPane().add(deleteButton, BorderLayout.SOUTH);
        getContentPane().add(buttonPanel, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        int[] indices = procedureList.getSelectedIndices();
        if ("delete".equals(cmd)) {
            for (int i = indices.length - 1; i > -1; i--) {
                wp.deleteProcedure(indices[i] + startupFiles);
                list.removeElementAt(indices[i]);
                procedureList.setListData(list);
            }
        } else if ("up".equals(cmd)) {
            if (indices.length > 0 && indices[0] != 0) {
                for (int i = 0; i < indices.length; i++) {
                    int id = indices[i] + startupFiles;
                    Procedure tampon = wp.getProcedure(id - 1);
                    wp.setProcedureList(id - 1, wp.getProcedure(id));
                    wp.setProcedureList(id, tampon);
                    String st = list.get(indices[i] - 1);
                    list.set(indices[i] - 1, list.get(indices[i]));
                    list.set(indices[i], st);
                    indices[i]--;
                }
                procedureList.setListData(list);
                procedureList.setSelectedIndices(indices);
            }
        } else if ("down".equals(cmd)) {
            if (indices.length > 0 && indices[indices.length - 1] != list.size() - 1) {
                for (int i = indices.length - 1; i > -1; i--) {
                    int id = indices[i] + startupFiles;
                    Procedure tampon = wp.getProcedure(id + 1);
                    wp.setProcedureList(id + 1, wp.getProcedure(id));
                    wp.setProcedureList(id, tampon);
                    String st = list.get(indices[i] + 1);
                    list.set(indices[i] + 1, list.get(indices[i]));
                    list.set(indices[i], st);
                    indices[i]++;
                }
                procedureList.setListData(list);
                procedureList.setSelectedIndices(indices);

            }
        }
    }
}
