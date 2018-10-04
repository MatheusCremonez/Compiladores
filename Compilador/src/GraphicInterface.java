import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GraphicInterface extends JFrame {

	// Indentificador de vers�o de serializa��o da classe
	private static final long serialVersionUID = 1L;

	private JMenuBar menu;
	private JMenu arquivo;
	private JMenuItem abrir;
	private JMenuItem salvar;

	private JButton compileButton;

	private JTextArea fileText;
	private JTextArea lineText;
	private JLabel consoleLabel;
	private JTextArea consoleText;
	
	// private int errorLine;

	public GraphicInterface() {

		super("Compilador");
		setLayout(null);

		menu = new JMenuBar();
		arquivo = new JMenu("Arquivo");
		abrir = new JMenuItem("Abrir");
		salvar = new JMenuItem("Salvar");

		arquivo.add(abrir);
		arquivo.add(salvar);
		menu.add(arquivo);

		setJMenuBar(menu);
		
		lineText = new JTextArea();
		lineText.setBackground(Color.LIGHT_GRAY);
		lineText.setEditable(false);
		add(lineText);
		
		JScrollPane lineTextScrollPane = new JScrollPane(lineText);
		lineTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		lineTextScrollPane.setBounds(10, 10, 20, 415);
		add(lineTextScrollPane);

		fileText = new JTextArea();
		add(fileText);

		JScrollPane fileTextScrollPane = new JScrollPane(fileText);
		fileTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		fileTextScrollPane.setBounds(30, 10, 880, 415);
		fileTextScrollPane.getVerticalScrollBar().setModel(lineTextScrollPane.getVerticalScrollBar().getModel());
		add(fileTextScrollPane);

		consoleLabel = new JLabel("Console:");
		consoleLabel.setBounds(10, 420, 900, 35);
		add(consoleLabel);

		consoleText = new JTextArea();
		consoleText.setEditable(false);
		add(consoleText);

		JScrollPane consoleTextScrollPane = new JScrollPane(consoleText);
		consoleTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleTextScrollPane.setBounds(10, 450, 900, 150);
		add(consoleTextScrollPane);

		compileButton = new JButton("Compilar");
		compileButton.setBounds(10, 610, 125, 35);
		add(compileButton);

		MenuActionListener menuHandler = new MenuActionListener();
		abrir.addActionListener(menuHandler);
		salvar.addActionListener(menuHandler);

		ButtonHandler buttonHandler = new ButtonHandler();
		compileButton.addActionListener(buttonHandler);
		
		ClickHandler clickHandler = new ClickHandler();
		consoleText.addMouseListener(clickHandler);
		
		KeyHandler keyHandler = new KeyHandler();
		fileText.addKeyListener(keyHandler);
	}

	public class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			if (event.getSource() == abrir) {
				try {
					JFileChooser choice = new JFileChooser();
					choice.setFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
					choice.showOpenDialog(null);
					String name = choice.getSelectedFile().getAbsolutePath();

					if (!name.toLowerCase().endsWith(".txt"))
						name += ".txt";

					BufferedReader in = new BufferedReader(new FileReader(name));
					fileText.read(in, null);
					in.close();
					fileText.requestFocus();
					setFileLines();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			if (event.getSource() == salvar) {
				try {
					JFileChooser choice = new JFileChooser();
					choice.setFileFilter(new FileNameExtensionFilter("TXT files", "txt"));
					choice.showSaveDialog(null);
					String name = choice.getSelectedFile().getAbsolutePath();

					if (!name.toLowerCase().endsWith(".txt"))
						name += ".txt";

					BufferedWriter out = new BufferedWriter(new FileWriter(name));
					out.write(fileText.getText());
					out.close();

					JOptionPane.showMessageDialog(null, "Arquivo Salvo com Sucesso");

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ButtonHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			if (event.getSource() == compileButton) {
				// setFileLines();
				SyntacticAnalyzer sa = new SyntacticAnalyzer(fileText.getText());
				consoleText.setText(sa.getMessage());
//				if (sa.getErrorLine() > 0) {
//					errorLine = sa.getErrorLine();
//				}
			}
		}
	}
	
	private class KeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {
				setFileLines();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				setFileLines();
			}
			
		}

		@Override
		public void keyTyped(KeyEvent e) { }

	}
	
	private class ClickHandler implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent event) {
			// consoleText.setText(String.valueOf(errorLine));
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	public void setFileLines() {
		int i = 1, fileIndex = 0;
		String fileContent = fileText.getText();
		String lineContent = "";
		
		if (fileIndex < fileContent.length()) {
			lineContent = "1\n";
		}
		
		while (fileIndex < fileContent.length()) {
			if(fileContent.charAt(fileIndex) == '\n') {
				i++;
				lineContent = lineContent + String.valueOf(i) + '\n';
			}
			fileIndex++;
		}
		
		lineText.setText(lineContent);
		
	}
		
}
