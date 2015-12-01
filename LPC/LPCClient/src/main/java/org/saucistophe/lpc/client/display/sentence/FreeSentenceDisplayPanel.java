package org.saucistophe.lpc.client.display.sentence;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.saucistophe.exceptions.FormatException;
import org.saucistophe.lpc.business.Sentence;
import org.saucistophe.lpc.client.display.input.GridInput;
import org.saucistophe.lpc.client.display.input.ImageInput;
import org.saucistophe.lpc.client.display.input.PhoneticKeyboard;
import org.saucistophe.lpc.client.display.input.SentencesDictionnaryPanel;
import org.saucistophe.lpc.client.display.input.SentenceViewPanel;
import org.saucistophe.patterns.Updatable;

public class FreeSentenceDisplayPanel extends JPanel implements Updatable<Sentence>
{
	// A dictionnary panel to choose sentences from.
	public SentencesDictionnaryPanel sentenceDictionnaryPanel;
	private SentenceVideoDisplay videoDisplay;
	private SentencePrintDisplay printDisplay;
	public final SentenceViewPanel sentenceView = new SentenceViewPanel();

	public FreeSentenceDisplayPanel()
	{
		super(new BorderLayout());
		setUpGui();
	}

	private void setUpGui()
	{
		// Left pane.
		JPanel inputAndFieldsPanel = new JPanel(new BorderLayout());

		// Sentence input
		JTabbedPane inputTabbedPane = new JTabbedPane();
		inputTabbedPane.setBackground(Color.white);

		inputTabbedPane.add(new PhoneticKeyboard(sentenceView), "Clavier Phonétique");
		inputTabbedPane.add(new GridInput(sentenceView), "Grille de kinèmes");
		inputTabbedPane.add(new ImageInput(sentenceView), "Visage Interactif");
		sentenceDictionnaryPanel = new SentencesDictionnaryPanel(sentenceView);
		inputTabbedPane.add(sentenceDictionnaryPanel, "Dictionnaire");

		inputAndFieldsPanel.add(inputTabbedPane, BorderLayout.CENTER);

		// Sentence preview to the bottom.
		inputAndFieldsPanel.add(sentenceView, BorderLayout.SOUTH);
		sentenceView.addActionListener(action ->
		{
			try
			{
				update(getSentence());
			} catch (FormatException ex)
			{
				Logger.getLogger(FreeSentenceDisplayPanel.class.getName()).log(Level.SEVERE, null, ex);
			}
		});

		// Display the current sentence at the center.
		// This tabbed pane will hold all input methods.
		JTabbedPane ouputTabbedPane = new JTabbedPane();
		ouputTabbedPane.setBackground(Color.white);

		// The methods are all updatable, to refresh them when a different sentence is selected.
		videoDisplay = new SentenceVideoDisplay();
		ouputTabbedPane.add(videoDisplay, "Vidéo");
		printDisplay = new SentencePrintDisplay();
		ouputTabbedPane.add(printDisplay, "Vignettes");

		// Add a split pane in the center.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputAndFieldsPanel, ouputTabbedPane);
		add(splitPane, BorderLayout.CENTER);
	}

	/**
	 @return A sentence made from the input data.

	 @throws FormatException If the given text has a wrong format.
	 */
	private Sentence getSentence() throws FormatException
	{
		Sentence result = new Sentence(sentenceView.standardTextfield.getText(), sentenceView.phoneticRepresentation);
		return result;
	}

	@Override
	public void update(Sentence newValue)
	{
		videoDisplay.update(newValue);
		printDisplay.update(newValue);
	}
}
