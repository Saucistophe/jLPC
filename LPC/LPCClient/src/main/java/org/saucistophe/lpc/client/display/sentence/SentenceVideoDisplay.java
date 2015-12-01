package org.saucistophe.lpc.client.display.sentence;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import org.saucistophe.lpc.business.Sentence;
import org.saucistophe.lpc.client.display.pictures.PictureRenderer;
import org.saucistophe.lpc.utils.Constants;
import org.saucistophe.patterns.Updatable;

/**
 */
public class SentenceVideoDisplay extends JPanel implements Updatable<Sentence>
{
	private final JSlider timeSlider = new JSlider(0, Constants.Display.TICKS_PER_FRAME - 1);
	private final PictureRenderer picture = new PictureRenderer(null);
	private Sentence currentSentence = null;

	public SentenceVideoDisplay()
	{
		setupGui();
	}

	private void setupGui()
	{
		setLayout(new BorderLayout());

		// Add a time slider at the bottom.
		timeSlider.setPaintTicks(true);
		timeSlider.setMajorTickSpacing(Constants.Display.TICKS_PER_FRAME);
		timeSlider.setEnabled(false);
		add(timeSlider, BorderLayout.SOUTH);

		timeSlider.addChangeListener((ChangeEvent ce) ->
		{
			int syllableNumber = timeSlider.getValue() / Constants.Display.TICKS_PER_FRAME;
			picture.currentSyllable = currentSentence.syllables.get(syllableNumber);

			picture.repaint();
		});

		// Add a picture renderer at the center.
		add(picture, BorderLayout.CENTER);
	}

	@Override
	public void update(Sentence newValue)
	{
		currentSentence = newValue;
		if (currentSentence != null && currentSentence.syllables != null)
		{
			int numberOfTicks = Constants.Display.TICKS_PER_FRAME * currentSentence.syllables.size() - 1;
			if (numberOfTicks <= 0)
			{
				timeSlider.setEnabled(false);
			}
			else
			{
				timeSlider.setEnabled(true);
				timeSlider.setMaximum(numberOfTicks);
			}
		}

		if (currentSentence == null || currentSentence.syllables == null || currentSentence.syllables.isEmpty())
		{
			picture.currentSyllable = null;
		}

		repaint();
	}
}
