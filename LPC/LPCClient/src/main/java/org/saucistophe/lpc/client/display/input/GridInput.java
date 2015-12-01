package org.saucistophe.lpc.client.display.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.saucistophe.lpc.business.Phonem;
import org.saucistophe.lpc.business.Syllable;
import org.saucistophe.lpc.client.display.pictures.IndexedButton;
import org.saucistophe.lpc.client.display.pictures.IndexedLabel;
import org.saucistophe.lpc.client.display.pictures.PictureRenderer;

/**
 Presents the possible input on a grid, with keys as columns and rows.
 */
public class GridInput extends JPanel
{
	private static final long serialVersionUID = 1L;
	private final SentenceViewPanel wordView;

	public GridInput(SentenceViewPanel wordView)
	{
		this.wordView = wordView;
		setUpGui();
	}

	private void setUpGui()
	{
		setLayout(new BorderLayout());
		setBackground(Color.white);

		final Phonem[][] vowelsGrid = Phonem.vowelsGrid;
		final Phonem[][] consonnantsGrid = Phonem.consonnantsGrid;

		JPanel picturePanel = new JPanel(new GridLayout(0, consonnantsGrid.length));
		picturePanel.setBackground(Color.WHITE);
		add(picturePanel, BorderLayout.CENTER);

		// Add the consonants on the top side.
		JPanel consonnantsLabelsPanel = new JPanel(new GridLayout(0, consonnantsGrid.length));
		consonnantsLabelsPanel.setBackground(Color.white);
		add(consonnantsLabelsPanel, BorderLayout.NORTH);

		// Add the consonants on the left side.
		for (Phonem[] consonnantsGrid1 : consonnantsGrid)
		{
			String label = "";
			for (int j = 0; j < consonnantsGrid1.length; j++)
			{
				if (j > 0)
				{
					label += ' ';
				}
				label += consonnantsGrid1[j];
			}
			consonnantsLabelsPanel.add(new IndexedLabel(label, IndexedLabel.CENTER));
		}

		// For each row:
		for (int j = 0; j < Phonem.positions.length; j++)
		{
			final int column = j;

			// For each column:
			for (int i = 0; i < consonnantsGrid.length; i++)
			{
				// Add a button corresponding to this vowel, and consonnant class.
				// The custom button implements a PictureRenderer.
				final int row = i;
				JButton button = new JButton()
				{
					// Add the graphic representation of the syllable as background.
					Syllable syllable = new Syllable(consonnantsGrid[row][0], vowelsGrid[column][0]);
					private final PictureRenderer pictureRenderer = new PictureRenderer(syllable);

					@Override
					public void paint(Graphics g)
					{
						super.paint(g);
						
						pictureRenderer.paintImages((Graphics2D) g, this, this.getBounds());
					}
				};

				// Additionnal rendering settings.
				button.setBorderPainted(true);
				button.setFocusPainted(false);
				button.setContentAreaFilled(false);

				// Last but not least, hook the button tho the word being created.
				// Add a listener to inject the character in the stream.
				button.addMouseListener(new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent e)
					{
						JPopupMenu popup = new JPopupMenu("Choix de syllabe");

						for (final Phonem vowel : Phonem.vowelsGrid[column])
						{
							for (final Phonem consonnant : Phonem.consonnantsGrid[row])
							{
								JMenuItem menuItem = new JMenuItem("" + consonnant + vowel);
								// On clicking the submenu element, the syllable is added.
								menuItem.addActionListener((ActionEvent e1) ->
								{
									// Special case: ignore the mute phonems.
									if (!consonnant.equals(Phonem.muteConsonnant))
									{
										wordView.addPhonem(consonnant);
									}
									if (!vowel.equals(Phonem.muteVowel))
									{
										wordView.addPhonem(vowel);
									}
								});
								popup.add(menuItem);
							}
						}
						// Show at cursor position.
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				});
				picturePanel.add(button);
			}
		}

		// Then, same for consonnants.
		JPanel consonnantsPanel = new JPanel();
		consonnantsPanel.setLayout(new GridLayout(0, 8));
		// Add the upper keys.
		for (int i = 1; i < 9; i++)
		{
			IndexedLabel label = new IndexedLabel("" + i, IndexedLabel.CENTER);
			consonnantsPanel.add(label);
		}

		for (int i = 0; i < 4; i++)
		{
			for (Phonem[] consonnant : consonnantsGrid)
			{
				if (i < consonnant.length)
				{
					IndexedButton<Phonem> button = new IndexedButton<>(consonnant[i]);
					consonnantsPanel.add(button);
				}
				else
				{
					consonnantsPanel.add(new JSeparator());
				}
			}
		}
	}
}
