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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GraphicInterface extends JFrame {

	// Indentificador de versão de serialização da classe
	private static final long serialVersionUID = 1L;

	private JMenuBar menu;
	private JMenu arquivo;
	private JMenuItem abrir;
	private JMenuItem salvar;

	private JButton compileButton;

	private JLabel fileLabel;
	private JTextArea fileText;
	private JLabel consoleLabel;
	private JTextArea consoleText;

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

		fileLabel = new JLabel("Arquivo:");
		fileLabel.setBounds(10, 5, 125, 35);
		add(fileLabel);

		fileText = new JTextArea();
		add(fileText);

		JScrollPane fileTextScrollPane = new JScrollPane(fileText);
		fileTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		fileTextScrollPane.setBounds(10, 40, 900, 250);
		add(fileTextScrollPane);

		consoleLabel = new JLabel("Console:");
		consoleLabel.setBounds(10, 290, 900, 35);
		add(consoleLabel);

		consoleText = new JTextArea();
		consoleText.setEditable(false);
		add(consoleText);

		JScrollPane consoleTextScrollPane = new JScrollPane(consoleText);
		consoleTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleTextScrollPane.setBounds(10, 325, 900, 250);
		add(consoleTextScrollPane);

		compileButton = new JButton("Compilar");
		compileButton.setBounds(10, 595, 125, 35);
		add(compileButton);

		MenuActionListener menuHandler = new MenuActionListener();
		abrir.addActionListener(menuHandler);
		salvar.addActionListener(menuHandler);

		ButtonHandler handler = new ButtonHandler();
		compileButton.addActionListener(handler);
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
				LexicalAnalyzer la = new LexicalAnalyzer(fileText.getText());
				consoleText.setText(la.getMessage());
			}
		}
	}
}
