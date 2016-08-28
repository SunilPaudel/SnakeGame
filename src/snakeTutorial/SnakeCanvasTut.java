package snakeTutorial;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;


public class SnakeCanvasTut extends Canvas implements Runnable, KeyListener {
	private static final long serialVersionUID = 1L;
	
	//define the box (of the grid) i.e. height and width
	private final int boxWidth =15;
	private final int boxHeight = 15;
	private final int gridWidth = 30;
	private final int gridHeight = 30;
	
	private LinkedList<Point> snake;
	private Point fruit;
	
	//to keep track fo the direction
	private int direction = Direction.noDirection;
	
	private Thread runThread;
	//Thread is capable of running the threads at the background
	
	private int score = 0;
	private String highscore = "";
	
	private boolean isInMenu = true;

	private Image menuImage = null;
	private boolean isAtEndGame = false;
	private boolean won = false;
		
	public void paint(Graphics g){
		if(runThread == null){
			this.setPreferredSize(new Dimension(640, 480));
			this.addKeyListener(this);			
			runThread = new Thread(this);
			runThread.start();
		}
		
		
		if(isInMenu){
			//draw the menu
			DrawMenu(g);
		}
		else if(isAtEndGame){
			//draw the end game screen
			DrawEndGame(g);
		}
		else{
			//draw everything
			if(snake == null){
				snake = new LinkedList<Point>();
				GenerateDefaultSnake();
				//fruit = new Point(10,10);
				PlaceFruit();
			}
			if(highscore.equals("")){
				//initialise the highscore
				highscore = this.GetHighScore();
			}
			
			DrawFruit(g);
			DrawGrid(g);
			DrawSnake(g);
			DrawScore(g);
		}
		
	}
	
