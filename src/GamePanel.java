import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics;

import javax.sound.sampled.*;
import javax.swing.*;

import java.util.Arrays;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener
{
	// game constants
	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	
	// coordinates for grid
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	
	// game variables
	int bodyParts = 6;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean gameRunning = false;
	boolean gameOver = false;
	boolean paused = false;
	
	Timer timer;
	Random random;
	GameFrame frame;
	
	Clip musicPlayer;
	
	
	GamePanel(GameFrame gf)
	{
		random = new Random();
		frame = gf;
		
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		
		File gameMusic = new File("snakeMusic.wav");
		
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(gameMusic);
			musicPlayer = AudioSystem.getClip();
			musicPlayer.open(audioStream);
			musicPlayer.loop(Clip.LOOP_CONTINUOUSLY);
			musicPlayer.start();
			musicPlayer.setMicrosecondPosition(500000);
		
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();		
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void startGame()
	{
		// sets timer delay and game state to running
		gameRunning = true;
		timer = new Timer(DELAY, this);
		
		// starts timer and places first apple randomly on screen
		newApple();
		timer.start();
	}
	
	public void retryGame()
	{
		gameOver = false;
		Arrays.fill(x, 0);
		Arrays.fill(y, 0);
		bodyParts = 6;
		applesEaten = 0;
		direction = 'R';
		startGame();
		
	}
	
	public void titleScreen(Graphics g)
	{
		if(!gameRunning && !gameOver)
		{
			g.setColor(Color.GREEN);
			g.setFont(new Font("Comic Sans", Font.BOLD, 55));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("WELCOME TO SNAKE", 
						(SCREEN_WIDTH - metrics.stringWidth("WELCOME TO SNAKE"))/2, 
						(SCREEN_HEIGHT - (g.getFont().getSize()))/3);
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("Comic Sans", Font.BOLD, 35));
			metrics = getFontMetrics(g.getFont());
			g.drawString("PRESS SPACE TO BEGIN", 
					(SCREEN_WIDTH - metrics.stringWidth("PRESS SPACE TO BEGIN"))/2, 
					(SCREEN_HEIGHT - (g.getFont().getSize()))/4*2);
		}
		
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g)
	{	
		if(gameRunning && !gameOver)
		{
			// draws background grid
			for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++)
			{
				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			}

			// draws apple
			g.setColor(Color.RED);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			// draws snake
			for(int i = 0; i < bodyParts; i++)
			{
				if(i == 0)
				{
					g.setColor(Color.GREEN);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}

				else
				{
					g.setColor(new Color(45, 180, 0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			// draws score
			g.setColor(Color.WHITE);
			g.setFont(new Font("Comic Sans", Font.PLAIN, 40));
			FontMetrics scoreMetrics = getFontMetrics(g.getFont());
			g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - 
						scoreMetrics.stringWidth("SCORE: " + applesEaten))/2, g.getFont().getSize());
		
			if(paused)
			{
				g.setFont(new Font("Comic Sans", Font.BOLD, 50));
				FontMetrics pauseMetrics = getFontMetrics(g.getFont());
				g.drawString("PAUSED", (SCREEN_WIDTH - pauseMetrics.stringWidth("PAUSED"))/2,
							(SCREEN_HEIGHT + g.getFont().getSize())/2);
			}
		}
		
		else if (!gameRunning && !gameOver)
			titleScreen(g);
		
		else
			gameOver(g);
	}
	
	public void newApple()
	{
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
	}
	
	public void move()
	{
		for(int i = bodyParts; i > 0; i--)
		{
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U' :
			y[0] = y[0] - UNIT_SIZE;
			break;
		
		case 'D' :
			y[0] = y[0] + UNIT_SIZE;
			break;
		
		case 'L' :
			x[0] = x[0] - UNIT_SIZE;
			break;
		
		case 'R' :
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple()
	{
		if((x[0] == appleX) && y[0] == appleY)
		{
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions()
	{
		// checks if head collides with body
		for(int i = bodyParts; i > 0; i--)
		{
			if((x[0] == x[i]) && (y[0] == y[i]))
				gameRunning = false;
		}
		
		
		// the following if blocks check if head collides with borders		
		// left border
		if(x[0] < 0)
			gameRunning = false;
		
		// right border
		if(x[0] > SCREEN_WIDTH - UNIT_SIZE)
			gameRunning = false;
		
		// top border
		if(y[0] < 0)
			gameRunning = false;
		
		// bottom border
		if(y[0] > SCREEN_HEIGHT - UNIT_SIZE)
			gameRunning = false;
		
		// stops game if snake crashes
		if(!gameRunning)
		{
			gameOver = true;
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g)
	{		
		// game over text
		g.setColor(Color.RED);
		g.setFont(new Font("Comic Sans", Font.BOLD, 75));
		FontMetrics metricsEnd = getFontMetrics(g.getFont());
		g.drawString("GAME OVER",
					(SCREEN_WIDTH - metricsEnd.stringWidth("GAME OVER"))/2, SCREEN_HEIGHT/2);

		// draws score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Comic Sans", Font.PLAIN, 40));
		FontMetrics metricsScore = getFontMetrics(g.getFont());
		g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - 
					metricsScore.stringWidth("SCORE: " + applesEaten))/2, 
					g.getFont().getSize());
		
		// draws retry text
		g.setFont(new Font("Comic Sans", Font.BOLD, 28));
		FontMetrics metricsRetry = getFontMetrics(g.getFont());
		g.drawString("PRESS SPACE TO RETRY", (SCREEN_WIDTH - metricsRetry.stringWidth("PRESS SPACE TO RETRY"))/2, 
					(SCREEN_HEIGHT - (g.getFont().getSize()))/3*2);
		g.drawString("OR", (SCREEN_WIDTH - metricsRetry.stringWidth("OR"))/2, 
				(SCREEN_HEIGHT - (g.getFont().getSize()))/3*2 + g.getFont().getSize());
		g.drawString("ESC TO QUIT", (SCREEN_WIDTH - metricsRetry.stringWidth("ESC TO QUIT"))/2, 
				(SCREEN_HEIGHT - (g.getFont().getSize()))/3*2 + (2*g.getFont().getSize()));
	}
	
	public void toggleTimer()
	{
		if(timer.isRunning())
			timer.stop();
		else
			timer.start();
		
		paused = !paused;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(gameRunning)
		{
			move();
			checkApple();
			checkCollisions();
		}
		
		repaint();
		
	}
	
	
	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch(e.getKeyCode()) {
			case KeyEvent.VK_SPACE:
				if(!gameRunning && !gameOver && !paused)
					startGame();
				else if(gameOver && !paused)
					retryGame();
				break;
				
			case KeyEvent.VK_LEFT:
				if(direction != 'R' && !gameOver && !paused)
					direction = 'L';
				break;
			
			case KeyEvent.VK_RIGHT:
				if(direction != 'L' && !gameOver && !paused)
					direction = 'R';
				break;
				
			case KeyEvent.VK_UP:
				if(direction != 'D' && !gameOver && !paused)
					direction = 'U';
				break;
				
			case KeyEvent.VK_DOWN:
				if(direction != 'U' && !gameOver && !paused)
					direction = 'D';
				break;
			case KeyEvent.VK_P:
				if(gameRunning && !gameOver)
				{
					toggleTimer();
					repaint();
				}
				break;
			case KeyEvent.VK_ESCAPE:
				frame.dispose();
				musicPlayer.close();
				break;
			}
		}
	}

}
