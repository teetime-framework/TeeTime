package teetime.util.stage.basic;

@SuppressWarnings("PMD.UseVarargs")
public class CyclicIndex {

	private int index;

	/**
	 * Reads the element and increments the internal index afterwards.
	 *
	 * @param elements
	 * @return the next element of the given elements.
	 */
	public <T> T getNextElement(final T[] elements) {
		final T element = elements[index];

		index = (index + 1) % elements.length;

		return element;
	}

	public void ensureWithinBounds(final Object[] elements) {
		index = index % elements.length;
	}
}
