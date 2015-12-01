package org.saucistophe.lpc.business;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 Represents a sequence of syllables.
 */
public class Sentence
{
	public List<Syllable> syllables = new ArrayList<>();

	public Sentence()
	{
	}

	public Sentence(Sentence that)
	{
		this.syllables = new ArrayList<>(that.syllables);
	}

	/**
	 Builds a sentence from its standard and phonetic representations.

	 @param standardRepresentation The sentence's standard representation.
	 @param phoneticRepresentation The sentence's phonetic representation.
	 */
	public Sentence(String standardRepresentation, List<Phonem> phoneticRepresentation)
	{
		// Create syllables.
		syllables = Phonem.toSyllables(phoneticRepresentation);

		// Split the alphabetic syllables.
		String[] alphabeticSyllables = standardRepresentation.split(",");

		for (int index = 0; (index < syllables.size()) && (index < alphabeticSyllables.length); index++)
		{
			Syllable syllable = syllables.get(index);
			syllable.setStandardRepresentation(alphabeticSyllables[index]);
		}
	}

	/**
	 Appends the given sentence to this and return the result.

	 @param otherSentence The sentence to add.
	 @return A new sentence, with the other one appended at the end.
	 */
	public Sentence append(Sentence otherSentence)
	{
		Sentence result = new Sentence(this);
		result.syllables.addAll(otherSentence.syllables);
		return result;
	}

	public String toAlphabeticRepresentation()
	{
		return syllables.stream()
			.map(Syllable::getStandardRepresentation)
			.collect(Collectors.joining(","));
	}

	public String toPhoneticRepresentation()
	{
		return syllables.stream()
			.map(Syllable::getPhoneticRepresentation)
			.collect(Collectors.joining());
	}

	@Override
	public String toString()
	{
		return toAlphabeticRepresentation();
	}

	public String toStorageString()
	{
		return toAlphabeticRepresentation() + "|" + toPhoneticRepresentation();
	}

	/**
	 @param storageString The string that persists a sentence.
	 @return The generated sentence.

	 @throws ParseException If the string was incorrect.
	 */
	public static Sentence fromStorageString(String storageString) throws ParseException
	{
		// First split along the separator.
		String[] strings = storageString.split("\\|");
		if (strings.length != 2)
		{
			throw new ParseException(storageString, 0);
		}

		String alphabeticRepresentation = strings[0];
		// Parse the phonetic string into phonems.
		List<Phonem> storedPhonems = Phonem.parse(strings[1]);

		Sentence result = new Sentence(alphabeticRepresentation, storedPhonems);
		return result;
	}
}
