package org.saucistophe.lpc.client.display.pictures;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

/**
 This methods allows to keep track of all labels created, to easily change
 their font.
 */
public class IndexedLabel extends JLabel
{
	/**
	 The list that keeps track of instanciated labels.
	 */
	private static List<IndexedLabel> labels = new ArrayList<>();

	@SuppressWarnings("LeakingThisInConstructor")
	public IndexedLabel(String text)
	{
		super(text);

		labels.add(this);
	}

	@SuppressWarnings("LeakingThisInConstructor")
	public IndexedLabel(String s, int horizontalAlignement)
	{
		super(s, horizontalAlignement);
		labels.add(this);
	}

	public static void setAllFonts(Font font)
	{
		for (IndexedLabel label : labels)
		{
			label.setFont(font);
		}
	}
}
