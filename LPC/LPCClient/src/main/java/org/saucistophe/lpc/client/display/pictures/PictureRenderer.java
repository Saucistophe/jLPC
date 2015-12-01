package org.saucistophe.lpc.client.display.pictures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.saucistophe.annotations.SettingsField;
import org.saucistophe.constants.Alignment;
import static org.saucistophe.constants.Alignment.Horizontal.CENTER;
import static org.saucistophe.constants.Alignment.Vertical.BOTTOM;
import org.saucistophe.lpc.business.Phonem;
import org.saucistophe.lpc.business.Syllable;
import org.saucistophe.lpc.client.display.MainApplet;
import org.saucistophe.lpc.utils.Constants;

/**
 A renderer to draw a LPC position.
 */
public class PictureRenderer extends JPanel
{
	@SettingsField(name = "Affichage phonétique", category = "Affichage")
	public static boolean displayPhoneticText = true;
	@SettingsField(name = "Affichage alphabétique", category = "Affichage")
	public static boolean displayText = true;

	@SettingsField(name = "Alignement horizontal", category = "Affichage")
	public static Alignment.Horizontal horizontalAlignment = CENTER;
	@SettingsField(name = "Alignement vertical", category = "Affichage")
	public static Alignment.Vertical verticalAlignment = BOTTOM;

	/**
	 The font used for text display on this renderer.
	 */
	public static Font font = MainApplet.defaultFont;

	/**
	 A lazy-loading map of phonems to their image.
	 */
	private static final Map<Phonem, BufferedImage> consonnantToPictureMap = new HashMap<>();

	/**
	 The background head image.
	 */
	private static BufferedImage headBackgroundImage = null;

	// TODO for videos.
	//public Syllable previousSyllable;
	// The syllable currently displayed.
	public Syllable currentSyllable;

	/**
	 The progress between
	 */
	private double progress = 1.;

	public PictureRenderer(Syllable syllable)
	{
		// If the background hasn't been loaded yet, load it.
		if (headBackgroundImage == null)
		{
			try
			{
				headBackgroundImage = ImageIO.read(PictureRenderer.class.getResourceAsStream("/images/Head.png"));
			} catch (IOException ex)
			{
				Logger.getLogger(PictureRenderer.class
						.getName()).log(Level.SEVERE, null, ex);
			}

		}
		currentSyllable = syllable;
		setBackground(Color.WHITE);
	}

