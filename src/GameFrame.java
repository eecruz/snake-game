import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class GameFrame extends JFrame
{
	GamePanel panel;

	GameFrame()
	{
		panel = new GamePanel(this);				
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);

		ImageIcon image = new ImageIcon("snakepic.png");
		this.setIconImage(image.getImage());
		

		this.setTitle("Snake!");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(null);

	}
}