	public void DrawEndGame(Graphics g){
		BufferedImage endGameImage = new BufferedImage(this.getPreferredSize().width,this.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics endGameGraphics = endGameImage.getGraphics();
		endGameGraphics.setColor(Color.black);
		
		if(won)
			endGameGraphics.drawString("Congrats You Won", this.getPreferredSize().width / 2, this.getPreferredSize().height/2);
		else
			endGameGraphics.drawString("You Lost. Try Again", this.getPreferredSize().width/2, this.getPreferredSize().height/2);
		
		endGameGraphics.drawString("The score is: " + score,this.getPreferredSize().width/2, (this.getPreferredSize().height/2 + 10));
		endGameGraphics.drawString("Press \"Spacebar\" to start a new game",this.getPreferredSize().width/2, (this.getPreferredSize().height/2 + 40));
		
		g.drawImage(endGameImage,0,0,this);
			
		
	}
	
	public void DrawMenu(Graphics g){
		if(this.menuImage == null){
			try{
				URL imagePath = SnakeCanvasTut.class.getResource("Menu.png");
				menuImage = Toolkit.getDefaultToolkit().getImage(imagePath);
			}
			catch(Exception e){
				e.printStackTrace();
			}

		}
				
		g.drawImage(menuImage, 0,0,640,480,this);
	}
	
	
	public void update(Graphics g){
		//this is the default update method which will contain our double buffering
		Graphics offScreenGraphics; //these are the graphics which will be used for drawing offscreen
		BufferedImage offScreen = null;
		Dimension d = this.getSize();
		
		offScreen = new BufferedImage(d.width,d.height, BufferedImage.TYPE_INT_ARGB);
		offScreenGraphics = offScreen.getGraphics();
		offScreenGraphics.setColor(this.getBackground());
		offScreenGraphics.fillRect(0,0,d.width,d.height);
		offScreenGraphics.setColor(this.getForeground());
		paint(offScreenGraphics);
		
		//flip
		g.drawImage(offScreen, 0, 0, this);
		
				
	}
	
	public void GenerateDefaultSnake(){
		score = 0;
		snake.clear();
		snake.add(new Point(0,2));
		snake.add(new Point(0,1));
		snake.add(new Point(0,0));
		direction = Direction.noDirection;
	}
	
	//the snake keeps on moving
	public void Move(){
		if(direction == Direction.noDirection){
			return;
		}
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch(direction){
		case Direction.north:
			newPoint = new Point(head.x, head.y-1);
			break;
		case Direction.south:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.west:
			newPoint = new Point(head.x-1, head.y);
			break;
		case Direction.east:
			newPoint = new Point(head.x+1, head.y);
			break;		
		}
		
		//remove the tail first and then add the head
		if(this.direction != Direction.noDirection)
			snake.remove(snake.peekLast());
		
		//you can run into fruits/walls/into yourself
		if(newPoint.equals(fruit)){
			//the snake has hit the fruit
			/*Point butt = snake.peekLast();
			snake.addLast(butt);*/
			
			//everytime snake eats fruit, increase the score by 10
			score += 10;
			
			Point addPoint = (Point) newPoint.clone();
			switch(direction){
			case Direction.north:
				newPoint = new Point(head.x, head.y-1);
				break;
			case Direction.south:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.west:
				newPoint = new Point(head.x-1, head.y);
				break;
			case Direction.east:
				newPoint = new Point(head.x+1, head.y);
				break;		
			}
			snake.push(addPoint);
			PlaceFruit();
			
		} else if(newPoint.x < 0 || newPoint.x > gridWidth -1 ){
			//we went out of bounce, reset the game
			/*runThread = new Thread(this);
			runThread.start();*/
			CheckScore();
			won = false;
			isAtEndGame= true;
			return;
			
		} else if(newPoint.y < 0 || newPoint.y > gridHeight -1){
			//we went out of bounce, reset the game
			CheckScore();
			won = false;
			isAtEndGame= true;
			return;
			
		} else if(snake.contains(newPoint)){
			//snake touched the body,reset the game
			if(direction != Direction.noDirection){
				CheckScore();
				won = false;
				isAtEndGame= true;
				return;	
			}
			
		}
		else if(snake.size() == (gridWidth * gridHeight)){
			//we won
			CheckScore();
			won = true;
			isAtEndGame = true;
			return;
		}
		
		//reaching this means, we are still on the game
		snake.push(newPoint);
	}
	
	public void DrawScore(Graphics g){
		g.drawString("Your Score is: " + score, 0, boxHeight * gridHeight + 10);
		g.drawString("High Score" + highscore, 0, boxHeight * gridHeight + 20);
	}
	
	public void CheckScore(){
		//since the format is Sunil:100
		//split the string as : 
		//System.out.println(highscore);
		
		if (highscore.equals(""))
			return;
		
		if (score > Integer.parseInt(highscore.split(":")[1])){
			//user got the highest score
			String name = JOptionPane.showInputDialog("You set the high score. Your name Please");
			highscore = name + ":" + score;	
			
			File scoreFile = new File("highscore.dat");
			
			if (!scoreFile.exists())
				try {
					scoreFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			FileWriter writeFile = null;
			BufferedWriter writer = null;
			try {
				writeFile = new FileWriter(scoreFile);
				writer = new BufferedWriter(writeFile);
				
				writer.write(this.highscore);
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			finally{
				if(writer != null)
					try {
						writer.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			 
		}
	}
	
	public void DrawGrid(Graphics g){
		//draw an outside rectangle
		g.drawRect(0, 0, gridWidth * boxWidth, gridHeight * boxHeight);
		
		//draw vertical lines
		for (int x = boxWidth; x < boxWidth * gridWidth; x+=boxWidth) {
			g.drawLine(x, 0, x, boxWidth*gridWidth);
		}
		
		//draw horizontal lines
		for(int y = boxHeight; y< boxHeight * gridHeight; y+=boxHeight){
			g.drawLine(0, y, gridHeight*boxHeight, y);
		}
			
	}
	
	//drawSnake now
	public void DrawSnake(Graphics g){
		g.setColor(Color.green);
		
		//for every point of the snake
		for (Point p : snake) {
			g.fillRect(p.x *boxWidth, p.y * boxHeight, boxWidth, boxHeight);
		}
		
		g.setColor(Color.black);
	}
	
	//draw the fruit
	public void DrawFruit(Graphics g){
		g.setColor(Color.red);
		g.fillOval(fruit.x * boxWidth, fruit.y * boxHeight, boxWidth, boxHeight);
		g.setColor(Color.black);
	}
	
	public void PlaceFruit(){
		Random rand = new Random();
		int randomX = rand.nextInt(gridWidth);
		int randomY = rand.nextInt(gridHeight);
		Point randomPoint = new Point(randomX, randomY);
		
		//keep generating the random point till it is off the snake
		while(snake.contains(randomPoint)){
			//if snake contains randomPoint, generate the random point
			randomX = rand.nextInt(gridWidth);
			randomY = rand.nextInt(gridHeight);
			randomPoint = new Point(randomX, randomY);
		}
		
		fruit = randomPoint;
	}

	@Override
	public void run() {
		// Run Method
		while(true){
			//runs indefinitely
			repaint();
			
			if(!isInMenu && !isAtEndGame)
				Move();
			
			try {
				Thread.currentThread();
				Thread.sleep(100); //the game will slow down by 100ms
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public String GetHighScore()
	{
		//format: Sunil: 100
		FileReader readFile = null;
		BufferedReader reader = null;
		try {
			readFile = new FileReader("highscore.dat");
			reader = new BufferedReader(readFile);
			try {
				return reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return "Nobody:0";
		}
		finally{
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return highscore;
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			if (direction != Direction.south)
				direction = Direction.north;			
			break;
		case KeyEvent.VK_DOWN:
			if (direction != Direction.north)
				direction = Direction.south;
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != Direction.west)
				direction = Direction.east;
			break;
		case KeyEvent.VK_LEFT:
			if (direction != Direction.east)
				direction = Direction.west;
			break;
		case KeyEvent.VK_ENTER:
			if(isInMenu){
				isInMenu = false;
				repaint();
			}
			break;
		case KeyEvent.VK_ESCAPE:
			isInMenu = true;
			break;	
		case KeyEvent.VK_SPACE:
			if(isAtEndGame){
				isAtEndGame = false;
				won = false;
				GenerateDefaultSnake();
				repaint();
			}
			break;
		}
		
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
