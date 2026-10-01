package client.java.io.quicksiiver.traction.misc;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import main.java.io.quicksiiver.traction.Main;

public class MainWindowListener implements WindowListener {
    
    @Override
    public void windowClosing(WindowEvent e) { Main.stop(); }

    // these are just here to satisfy the implements
    @Override
    public void windowClosed(WindowEvent e) {  }
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
}
