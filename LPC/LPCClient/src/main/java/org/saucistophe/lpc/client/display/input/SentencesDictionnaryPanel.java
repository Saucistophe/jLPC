package org.saucistophe.lpc.client.display.input;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import org.saucistophe.lpc.business.Sentence;
import org.saucistophe.lpc.client.display.MainApplet;
import org.saucistophe.lpc.client.display.pictures.IndexedButton;

/**
 The sentence dictionnary panel allows to browse and edit the sentence dictionnary. It can show
 words and sentences, or only words, allow to modify them or not.
 */
public class SentencesDictionnaryPanel extends JPanel
{
	public final DefaultListModel<Sentence> sentenceListModel = new DefaultListModel<>();
	private final SentenceViewPanel sentenceView;
	private JList<Sentence> sentenceList;

	public SentencesDictionnaryPanel(SentenceViewPanel sentenceView)
	{
		super(new BorderLayout());
		this.sentenceView = sentenceView;
		setUpGui();
	}

	private void setUpGui()
	{
		// First add a "sentence" column.
		JPanel sentencePanel = new JPanel(new BorderLayout());
		add(sentencePanel, BorderLayout.CENTER);

		// In it, add a list of words.
		sentenceList = new JList<>(sentenceListModel);
		sentenceList.setBackground(Color.white);
		sentenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a scrollpane around the list.
		JScrollPane scrollPane = new JScrollPane(sentenceList);
		sentencePanel.add(new JLabel("Phrases du dictionnaire"), BorderLayout.NORTH);
		sentencePanel.add(scrollPane, BorderLayout.CENTER);
		sentencePanel.setMinimumSize(new Dimension(300, 200));

		// Add a buttons panel.
		JPanel buttonsPanel = new JPanel(new FlowLayout());
		sentencePanel.add(buttonsPanel, BorderLayout.SOUTH);

		// Add control buttons.
		IndexedButton<String> deleteButton = new IndexedButton<>("Supprimer");
		// On clicking the "remove" button, remove it from the dao and from the list.
		deleteButton.addActionListener((event) ->
		{
			Sentence sentenceToRemove = sentenceList.getSelectedValue();
			if (sentenceToRemove != null)
			{
				sentenceListModel.removeElement(sentenceToRemove);
			}
			MainApplet.getInstance().repaint();
		});
		buttonsPanel.add(deleteButton);

		// Simple load button
		IndexedButton<String> loadSentenceButton = new IndexedButton<>("Charger");
		loadSentenceButton.addActionListener((event) ->
		{
			Sentence selectedSentence = sentenceList.getSelectedValue();
			if (selectedSentence != null)
			{
				sentenceView.setSentence(selectedSentence);
			}
		});
		buttonsPanel.add(loadSentenceButton);

		// Append button
		IndexedButton<String> appendSentenceButton = new IndexedButton<>("Charger à la suite");
		appendSentenceButton.addActionListener((event) ->
		{
			// TODO
			Sentence selectedSentence = sentenceList.getSelectedValue();
			if (selectedSentence != null)
			{
				sentenceView.setSentence(sentenceView.getSentence().append(selectedSentence));
			}
		});
		buttonsPanel.add(appendSentenceButton);

		// Save button
		IndexedButton<String> saveToDicoSentenceButton = new IndexedButton<>("Sauver");
		saveToDicoSentenceButton.addActionListener((event) ->
		{
			Sentence savedSentence = sentenceView.getSentence();
			if (savedSentence != null)
			{
				sentenceListModel.addElement(savedSentence);
			}
		});
		buttonsPanel.add(saveToDicoSentenceButton);

		// Save to dico button
		// Set a preferred size so that it will not shrink to ridiculous size.
		setPreferredSize(new Dimension(200, 200));
	}

	public List<Sentence> getSentences()
	{
		List<Sentence> result = new ArrayList<>();

		for (int i = 0; i < sentenceListModel.getSize(); i++)
		{
			result.add(sentenceListModel.elementAt(i));
		}
		return result;
	}
}
