import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Main{
	
	public static void main(String[] args) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenHeight = screenSize.getHeight();
		//double screenWidth = screenSize.getWidth();
		
		GraphicInterface gi = new GraphicInterface();
		gi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gi.setSize(930,(int)(0.67*screenHeight));
		gi.setResizable(false);
		gi.setVisible(true);
	}
	
}
