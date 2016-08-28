package snakeTutorial;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MovingRectangle extends Applet implements KeyListener {
	
	private static final long serialVersionUID = 1L;
	private Rectangle rect;
	
	public void init(){
		this.addKeyListener(this);
		rect = new Rectangle(0,0,50,50);
	}
	
	public void paint(Graphics g){
		setSize(400, 400);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.fill(rect);
		//rect = new Rectangle(0,0,50,50);
		
		//g.fill3DRect(x, y, width, height, raised);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("Something");
		if(e.getKeyCode() == KeyEvent.VK_UP){
			rect.setLocation(rect.x, rect.y - 2 );
			//System.out.println("Up Pressed");
		} 
		else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			rect.setLocation(rect.x, rect.y + 2);
			//System.out.println("Down pressed");
		} 
		else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			rect.setLocation(rect.x - 2, rect.y);
			//System.out.println("Left Pressed");
		} 
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			rect.setLocation(rect.x + 2, rect.y);
			//System.out.println("Right Pressed");
		}
		
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
