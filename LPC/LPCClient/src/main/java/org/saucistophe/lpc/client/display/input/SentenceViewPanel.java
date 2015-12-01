package org.saucistophe.lpc.client.display.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Integer.MAX_VALUE;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.saucistophe.lpc.business.Phonem;
import org.saucistophe.lpc.business.Sentence;

/**
 A panel to view and edit a word.
 */
public class SentenceViewPanel extends JPanel
{
	// TODO turn red if something is fishy, backup to the list of phonems if all is fine.
	public List<Phonem> phoneticRepresentation = new ArrayList<>();
	// max length: anticonstitutionnellement plus a bit.
	protected final JTextField phoneticTextfield = new JTextField(30);
	public final JTextField standardTextfield = new JTextField(30);

	public SentenceViewPanel()
	{
		super();
		setUpGui();
	}

	/**
	 @param newStandardField The field for the standard representation (liaison or not)
	 @param newPhoneticField The field for the phonetic representation (liaison or not)
	 @return A nice panel displaying them.
	 */
	JPanel getWordMiniPanel(JTextField newStandardField, JTextField newPhoneticField, List<Phonem> relevantPhoneticRepresentation)
	{
		JPanel panel = new JPanel();
		// Set layout to a vertical BoxLayout.
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		// Standard representation line
		panel.add(new JLabel("Représentation alphabétique"));
		panel.add(newStandardField);

		// Phonetic representation line - Not editable by hand
		newPhoneticField.setEditable(false);
		newPhoneticField.setBackground(Color.WHITE);
		panel.add(new JLabel("Représentation phonétique"));

		// Add a button to delete the last phonem
		JPanel phoneticPanel = new JPanel(new BorderLayout());
		JButton returnButton = new BasicArrowButton(BasicArrowButton.WEST);

		returnButton.addActionListener(e ->
		{
			if (!relevantPhoneticRepresentation.isEmpty())
			{
				relevantPhoneticRepresentation.remove(relevantPhoneticRepresentation.size() - 1);
				refreshPhonetic();
			}
		});
		phoneticPanel.add(returnButton, BorderLayout.EAST);
		phoneticPanel.add(newPhoneticField, BorderLayout.CENTER);
		panel.add(phoneticPanel);

		return panel;
	}

	/**
	 Sets up the gui and layouts.
	 */
	public final void setUpGui()
	{
		// Set global layout to a vertical BoxLayout.
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// Create a formatted text field, accepting only lowercase characters
		DocumentFilter dfilter = new LowercaseFilter();
		((AbstractDocument) standardTextfield.getDocument()).setDocumentFilter(dfilter);

		// Create a panel to display each word
		JPanel standardPanel = getWordMiniPanel(standardTextfield, phoneticTextfield, phoneticRepresentation);
		standardPanel.setBorder(new TitledBorder("Mot"));
		add(standardPanel);

		// Save panel
		JPanel buttonsPanel = new JPanel();

		// Add a separator
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		buttonsPanel.add(separator);

		// Add "word reset" button.
		JButton resetButton = new JButton("Nouveau Mot");
		resetButton.setToolTipText("Vide les champs pour rentrer un nouveau mot.");
		resetButton.addActionListener((ActionEvent ae) ->
		{
			resetDisplay();
		});
		buttonsPanel.add(resetButton);

		// Add the buttons panel at the bottom.
		add(buttonsPanel);

		// Add a border to this panel.
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		setBorder(loweredEtched);

		// Save the default textfield size, as the preferred size.
		Dimension size = standardTextfield.getPreferredSize();
		size.width = MAX_VALUE;
		// Apply to all fields (except the last one, the button.
		for (Component component : getComponents())
		{
			if (component != buttonsPanel)
			{
				component.setMaximumSize(size);
			}
		}
	}

	
	public Sentence getSentence()
	{
		// Get the standard representation first.
		String standardRepresentation = standardTextfield.getText();
		return new Sentence(standardRepresentation, phoneticRepresentation);
	}

	public void setSentence(Sentence sentence)
	{
		standardTextfield.setText(sentence.toAlphabeticRepresentation());
		phoneticRepresentation = Phonem.toPhonems(sentence.syllables);
		refreshPhonetic();
	}

	/** Adds a phonem to the currently edited list of phonems (regular or liaison).

	 @param phonem The phonem to add.
	 */
	public void addPhonem(Phonem phonem)
	{
		phoneticRepresentation.add(phonem);
		refreshPhonetic();
	}

	/**
	 Refreshes the phonetic fields from the phonems lists.
	 */
	public void refreshPhonetic()
	{
		String s = "";
		for (Phonem phonem : phoneticRepresentation)
		{
			s += phonem.toString();
		}
		phoneticTextfield.setText(s);
	}

	/**
	 A filter to allow only lowercase Letters and commas.
	 */
	public class LowercaseFilter extends DocumentFilter
	{
		private String process(String text)
		{
			String letters = "";
			for (char c : text.toCharArray())
			{
				if (Character.isLetter(c) || c == ',')
				{
					letters += c;
				}
			}
			return letters.toLowerCase();
		}

		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset,
			String text, AttributeSet attr) throws BadLocationException
		{

			fb.insertString(offset, process(text), attr);
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
			String text, AttributeSet attr) throws BadLocationException
		{
			String letters = "";
			for (char c : text.toCharArray())
			{
				if (Character.isLetter(c))
				{
					letters += c;
				}
			}
			fb.replace(offset, length, process(text), attr);
		}
	}

	/**
	 Resets the display to prepare for the next word.
	 */
	private void resetDisplay()
	{
		phoneticRepresentation.clear();
		refreshPhonetic();
		standardTextfield.setText("");
	}

	public void addActionListener(ActionListener listener)
	{
		DocumentListener documentListener = new DocumentListener()
		{
			@Override
			public void insertUpdate(DocumentEvent de)
			{
				listener.actionPerformed(null);
			}

			@Override
			public void removeUpdate(DocumentEvent de)
			{
				listener.actionPerformed(null);
			}

			@Override
			public void changedUpdate(DocumentEvent de)
			{
				listener.actionPerformed(null);
			}
		};
		standardTextfield.getDocument().addDocumentListener(documentListener);
		phoneticTextfield.getDocument().addDocumentListener(documentListener);
	}
}
