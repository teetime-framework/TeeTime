package teetime.framework;

/**
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of elements to be received
 *
 * @since 1.2
 */
public final class DynamicInputPort<T> extends InputPort<T> {

	private int index;

	DynamicInputPort(final Class<T> type, final Stage owningStage, final int index) {
		super(type, owningStage);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(final int index) {
		this.index = index;
	}

}
