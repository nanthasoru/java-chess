package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class Inputs implements MouseInputListener, KeyListener
{

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        App.choosePiece(e.getY()/100, e.getX()/100);    
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        App.dropPiece(e.getY()/100, e.getX()/100);    
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        App.dragPiece(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_U:
                App.requestUndo();
                break;
            case KeyEvent.VK_Q:
                App.close();
                System.exit(0);
                break;
            case KeyEvent.VK_F:
                System.out.println(App.getFen());
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
}
