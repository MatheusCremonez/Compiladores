import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GraphicInterface extends JFrame{
	
	private JLabel label1;
	
	private JButton openFileButton;
	private JButton saveFileButton;
	private JButton compileButton;
	
	private JLabel fileLabel;
	private JTextArea fileText;
	private JLabel divisorLabel;
	private JLabel consoleLabel;
	private JTextArea consoleText;
	
	public GraphicInterface()
	{
		super("Compilador");
		setLayout(null);
		
		openFileButton = new JButton("Abrir Arquivo");
		openFileButton.setBounds(10, 10, 125, 35);
		add(openFileButton);
		
		saveFileButton = new JButton("Salvar Arquivo");
		saveFileButton.setBounds(145, 10, 125, 35);
		add(saveFileButton);
		
		fileLabel = new JLabel("Arquivo:");
		fileLabel.setBounds(10, 45, 125, 35);
		add(fileLabel);
		
		fileText = new JTextArea();
		add(fileText);
		
		JScrollPane fileTextScrollPane = new JScrollPane(fileText); 
		fileTextScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		fileTextScrollPane.setBounds(10, 80, 900, 250);
		add(fileTextScrollPane);
		
		consoleLabel = new JLabel("Console:");
		consoleLabel.setBounds(10, 330, 900, 35);
		add(consoleLabel);
		
		consoleText = new JTextArea();
		add(consoleText);
		
		JScrollPane consoleTextScrollPane = new JScrollPane(consoleText); 
		consoleTextScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleTextScrollPane.setBounds(10, 365, 900, 250);
		add(consoleTextScrollPane);
		
		compileButton = new JButton("Compilar");
		compileButton.setBounds(10, 635, 125, 35);
		add(compileButton);
		
		label1 = new JLabel();
		label1.setBounds(0, 1, 819, 460);
		label1.setHorizontalTextPosition(SwingConstants.CENTER);
		label1.setVerticalTextPosition(SwingConstants.TOP);
		add(label1);
		
		ButtonHandler handler = new ButtonHandler();
		openFileButton.addActionListener(handler);
		saveFileButton.addActionListener(handler);
		compileButton.addActionListener(handler);
	}
	
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{	
			if(event.getSource() == openFileButton) 
			{
				try {
					JFileChooser choice = new JFileChooser();
					choice.setFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
					choice.showOpenDialog(null);
					String name = choice.getSelectedFile().getAbsolutePath();
					
					if(!name.toLowerCase().endsWith(".txt"))
						name += ".txt";
					
					BufferedReader in = new BufferedReader(new FileReader(name));
					fileText.read(in, null);
					in.close();
					fileText.requestFocus();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if(event.getSource() == saveFileButton) 
			{				
				try {
					JFileChooser choice = new JFileChooser();
					choice.setFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
					choice.showSaveDialog(null);
					String name = choice.getSelectedFile().getAbsolutePath();
					
					if(!name.toLowerCase().endsWith(".txt"))
						name += ".txt";
					
					BufferedWriter out = new BufferedWriter(new FileWriter(name));
					out.write(fileText.getText());
					out.close();
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			if(event.getSource() == compileButton) 
			{
				//consoleText.setText(fileText.getText());
				// o arquivo a ser mandado para o analisador seria: fileText.getText()
				//SyntacticAnalyzer sa = new SyntacticAnalyzer();
			}
		}	
	}
}
