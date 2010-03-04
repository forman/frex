package z.ui.application;

import javax.swing.JFrame;

public interface ApplicationWindow {
    JFrame getShell();

    ApplicationPage getPage();

    void close();

    void setStatusMessage(String text);
}
