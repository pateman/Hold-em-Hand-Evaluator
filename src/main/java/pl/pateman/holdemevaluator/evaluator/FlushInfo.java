package pl.pateman.holdemevaluator.evaluator;

import pl.pateman.holdemevaluator.HandName;

final class FlushInfo {

  static final int NO_FLUSH = 0;

  private final int flushSuit;
  private final HandName outcome;

  FlushInfo(final int flushSuit, final HandName outcome) {
    this.flushSuit = flushSuit;
    this.outcome = outcome;
  }

  boolean isFlush() {
    return this.flushSuit != NO_FLUSH;
  }

  int getFlushSuit() {
    return flushSuit;
  }

  HandName getOutcome() {
    return outcome;
  }
}
