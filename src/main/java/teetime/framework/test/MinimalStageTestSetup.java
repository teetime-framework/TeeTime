package teetime.framework.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

abstract class MinimalStageTestSetup {

	protected MinimalStageTestSetup() {
		// do nothing
	}

	/**
	 * @param elements
	 *            which serve as input. If nothing should be sent, pass
	 */
	@SafeVarargs
	public final <I> InputHolder<I> send(final I... elements) {
		return this.send(Arrays.asList(elements));
	}

	/**
	 * @param elements
	 *            which serve as input. If nothing should be sent, pass
	 *
	 *            <pre>
	 * Collections.&lt;your type&gt;emptyList().
	 *            </pre>
	 */
	public abstract <I> InputHolder<I> send(final Collection<I> elements);

	/**
	 * @param actualElements
	 *            which should be tested against the expected elements.
	 */
	public abstract <O> OutputHolder<O> receive(final List<O> actualElements);
}
