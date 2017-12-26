package pl.pateman.holdemevaluator.evaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.IntegrationTest;
import pl.pateman.holdemevaluator.parser.StringHandParser;
import pl.pateman.holdemevaluator.parser.StringHandParserException;

@Category(IntegrationTest.class)
public class HandOutcomeTest {

  private HoldemEvaluator evaluator;
  private StringHandParser handParser;

  private void assertHandComparison(final String handA, final String handB, final String tableCards,
      final boolean whetherHandAWins) throws StringHandParserException {
    final Card[] parsedHandA = this.handParser.parse(handA);
    final Card[] parsedHandB = this.handParser.parse(handB);
    final Card[] parsedTableCards = this.handParser.parse(tableCards);

    final HandOutcome outcomeA = this.evaluator.calculate(parsedHandA, parsedTableCards);
    final HandOutcome outcomeB = this.evaluator.calculate(parsedHandB, parsedTableCards);
    final int compareTo = outcomeA.compareTo(outcomeB);
    if (whetherHandAWins) {
      assertTrue(compareTo >= 1);
    } else {
      assertEquals(compareTo, -1);
    }
  }

  @Before
  public void setUp() throws Exception {
    this.evaluator = new HoldemEvaluator();
    this.handParser = new StringHandParser();
  }

  @Test
  public void testHandNameComparison() throws Exception {
    //  High card vs one pair.
    this.assertHandComparison("AS KH", "2D 2C", "9H 6C TD JC 7H", false);
    //  High card vs high card.
    this.assertHandComparison("AS KH", "2D 3C", "9H 6C TD JC 7H", true);
  }
}