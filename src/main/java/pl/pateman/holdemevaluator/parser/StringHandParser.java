package pl.pateman.holdemevaluator.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.CardValue;
import pl.pateman.holdemevaluator.Suit;

public final class StringHandParser {

  private static final Map<Character, CardValue> CHARACTER_CARD_VALUE_MAP = new HashMap<>();
  private static final Map<Character, Suit> CHARACTER_SUIT_MAP = new HashMap<>();

  static {
    CHARACTER_CARD_VALUE_MAP.put('A', CardValue.ACE);
    CHARACTER_CARD_VALUE_MAP.put('2', CardValue.TWO);
    CHARACTER_CARD_VALUE_MAP.put('3', CardValue.THREE);
    CHARACTER_CARD_VALUE_MAP.put('4', CardValue.FOUR);
    CHARACTER_CARD_VALUE_MAP.put('5', CardValue.FIVE);
    CHARACTER_CARD_VALUE_MAP.put('6', CardValue.SIX);
    CHARACTER_CARD_VALUE_MAP.put('7', CardValue.SEVEN);
    CHARACTER_CARD_VALUE_MAP.put('8', CardValue.EIGHT);
    CHARACTER_CARD_VALUE_MAP.put('9', CardValue.NINE);
    CHARACTER_CARD_VALUE_MAP.put('T', CardValue.TEN);
    CHARACTER_CARD_VALUE_MAP.put('J', CardValue.JACK);
    CHARACTER_CARD_VALUE_MAP.put('Q', CardValue.QUEEN);
    CHARACTER_CARD_VALUE_MAP.put('K', CardValue.KING);

    CHARACTER_SUIT_MAP.put('C', Suit.CLUBS);
    CHARACTER_SUIT_MAP.put('\u2663', Suit.CLUBS);
    CHARACTER_SUIT_MAP.put('\u2667', Suit.CLUBS);
    CHARACTER_SUIT_MAP.put('H', Suit.HEARTS);
    CHARACTER_SUIT_MAP.put('\u2665', Suit.HEARTS);
    CHARACTER_SUIT_MAP.put('\u2661', Suit.HEARTS);
    CHARACTER_SUIT_MAP.put('S', Suit.SPADES);
    CHARACTER_SUIT_MAP.put('\u2660', Suit.SPADES);
    CHARACTER_SUIT_MAP.put('\u2664', Suit.SPADES);
    CHARACTER_SUIT_MAP.put('D', Suit.DIAMONDS);
    CHARACTER_SUIT_MAP.put('\u2666', Suit.DIAMONDS);
    CHARACTER_SUIT_MAP.put('\u2662', Suit.DIAMONDS);
  }

  private Card parseCard(final char value, final char suit) throws StringHandParserException {
    final CardValue cardValue = CHARACTER_CARD_VALUE_MAP.get(value);
    final Suit cardSuit = CHARACTER_SUIT_MAP.get(suit);

    if (cardValue == null) {
      throw new StringHandParserException("Unrecognized card value character '" + value + "'");
    }
    if (cardSuit == null) {
      throw new StringHandParserException("Unrecognized card suit character '" + suit + "'");
    }

    return new Card(cardValue, cardSuit);
  }

  public Card[] parse(final String string) throws StringHandParserException {
    if (string == null) {
      throw new StringHandParserException("A valid hand string needs to be provided");
    }

    final int length = string.length();
    final List<Card> parsedCards = new ArrayList<>(Math.floorDiv(length, 2));
    char currentValue = 0, currentSuit = 0;
    for (int index = 0; index < length; ++index) {
      final char currentChar = string.charAt(index);
      if (Character.isWhitespace(currentChar)) {
        continue;
      }

      if (currentValue == 0) {
        currentValue = currentChar;
      } else if (currentSuit == 0) {
        currentSuit = currentChar;
      }

      if (currentValue != 0 && currentSuit != 0) {
        final Card parsedCard = this.parseCard(currentValue, currentSuit);
        parsedCards.add(parsedCard);

        currentValue = 0;
        currentSuit = 0;
      }
    }

    return parsedCards.toArray(new Card[parsedCards.size()]);
  }
}
