
package com.hossam.notepad.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UndoAction extends AbstractAction {

    private final MainFrame frame;

    public UndoAction(MainFrame frame) {
        super("Undo");
        this.frame = frame;
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke("control Z"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.undo();
    }
}
