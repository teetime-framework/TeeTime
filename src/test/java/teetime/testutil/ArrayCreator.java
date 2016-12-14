package teetime.testutil;

import java.util.Random;

/**
 *
 * @author Christian Wulf
 *
 */
public class ArrayCreator {

	private final Random random;

	public ArrayCreator(final long seed) {
		random = new Random(seed);
	}

	public int[] createFilledArray(final int numValues) {
		int[] randomValues = new int[numValues];

		for (int i = 0; i < randomValues.length; i++) {
			randomValues[i] = random.nextInt();
		}

		return randomValues;
	}

}
