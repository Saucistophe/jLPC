package org.saucistophe.lpc.client.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.saucistophe.lpc.business.Sentence;
import org.saucistophe.lpc.client.display.pictures.IndexedButton;
import org.saucistophe.lpc.client.display.pictures.IndexedLabel;
import org.saucistophe.lpc.client.display.pictures.PictureRenderer;
import org.saucistophe.lpc.client.display.sentence.FreeSentenceDisplayPanel;
import org.saucistophe.lpc.utils.Constants;
import org.saucistophe.settings.SettingsHandler;
import org.saucistophe.swing.FileComponentsUtils;

public class MainApplet extends JApplet
{
	private static MainApplet instance = null;

	private static final long serialVersionUID = 1L;
	public static Font defaultFont = null;
	public static Font biggerFont = null;
	public static Font biggestFont = null;
	public FreeSentenceDisplayPanel freeSentencePanel;

	private static File currentFile = null;

	/**
	 @return The one and only instance of this class.
	 */
	public static MainApplet getInstance()
	{
		if (instance == null)
		{
			instance = new MainApplet();
		}
		return instance;
	}

	@Override
	public void init()
	{
		super.init();

		try
		{
			// Load Noto fonts
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MainApplet.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MainApplet.class.getResourceAsStream("/fonts/NotoSans-Bold.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MainApplet.class.getResourceAsStream("/fonts/NotoSans-BoldItalic.ttf")));
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MainApplet.class.getResourceAsStream("/fonts/NotoSans-Italic.ttf")));

			defaultFont = new Font("NotoSans", Font.PLAIN, 14);
			biggerFont = new Font("NotoSans", Font.PLAIN, 20);
			biggestFont = new Font("NotoSans", Font.PLAIN, 24);

			// Set the renderer's default font.
			PictureRenderer.font = defaultFont;
		} catch (FontFormatException | IOException ex)
		{
			Logger.getLogger(MainApplet.class.getName()).log(Level.SEVERE, null, ex);
		}

