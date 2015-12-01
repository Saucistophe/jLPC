package org.saucistophe.lpc.client.display.pictures;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

/**
 This methods allows to keep track of all buttons created, to easily change
 their font.
 @param <T> The value held by this button.
 */
public class IndexedButton<T> extends JButton
{
    private static final List<IndexedButton> buttons = new ArrayList<>();
    public final T value;

    @SuppressWarnings("LeakingThisInConstructor")
    public IndexedButton(T value)
    {
        super(value.toString());
        this.value = value;

        buttons.add(this);
    }

    public static void setAllFonts(Font font)
    {
        for (IndexedButton button : buttons)
        {
            button.setFont(font);
        }
    }
}
