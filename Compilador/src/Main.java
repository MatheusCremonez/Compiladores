import javax.swing.JFrame;

public class Main{
	
	public static void main(String[] args) {		
		GraphicInterface gi = new GraphicInterface();
		gi.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gi.setSize(930,710);
		gi.setResizable(false);
		gi.setVisible(true);
	}
	
}
