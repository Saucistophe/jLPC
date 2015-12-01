package org.saucistophe.lpc.business;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 A phonem is a basic sound, and can ben a vowel or a consonnant.
 */
public class Phonem
{
	private int phonemIndex;
	public static final double[][] handPositions =
	{
		{// Cheek
			0.80, 0.62, -Math.PI * 0.12
		},
		{// Next to the head
			1.0, 0.55, -Math.PI * 0.06
		},
		{// Mouth side
			0.78, 0.77, -Math.PI * 0.35
		},
		{// Chin
			0.52, 0.90, -Math.PI * 0.35
		},
		{// Neck
			0.5, 1., -Math.PI * 0.45
		}
	};

	public static final String positions[] =
	{
		"Joue", "Côté", "Bouche", "Menton", "Cou"
	};
	public static final List<String> phonemsStrings = Arrays.asList(new String[]
	{
		// Vowels
		"ɛ̃", "ø",
		"a", "o", "œ", "0",
		"i", "ɔ̃", "ɑ̃",
		"ɛ", "u", "ɔ",
		"y", "e", "œ̃",
		// Consonnants
		"p", "d", "ʒ",
		"k", "v", "z",
		"s", "ʁ",
		"b", "n", "ɥ",
		"t", "m", "f", " ",
		"l", "ʃ", "ɲ", "w",
		"g",
		"j", "ŋ"
	});

	/**
	 A list of all phonems, for convenience.
	 */
	public static final List<Phonem> phonems = Arrays.asList(new Phonem[]
	{
		// Vowels
		new Phonem("ɛ̃"), new Phonem("ø"),
		new Phonem("a"), new Phonem("o"), new Phonem("œ"), new Phonem("0"),
		new Phonem("i"), new Phonem("ɔ̃"), new Phonem("ɑ̃"),
		new Phonem("ɛ"), new Phonem("u"), new Phonem("ɔ"),
		new Phonem("y"), new Phonem("e"), new Phonem("œ̃"),
		// Consonnants
		new Phonem("p"), new Phonem("d"), new Phonem("ʒ"),
		new Phonem("k"), new Phonem("v"), new Phonem("z"),
		new Phonem("s"), new Phonem("ʁ"),
		new Phonem("b"), new Phonem("n"), new Phonem("ɥ"),
		new Phonem("t"), new Phonem("m"), new Phonem("f"), new Phonem(" "),
		new Phonem("l"), new Phonem("ʃ"), new Phonem("ɲ"), new Phonem("w"),
		new Phonem("g"),
		new Phonem("j"), new Phonem("ŋ")
	});

	/**
	 A grid of vowels. Each row holds the vowels associated to the corresponding hand position.
	 */
	public static final Phonem vowelsGrid[][] =
	{
		{
			phonems.get(0), phonems.get(1)
		},
		{
			phonems.get(2), phonems.get(3), phonems.get(4), phonems.get(5)
		},
		{
			phonems.get(6), phonems.get(7), phonems.get(8)
		},
		{
			phonems.get(9), phonems.get(10), phonems.get(11)
		},
		{
			phonems.get(12), phonems.get(13), phonems.get(14)
		}
	};

	/**
	 A grid of consonnants. Each row holds the consonnants associated to the corresponding hand keys.
	 */
	public static final Phonem consonnantsGrid[][] =
	{
		{
			phonems.get(15), phonems.get(16), phonems.get(17),
		},
		{
			phonems.get(18), phonems.get(19), phonems.get(20),
		},
		{
			phonems.get(21), phonems.get(22),
		},
		{
			phonems.get(23), phonems.get(24), phonems.get(25),
		},
		{
			phonems.get(26), phonems.get(27), phonems.get(28), phonems.get(29),
		},
		{
			phonems.get(30), phonems.get(31), phonems.get(32), phonems.get(33),
		},
		{
			phonems.get(34),
		},
		{
			phonems.get(35), phonems.get(36),
		}
	};

	public final static Phonem muteConsonnant = phonems.get(29);
	public final static Phonem muteVowel = phonems.get(5);

