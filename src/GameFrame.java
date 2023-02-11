import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class GameFrame extends JFrame
{
	GamePanel panel;

	GameFrame()
	{
		panel = new GamePanel(this);				
		this.add(panel);

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