		// Create the GUI.
		try
		{
			javax.swing.SwingUtilities.invokeAndWait(this::setUpGui);
		} catch (InterruptedException | InvocationTargetException e)
		{
			System.err.println("createGUI didn't successfully complete.");
			Logger.getLogger(MainApplet.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public static void main(String[] args) throws ClassNotFoundException,
		InstantiationException, IllegalAccessException,
		UnsupportedLookAndFeelException
	{
		// First load settings.
		SettingsHandler.readFromFile();

		// Create an applet
		JFrame frame = new JFrame("jLPC");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MainApplet mainApplet = MainApplet.getInstance();
		mainApplet.init();

		frame.add(mainApplet);
		frame.pack();
		frame.setVisible(true);
	}

	// Sets up the gui and settings.
	private void setUpGui()
	{
		try
		{
			// Change the look 'n' feel.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
		{
			Logger.getLogger(MainApplet.class.getName()).log(Level.SEVERE, null, ex);
		}

		setBackground(Color.white);

		// Add the input and visualisation display to the panel.
		freeSentencePanel = new FreeSentenceDisplayPanel();
		add(freeSentencePanel, BorderLayout.CENTER);

		// Create the menu bar.
		setMenuBar();
	}

	private void setMenuBar()
	{
		// Add a menu.
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("Fichier");
		{
			// New file item
			JMenuItem newFileItem = new JMenuItem("Nouveau");
			newFileItem.addActionListener(e ->
			{
				// Empty the current sentence list.
				freeSentencePanel.sentenceDictionnaryPanel.sentenceListModel.clear();

				// Also lose current file context.
				currentFile = null;
			});
			fileMenu.add(newFileItem);

			// Open file item
			JMenuItem openFileItem = new JMenuItem("Ouvrir");
			openFileItem.addActionListener(e ->
			{
				// Chose a file to open.
				File chosenFile;
				chosenFile = FileComponentsUtils.chooseFile("lpc", null);

				if (chosenFile != null)
				{
					// Warn if the file doesn't exist.
					if (!chosenFile.exists())
					{
						JOptionPane.showMessageDialog(this, "Impossible d'ouvrir le fichier " + chosenFile.getName(), "Erreur de lecture", ERROR_MESSAGE);
						return;
					}

					// Empty the current sentence list.
					freeSentencePanel.sentenceDictionnaryPanel.sentenceListModel.clear();

					String line;
					int lineIndex = 1;

					try (
						InputStream fileInputStream = new FileInputStream(chosenFile);
						InputStreamReader reader = new InputStreamReader(fileInputStream, Constants.ENCODING);
						BufferedReader bufferedReader = new BufferedReader(reader);)
					{
						while ((line = bufferedReader.readLine()) != null)
						{
							// Parse the line into a sentence.
							freeSentencePanel.sentenceDictionnaryPanel.sentenceListModel.addElement(Sentence.fromStorageString(line));

							lineIndex++;
						}
					} catch (IOException ex)
					{
						Logger.getLogger(MainApplet.class.getName()).log(Level.SEVERE, null, ex);
					} catch (ParseException parseException)
					{
						// Tell the user something is wrong in his file.
						JOptionPane.showMessageDialog(this, "Erreur de lecture ligne " + lineIndex + ", colonne " + parseException.getErrorOffset() + ": " + parseException.getMessage(), "Erreur de syntaxe", ERROR_MESSAGE);
					}
					// Keep the file as the active one.
					currentFile = chosenFile;
				}
			});
			fileMenu.add(openFileItem);

			// Save file item
			JMenuItem saveFileItem = new JMenuItem("Enregistrer");
			saveFileItem.addActionListener(e -> saveToFile(currentFile));
			fileMenu.add(saveFileItem);

			// "Save as" file item
			JMenuItem saveAsFileItem = new JMenuItem("Enregistrer sous...");
			saveAsFileItem.addActionListener(e -> saveToFile(null));

			fileMenu.add(saveAsFileItem);
		}
		menuBar.add(fileMenu);

		JMenu toolsMenu = new JMenu("Outils");
		menuBar.add(toolsMenu);
		JMenuItem settingsItem = new JMenuItem("Préférences");
		settingsItem.addActionListener(e ->
		{
			SettingsHandler.showSettingsDialog();
			repaint();
		});
		toolsMenu.add(settingsItem);

		// Add a display menu.
		JMenu displayMenu = new JMenu("Affichage");
		menuBar.add(displayMenu);

		ButtonGroup group = new ButtonGroup();
		// Normal font
		JRadioButtonMenuItem normalFontItem = new JRadioButtonMenuItem("Police normale");
		//  normalFontItem.setSelected(true);
		normalFontItem.addActionListener(e ->
		{
			IndexedButton.setAllFonts(defaultFont);
			IndexedLabel.setAllFonts(defaultFont);
			PictureRenderer.font = defaultFont;
			MainApplet.getInstance().repaint();
		});
		group.add(normalFontItem);

		// Bigger font
		JRadioButtonMenuItem biggerFontItem = new JRadioButtonMenuItem("Police large");
		// biggerFontItem.setSelected(true);
		biggerFontItem.addActionListener(e ->
		{
			IndexedButton.setAllFonts(biggerFont);
			IndexedLabel.setAllFonts(biggerFont);
			PictureRenderer.font = biggerFont;
			MainApplet.getInstance().repaint();
		});
		group.add(biggerFontItem);

		// Biggest font
		JRadioButtonMenuItem biggestFontItem = new JRadioButtonMenuItem("Police très large");
		//  biggestFontItem.setSelected(true);
		biggestFontItem.addActionListener(e ->
		{
			IndexedButton.setAllFonts(biggestFont);
			IndexedLabel.setAllFonts(biggestFont);
			PictureRenderer.font = biggestFont;
			MainApplet.getInstance().repaint();
		});
		group.add(biggestFontItem);
		// Add those radio buttons together.
		displayMenu.add(normalFontItem);
		displayMenu.add(biggerFontItem);
		displayMenu.add(biggestFontItem);

		displayMenu.addSeparator();
	}

	/**
	 Saves the current sentences to the current file, if any.

	 @param saveFile The file where the data must be saved.
	 */
	public void saveToFile(File saveFile)
	{
		// Retrieve the sentences to save from the display.
		List<Sentence> sentencesToSave = freeSentencePanel.sentenceDictionnaryPanel.getSentences();

		// Check if a file is saved. If not, chose another.
		File chosenFile = saveFile;
		if (chosenFile == null)
		{
			chosenFile = FileComponentsUtils.chooseFile("lpc", null);
		}
		if (chosenFile != null)
		{

			// Add them to the file.
			try (PrintWriter out = new PrintWriter(chosenFile, Constants.ENCODING))
			{
				for (Sentence sentence : sentencesToSave)
				{
					out.println(sentence.toStorageString());
				}
			} catch (IOException exception)
			{
				Logger.getLogger(MainApplet.class.getName()).log(Level.SEVERE, "Can''t save sentences {0} to file {1}", new Object[]
				{
					sentencesToSave.size(), chosenFile.getAbsolutePath()
				});
				Logger.getLogger(MainApplet.class.getName()).log(Level.SEVERE, null, exception);
			}

			// Keep the file as the active one.
			currentFile = chosenFile;
		}
	}
}
