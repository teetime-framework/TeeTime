package teetime.framework;

/**
 *
 * @author Christian Wulf
 *
 * @param <T>
 *            the type of elements to be sent
 *
 * @since 1.2
 */
public final class DynamicOutputPort<T> extends OutputPort<T> {

	private int index;

	DynamicOutputPort(final Class<T> type, final Stage owningStage, final int index) {
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
