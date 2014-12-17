package teetime.stage;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.AbstractConsumerStage;

/**
 * @param <T>
 *            the type of the elements to be collected
 *
 * @author Christian Wulf
 *
 * @since 1.0
 */
public final class CollectorSink<T> extends AbstractConsumerStage<T> {

	private final List<T> elements;

	/**
	 * Creates a new {@link CollectorSink} with an empty {@link ArrayList}.
	 */
	public CollectorSink() {
		this(new ArrayList<T>());
	}

	public CollectorSink(final List<T> list) {
		this.elements = list;
	}

	@Override
	protected void execute(final T element) {
		this.elements.add(element);
	}

	public List<T> getElements() {
		return elements;
	}

}
