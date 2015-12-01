package org.saucistophe.lpc.client.display;

import javax.swing.JComboBox;
import org.saucistophe.lpc.business.Phonem;

/**
 A combo box that allows to choose from a list of all phonems, or vowels or consonnants.
 */
public class PhonemChooser extends JComboBox<Phonem>
{
    public enum PhonemsProposed
    {
        VOWELS_ONLY, CONSONNANTS_ONLY, ALL_PHONEMS
    }

    public PhonemChooser(PhonemsProposed phonemProposed)
    {
        addItem(null);
        switch (phonemProposed)
        {
            case VOWELS_ONLY:
            {
                for (Phonem vowel : Phonem.getVowels())
                {
                    addItem(vowel);
                }
            }
            break;
            case CONSONNANTS_ONLY:
            {
                for (Phonem consonnant : Phonem.getConsonnants())
                {
                    addItem(consonnant);
                }
            }
            break;
            case ALL_PHONEMS:
            {
                for (Phonem phonem : Phonem.phonems)
                {
                    addItem(phonem);
                }
            }
            break;
        }
    }
}
