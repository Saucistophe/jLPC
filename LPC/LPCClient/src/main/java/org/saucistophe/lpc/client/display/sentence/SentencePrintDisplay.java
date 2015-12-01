package org.saucistophe.lpc.client.display.sentence;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import org.saucistophe.lpc.business.Sentence;
import org.saucistophe.lpc.business.Syllable;
import org.saucistophe.lpc.client.display.pictures.PictureRenderer;
import org.saucistophe.patterns.Updatable;
import org.saucistophe.swing.TransferableImage;
import org.saucistophe.utils.ArrayUtils;
import org.saucistophe.utils.Constants;
import org.saucistophe.utils.PdfUtils;

/**
 This panel creates a grid that will fit on a page. It can, on the push of a
 button, be sent to iText to be printed.
 */
public class SentencePrintDisplay extends JPanel implements Updatable<Sentence>, ClipboardOwner
{
	private final JTabbedPane sentenceDisplayTabs = new JTabbedPane();
	private Sentence currentSentence = null;

	private final JSlider gridSizeSlider = new JSlider(1, 15, 6);

	public SentencePrintDisplay()
	{
		setupGui();
	}

	private void setupGui()
	{
		// The layout will be a grid Layout. It is empty and of unspecified
		// size until a refresh is called.
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setOpaque(true);

		add(sentenceDisplayTabs, BorderLayout.CENTER);
		sentenceDisplayTabs.setBackground(Color.WHITE);

		// Add a bottom print panel.
		JPanel printPanel = new JPanel();
		{
			gridSizeSlider.addChangeListener(changeEvent ->
			{
				update(currentSentence);
			});

			gridSizeSlider.setMajorTickSpacing(3);
			gridSizeSlider.setPaintLabels(true);
			gridSizeSlider.setPaintTicks(true);

			printPanel.add(new JLabel("Taille de la grille"));
			printPanel.add(gridSizeSlider);

			// The print button itself
			JButton printButton = new JButton("Imprimer");
			printButton.addActionListener(this::printToPdf);
			printPanel.add(printButton);

			// An "Export to image" button.
			JButton exportToPngButton = new JButton("Copier vers le presse-papier");
			exportToPngButton.addActionListener(this::exportToPng);
			printPanel.add(exportToPngButton);
		}
		add(printPanel, BorderLayout.SOUTH);
	}

	@Override
	public void update(Sentence newValue)
	{
		// TODO merge with the print.
		// Update the current sentence, if a new one is selected.
		if (newValue != null)
		{
			currentSentence = newValue;
		}

		// For the current sentence, if any:
		if (currentSentence != null)
		{
			// Clear the tabs
			sentenceDisplayTabs.removeAll();

			// Re-add each panel.
			for (JPanel panel : getPanels())
			{
				sentenceDisplayTabs.addTab(panel.getName(), panel);
			}

			repaint();
		}
	}

	public void printToPdf(ActionEvent ae)
	{
		if (currentSentence != null)
		{
			// Create a document (landscape).
			Document document = new Document(PageSize.A4.rotate(), 20f, 20f, 20f, 20f);
			PdfWriter pdfWriter = PdfUtils.createNewPdf(document);

			if (pdfWriter != null)
			{
				document.open();

				// For each panel, corresponding to each page:
				for (JPanel panel : getPanels())
				{
					// Create a new PDF page.
					document.newPage();

					// Setup the pdf page.
					int width = (int) document.getPageSize().getWidth();
					int height = (int) document.getPageSize().getHeight();
					panel.setSize(width, height);
					panel.setBackground(Color.WHITE);

					// Hook the page's content to the panel graphics.
					PdfContentByte cb = pdfWriter.getDirectContent();
					Graphics2D g2d = new PdfGraphics2D(cb, width, height);
					g2d.translate(width * Constants.Pdf.margin, height * Constants.Pdf.margin);
					g2d.scale(1. - (2 * Constants.Pdf.margin), 1. - (2 * Constants.Pdf.margin));

					// Tweak the panel to force refreshing.
					panel.setDoubleBuffered(true);
					panel.setVisible(true);
					panel.addNotify();
					panel.validate();

					// Write the panel's content to the page.
					panel.paint(g2d);
					g2d.dispose();
				}

				document.close();
			}
		}
	}

	public void exportToPng(ActionEvent ae)
	{
		if (currentSentence != null)
		{
			// Select the currently visible.
			JPanel panel = (JPanel) sentenceDisplayTabs.getSelectedComponent();

			// Prepare the panel
			panel.setSize(1024, 768);
			panel.setBackground(Color.WHITE);
			panel.setDoubleBuffered(true);
			panel.setVisible(true);
			panel.addNotify();
			panel.validate();

			// Limit the capture to what is actually inside the grid.
			int xMax =Arrays.stream(panel.getComponents())
				.filter(c -> c instanceof PictureRenderer)
				.map(c -> (PictureRenderer) c)
				.mapToInt(c -> c.getX() + c.getWidth())
				.max()
				.getAsInt();
			int yMax =Arrays.stream(panel.getComponents())
				.filter(c -> c instanceof PictureRenderer)
				.map(c -> (PictureRenderer) c)
				.mapToInt(c -> c.getY() + c.getHeight())
				.max()
				.getAsInt();

			// Render it to a possibly cropped image.
			BufferedImage outputImage = new BufferedImage(xMax, yMax, BufferedImage.TYPE_INT_ARGB);
			Graphics outputGraphics = outputImage.getGraphics();
			panel.paint(outputGraphics);

			// Copy it to the clipboard.
			TransferableImage trans = new TransferableImage(outputImage);
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			c.setContents(trans, this);

			repaint();
		}
	}

	/**

	 @return
	 */
	private List<JPanel> getPanels()
	{
		List<JPanel> result = new ArrayList<>();

		int gridSize = gridSizeSlider.getValue();

		// First make "packets" of x*y pictures.
		int panelsPerPage = gridSize * gridSize;
		List<List<Syllable>> syllablesLists = ArrayUtils.splitToMaxSize(currentSentence.syllables, panelsPerPage);

		// For each packet:
		int pageNumber = 1;
		for (List<Syllable> pageSyllables : syllablesLists)
		{
			// Create a panel from the packet.
			JPanel panel = new JPanel(new GridLayout(gridSize, gridSize));
			for (Syllable syllable : pageSyllables)
			{
				JPanel picture = new PictureRenderer(syllable);
				panel.add(picture);
			}

			// Fill the remaining cells of the grid with placeholders.
			for (int i = pageSyllables.size(); i < panelsPerPage; i++)
			{
				JLabel dummyLabel = new JLabel("");
				dummyLabel.setOpaque(true);
				dummyLabel.setBackground(Color.WHITE);
				panel.add(dummyLabel);
			}

			panel.setName("" + pageNumber);
			pageNumber++;
			result.add(panel);
		}

		return result;
	}

	@Override
	public void lostOwnership(Clipboard clpbrd, Transferable t)
	{
		// In case of clipboard ownership loss, do nothing.
	}
}
