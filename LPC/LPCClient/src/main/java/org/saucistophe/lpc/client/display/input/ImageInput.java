package org.saucistophe.lpc.client.display.input;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * Presents the possible input as an image.
 */
public class ImageInput extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JTextField textField;
    private final SentenceViewPanel wordView;

    public ImageInput(SentenceViewPanel wordView)
    {
        this.wordView = wordView;
        setUpGui();
    }
    private void setUpGui()
    {
        setBackground(Color.white);
    }
}
