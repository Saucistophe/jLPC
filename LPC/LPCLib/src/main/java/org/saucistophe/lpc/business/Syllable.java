package org.saucistophe.lpc.business;

import java.util.Objects;

/**
 */
public class Syllable
{
    private Phonem consonnant = null;
    private Phonem vowel = null;
    private String standardRepresentation = "";

    public Syllable()
    {
    }

    public Syllable(Phonem consonnant, Phonem vowel)
    {
        this.consonnant = consonnant;
        this.vowel = vowel;
    }

    /**
     @return the phonetic representation, without considering mute phonems (mute vowel and consonnant).
     */
    public String getPhoneticRepresentation()
    {
        String result = "";
        if (!consonnant.equals(Phonem.muteConsonnant))
        {
            result += consonnant;
        }
        if (!vowel.equals(Phonem.muteVowel))
        {
            result += vowel;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return "" + consonnant + vowel;
    }

    @Override
    public boolean equals(Object that)
    {
        if (that != null && that instanceof Syllable)
        {
            Syllable thatSyllable = (Syllable) that;
            return this.vowel.equals(thatSyllable.vowel)
                && this.consonnant.equals(thatSyllable.consonnant)
                && this.standardRepresentation.equals(thatSyllable.standardRepresentation);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.consonnant);
        hash = 59 * hash + Objects.hashCode(this.vowel);
        hash = 59 * hash + Objects.hashCode(this.standardRepresentation);
        return hash;
    }

    /**
     @return the consonnant
     */
    public Phonem getConsonnant()
    {
        return consonnant;
    }

    /**
     @param consonnant the consonnant to set
     */
    public void setConsonnant(Phonem consonnant)
    {
        assert consonnant.isConsonnant();
        this.consonnant = consonnant;
    }

    /**
     @return the vowel
     */
    public Phonem getVowel()
    {
        return vowel;
    }

    /**
     @param vowel the vowel to set
     */
    public void setVowel(Phonem vowel)
    {
        assert vowel.isVowel();
        this.vowel = vowel;
    }

    /**
     @return the standardRepresentation
     */
    public String getStandardRepresentation()
    {
        return standardRepresentation;
    }

    /**
     @param standardRepresentation the standardRepresentation to set
     */
    public void setStandardRepresentation(String standardRepresentation)
    {
        this.standardRepresentation = standardRepresentation;
    }
}