	/**
	 @param phonem The Vowel for which we need the hand image.
	 @return The corresponding image.
	 */
	public BufferedImage getImageFromConsonnant(Phonem phonem)
	{
		BufferedImage result = null;

		if (phonem.isVowel())
		{
			throw new IndexOutOfBoundsException("The given phonem is not a consonnant.");
		} else
		{
			// Find the phonem's corresponding position.
			int position = 0;
			Phonem[][] grid = Phonem.consonnantsGrid;
			mainLoop:
			for (; position < grid.length; position++)
			{
				for (Phonem consonnant : grid[position])
				{
					if (consonnant.equals(phonem))
					{
						break mainLoop;

					}
				}
			}
			// Now we have the position number, find the image.
			try
			{
				result = ImageIO.read(PictureRenderer.class
						.getResource("/images/hand" + position + ".png"));
			} catch (IOException ex)
			{
				Logger.getLogger(PictureRenderer.class
						.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return result;
	}

	/**
	 @param phonem The Vowel for which we need the hand position.
	 @return The position (x,y,angle) for the given vowel.
	 */
	public double[] getPositionFromVowel(Phonem phonem)
	{
		if (phonem.isConsonnant())
		{
			throw new IndexOutOfBoundsException("The given phonem is not a vowel.");
		} else
		{
			// Find the phonem's corresponding position.
			int position = 0;
			Phonem[][] grid = Phonem.vowelsGrid;
			mainloop:
			for (; position < grid.length; position++)
			{
				for (Phonem vowel : grid[position])
				{
					if (vowel.equals(phonem))
					{
						break mainloop;
					}
				}
			}

			// Now we have the position number, return the data.
			return Phonem.handPositions[position];
		}
	}

	@Override
	public void paint(Graphics graphics)
	{
		super.paint(graphics);

		// Nothing to do on empty syllables.
		if (currentSyllable == null)
		{
			return;
		}

		// Set the font.
		setFont(font);

		// First, compute the size taken by the strings.
		int fontHeight = font.getSize();
		boolean shoudDisplayRegular = displayText && currentSyllable.getStandardRepresentation() != null;
		boolean shoudDisplayPhonetic = displayPhoneticText && currentSyllable.getPhoneticRepresentation() != null;
		int textHeight = (shoudDisplayPhonetic ? font.getSize() : 0) + (shoudDisplayRegular ? font.getSize() : 0);

		// Determine the face location.
		Rectangle faceLocation = new Rectangle(0, 0, getWidth(), getHeight() - textHeight);

		// Paint the head and hand images on the leftover space.
		paintImages((Graphics2D) graphics, this, faceLocation);

		// The position depends on the chosen aligment and strings length.
		int stringLength = 0;
		if (shoudDisplayRegular)
		{
			stringLength = Math.max(stringLength, graphics.getFontMetrics(font).stringWidth(currentSyllable.getStandardRepresentation()));
		}
		if (shoudDisplayPhonetic)
		{
			stringLength = Math.max(stringLength, graphics.getFontMetrics(font).stringWidth(currentSyllable.getPhoneticRepresentation()));
		}

		int startX = 0;
		switch (horizontalAlignment)
		{
			case LEFT:
				startX = 3;
				break;
			case CENTER:
				startX = 3 + (getWidth() - stringLength) / 2;
				break;
			case RIGHT:
				startX = getWidth() - stringLength - 3;
				break;
		}

		if (displayText && currentSyllable.getStandardRepresentation() != null)
		{
			// Make the regular text bold.
			Font regularTextFont = new Font(font.getName(), Font.BOLD, font.getSize());
			graphics.setFont(regularTextFont);
			graphics.drawString(currentSyllable.getStandardRepresentation(), startX, this.getHeight() - font.getSize() - 6);
		}
		if (displayPhoneticText && currentSyllable.getPhoneticRepresentation() != null)
		{
			graphics.setFont(font);
			graphics.drawString(currentSyllable.getPhoneticRepresentation(), startX, this.getHeight() - 3);
		}
	}

	/**
	 Paint this syllable to the given graphics, on the given component.

	 @param graphics The graphics that draws the images.
	 @param component The component on which the images are drawn.
	 @param faceBounds The rectangle into wich the face must be centered.
	 */
	public void paintImages(Graphics2D graphics, JComponent component, Rectangle faceBounds)
	{
		if (currentSyllable != null)
		{
			// Force smoothness.
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			double scale;
			double horizontalTranslation;

			// If the given space is too small, make the image span the whole component, and ignore text margins.
			if (faceBounds.width < component.getWidth() / 2)
			{
				faceBounds.width = component.getWidth();
			} else if (faceBounds.height < component.getHeight() / 2)
			{
				faceBounds.height = component.getHeight();
			}

			// Add a margin to take the hands into account.
			int horizontalMargin = (int) (faceBounds.width * 0.25);
			int verticalMargin = (int) (faceBounds.height * 0.2);

			// First draw the head background.
			// Scale it to width OR height.
			double xScale = (faceBounds.width - horizontalMargin) * 1. / headBackgroundImage.getWidth(component);
			double yScale = (faceBounds.height - verticalMargin) * 1. / headBackgroundImage.getHeight(component);

			// Keep the most constraining scale.
			scale = Math.min(xScale, yScale);

			// Adjust the face image centering to take the margin into account, if needed.
			if (xScale < yScale)
			{
				horizontalTranslation = 0.5 * faceBounds.width;
			} else
			{
				horizontalTranslation = 0.5 * component.getWidth();
			}

			// Draw the head.
			AffineTransform headTransform = new AffineTransform();

			headTransform.translate(horizontalTranslation, 0);
			headTransform.scale(scale, scale);
			headTransform.translate(-headBackgroundImage.getWidth() / 2, 0);
			graphics.drawImage(headBackgroundImage, headTransform, null);

			// Lazy-load the hand picture.
			BufferedImage handImage;
			Phonem currentConsonnant = currentSyllable.getConsonnant();
			Phonem currentVowel = currentSyllable.getVowel();
			if (consonnantToPictureMap.containsKey(currentConsonnant))
			{
				handImage = consonnantToPictureMap.get(currentConsonnant);
			} else
			{
				handImage = getImageFromConsonnant(currentConsonnant);
				consonnantToPictureMap.put(currentConsonnant, handImage);
			}

			// Find its position.
			double[] position = getPositionFromVowel(currentVowel);

			// Elaborate the full transformation.
			AffineTransform handTransform = new AffineTransform();
			// Move it to it's right place. Consider either the component width,
			// or if stretched, the component's default width. Consider the height the same way.
			handTransform.translate(horizontalTranslation + headBackgroundImage.getWidth() * scale * (position[0] - 0.5),
					headBackgroundImage.getHeight() * position[1] * scale);
			// Scale it to fit the head's size.
			double handScale = Constants.Display.HAND_HEAD_RATIO * scale;
			handTransform.scale(handScale, handScale);

			// Rotate.
			handTransform.rotate(position[2]);
			// Center on the finger.
			handTransform.translate(-handImage.getWidth() / 2, -handImage.getHeight() / 10);

			// Draw the image with the transform.
			graphics.drawImage(handImage, handTransform, component);
		}
	}
}