	/**
	 Parses a String containing phonems.

	 @param string A phonetic string, i.e. "katʁ"
	 @return a list of phonems.
	 @throws java.text.ParseException If the phonems could not be handled.
	 */
	public static List<Phonem> parse(String string) throws ParseException
	{
		if (string == null)
		{
			return null;
		}

		List<Phonem> result = new ArrayList<>();

		// A little kludge here. It allows to treat some characters as one, since, for no apparent reason, they are split in two in files.
		string = string.replace("œ̃", "1");
		string = string.replace("ɛ̃", "2");
		string = string.replace("ɑ̃", "3");

		// Then, for each character:
		int characterIndex = 0;
		for (char c : string.toCharArray())
		{
			String character = Character.toString(c);

			// Switch back from the kludge.
			character = character.replace("1", "œ̃");
			character = character.replace("2", "ɛ̃");
			character = character.replace("3", "ɑ̃");

			// Turn it into a known phonem, if possible.
			Phonem parsedPhonem = new Phonem(character);
			if(parsedPhonem.phonemIndex == -1)
			{
				throw new ParseException(string, characterIndex);
			}

			result.add(parsedPhonem);
			characterIndex++;
		}

		return result;
	}

	/**
	 Parses a list of phonems, and turns it into a list of syllables.

	 @param phonems The list of phonems to parse
	 @return A list of the syllables.
	 */
	public static List<Syllable> toSyllables(List<Phonem> phonems)
	{
		List<Syllable> result = new ArrayList<>();

		// For each phonem.
		for (int i = 0; i < phonems.size(); i++)
		{
			Phonem phonem = phonems.get(i);
			Syllable syllable = new Syllable();
			if (phonem.isVowel())
			{
				// If it is a vowel, it needs to be prefixed with a mute consonnnant.
				syllable.setConsonnant(muteConsonnant);
				syllable.setVowel(phonem);
				result.add(syllable);
			}
			else
			{
				// If it is a consonnant, check if it is followed by a vowel.
				if (i + 1 < phonems.size() && phonems.get(i + 1).isVowel())
				{
					// If it is, add the syllable, skip the vowel, and continue.
					syllable.setConsonnant(phonem);
					syllable.setVowel(phonems.get(i + 1));
					result.add(syllable);
					i++;
				}
				else
				{
					// If not, simply add a mute vowel and go on.
					syllable.setConsonnant(phonem);
					syllable.setVowel(muteVowel);
					result.add(syllable);
				}
			}
		}
		return result;
	}

	/**
	 Takes a list of syllables, and merges it into a list of Phonems.

	 @param syllables
	 @return
	 */
	public static List<Phonem> toPhonems(List<Syllable> syllables)
	{
		List<Phonem> result = new ArrayList<>();

		// For each Syllable.
		for (Syllable syllable : syllables)
		{
			// TODO Check if it's ok.
			if (syllable.getConsonnant() != muteConsonnant)
			{
				result.add(syllable.getConsonnant());
			}
			if (syllable.getVowel() != muteVowel)
			{
				result.add(syllable.getVowel());
			}
		}
		return result;
	}

	public static List<Phonem> getVowels()
	{
		return phonems.subList(0, 15);
	}

	public static List<Phonem> getConsonnants()
	{
		return phonems.subList(15, phonems.size());
	}

	/**
	 Should not be used.
	 */
	private Phonem()
	{
	}

	/**
	 Builds a Phonem from its IPA representation.

	 @param string
	 */
	public Phonem(String string)
	{
		this.phonemIndex = phonemsStrings.indexOf(string);
	}

	/**
	 Builds a Phonem from its Phonem index.

	 @param phonemIndex
	 */
	public Phonem(int phonemIndex)
	{
		this.phonemIndex = phonemIndex;
	}

	/**
	 @return True if the string corresponds to a vowel. That is, if it is not
	 a consonnant and allows a liaison.
	 */
	public boolean isVowel()
	{
		return phonems.indexOf(this) <= 14;
	}

	/**
	 @return True if the string corresponds to a consonnant. That is, if it is
	 not a vowel and thus doesn't allows a liaison.
	 */
	public boolean isConsonnant()
	{
		return !this.isVowel();
	}

	@Override
	public String toString()
	{
		if (phonemsStrings.size() > phonemIndex)
		{
			// Special case: ignore the mute phonems.
			if (phonemIndex == muteConsonnant.getPhonemIndex()
				|| phonemIndex == muteVowel.getPhonemIndex())
			{
				return "";
			}
			else
			{
				return phonemsStrings.get(phonemIndex);
			}
		}
		else
		{
			return "XX";
		}
	}

	@Override
	public boolean equals(Object that)
	{
		if (that != null && that instanceof Phonem)
		{
			return ((Phonem) that).phonemIndex == this.phonemIndex;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return phonemIndex;
	}

	/**
	 @return the phonemIndex
	 */
	public int getPhonemIndex()
	{
		return phonemIndex;
	}

	/**
	 @param phonemIndex the phonemIndex to set
	 */
	public void setPhonemIndex(int phonemIndex)
	{
		this.phonemIndex = phonemIndex;
	}
}
