package org.saucistophe.lpc;

import org.junit.Assert;
import org.junit.Test;
import org.saucistophe.lpc.business.Phonem;

public class PhonemsTest
{
    @Test
    public void phonemsTest()
    {
        // Checks if vowels are vowels...
        for (Phonem[] phonems : Phonem.vowelsGrid)
        {
            for (Phonem p : phonems)
            {
                assert (p.isVowel());
                assert (!p.isConsonnant());
            }

        }
        // ... And if consonnants are consonnants!
        for (Phonem[] phonems : Phonem.consonnantsGrid)
        {
            for (Phonem p : phonems)
            {
                Assert.assertFalse(p.isVowel());
                Assert.assertTrue(p.isConsonnant());
            }
        }

        // Check the non-breaking space kludge.
        Assert.assertNotEquals(Phonem.muteConsonnant, Phonem.muteVowel);
    }

    /**
     For each phonem, store it, retrieve it, and check it's still the same.
     */
    @Test
    public void storingTest()
    {
        for (Phonem p : Phonem.phonems)
        {
            Assert.assertEquals(p, new Phonem(p.getPhonemIndex()));
        }
    }
}
