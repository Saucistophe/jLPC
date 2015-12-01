package org.saucistophe.lpc.client.display.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import org.saucistophe.lpc.business.Phonem;
import org.saucistophe.lpc.client.display.pictures.IndexedButton;
import org.saucistophe.lpc.client.display.pictures.IndexedLabel;

public class PhoneticKeyboard extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final SentenceViewPanel wordView;

	public PhoneticKeyboard(SentenceViewPanel wordView)
	{
		this.wordView = wordView;
		setUpGui();
	}

	private void setUpGui()
	{
		setBackground(Color.white);
		// Left panel: vowels
		JPanel vowelPanel = new JPanel();
		vowelPanel.setLayout(new GridLayout(0, 5));
		vowelPanel.setBackground(Color.white);

		// Add the upper keys.
		for (String s : Phonem.positions)
		{
			IndexedLabel label = new IndexedLabel(s, IndexedLabel.CENTER);
			vowelPanel.add(label);
		}
		// Then for each row:
		Phonem[][] vowels = Phonem.vowelsGrid;
		Phonem[][] consonnants = Phonem.consonnantsGrid;
		for (int i = 0; i < 3; i++)
		{
			for (Phonem[] vowel : vowels)
			{
				if (i < vowel.length)
				{
					IndexedButton<Phonem> button = new IndexedButton<>(vowel[i]);
					handleButton(button);
					vowelPanel.add(button);
				}
				else
				{
					vowelPanel.add(new JSeparator());
				}
			}
		}

		// Then, same for consonnants.
		JPanel consonantsPanel = new JPanel();
		consonantsPanel.setLayout(new GridLayout(0, 8));
		consonantsPanel.setBackground(Color.white);
		// Add the upper keys.
		for (int i = 1; i < 9; i++)
		{
			IndexedLabel label = new IndexedLabel("" + i, IndexedLabel.CENTER);
			consonantsPanel.add(label);
		}

		for (int i = 0; i < 4; i++)
		{
			for (Phonem[] consonnant : consonnants)
			{
				if (i < consonnant.length)
				{
					IndexedButton<Phonem> button = new IndexedButton<>(consonnant[i]);
					handleButton(button);
					consonantsPanel.add(button);
				}
				else
				{
					consonantsPanel.add(new JSeparator());
				}
			}
		}

		add(vowelPanel, BorderLayout.WEST);
		add(consonantsPanel, BorderLayout.EAST);
        // Pack. Don't set visible, it will be activated from the menu.
		//pack();
	}

	private void handleButton(final IndexedButton<Phonem> button)
	{
		// Add a listener to inject the character in the stream.
		button.addActionListener(action ->
		{
			wordView.addPhonem(button.value);
		});
	}
}
