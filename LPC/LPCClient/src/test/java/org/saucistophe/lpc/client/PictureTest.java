package org.saucistophe.lpc.client;

import java.util.function.Predicate;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.saucistophe.lpc.business.Phonem;
import org.saucistophe.lpc.client.display.pictures.PictureRenderer;

/**
 */
public class PictureTest
{
    /**
     Test the image retrieval for every phonem.
     */
    @Test
    public void pictureTest()
    {
        // The phonems can be null for this renderer, since it won't render anything.
        PictureRenderer lpcPicture = new PictureRenderer(null);

        // Check that all consonnants have an image.
        assertTrue(Phonem.phonems.parallelStream().filter(Phonem::isConsonnant)
            .map(phonem -> lpcPicture.getImageFromConsonnant(phonem))
            .noneMatch(Predicate.isEqual(null)));

        // Check that all vowels throw exceptions.
        assertTrue(Phonem.phonems.parallelStream().filter(Phonem::isVowel)
            .allMatch(
                phonem ->
                {
                    try
                    {
                        lpcPicture.getImageFromConsonnant(phonem);
                    } catch (IndexOutOfBoundsException e)
                    {
                        return true;
                    }
                    return false;
                }
            ));
    }
}
