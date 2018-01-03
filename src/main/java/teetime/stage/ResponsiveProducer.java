package teetime.stage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import teetime.framework.AbstractProducerStage;

/**
 * Represents a producer stage which outputs one element per execution.
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of the elements
 *
 * @see InitialElementProducer
 *
 * @since 3.0
 */
public class ResponsiveProducer<T> extends AbstractProducerStage<T> {
	// #281: not Iterable<T> since it would forbid to pass java.nio.Path as single object
	private final Iterator<T> iterator;

	@SafeVarargs
	public ResponsiveProducer(final T... elements) {
		this(Arrays.asList(elements));
	}

	public ResponsiveProducer(final Collection<T> elements) {
		if (elements == null) {
			throw new IllegalArgumentException("4002 - The given collection must not be null.");
		}
		if (elements.isEmpty()) {
			logger.warn("The given collection is empty! This stage will not output anything.");
		}
		this.iterator = elements.iterator();
	}

	@Override
	protected void execute() throws Exception {
		if (iterator.hasNext()) {
			T element = iterator.next();
			this.outputPort.send(element);
		} else {
			this.workCompleted();
		}
	}

	// TODO uncomment for arbitrary scheduling approaches
	// @Override
	// protected boolean shouldTerminate() {
	// return !iterator.hasNext();
	// }
}
