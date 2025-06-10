package main;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class Inputs implements MouseInputListener 
{

    @Override
    public void mouseClicked(MouseEvent e)
    {
        App.gamePanel.evalMouseEvent(e.getY(), e.getX());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseClicked(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        App.gamePanel.setSlide(false);
        mouseClicked(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        App.gamePanel.setSlide(true);
        App.gamePanel.setXY(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}
