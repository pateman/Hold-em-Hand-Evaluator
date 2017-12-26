package pl.pateman.holdemevaluator;

public final class TestUtil {

  private TestUtil() {

  }

  public static  <T> boolean arrayContainsAll(final T[] arrayA, final T[] arrayB) {
    for (final T b : arrayB) {
      boolean aContainsB = false;
      for (final T a : arrayA) {
        if (a.equals(b)) {
          aContainsB = true;
          break;
        }
      }

      if (!aContainsB) {
        return false;
      }
    }
    return true;
  }
}
