package teetime.variant.explicitScheduling.framework.core;

/**
 *
 * @author Christian Wulf
 *
 * @param <S>
 *            the stage, this port belongs to<br>
 *            <i>(used for ensuring type safety)</i>
 * @param <T>
 */
public interface IInputPort<S extends IStage, T> extends IPort<S, T> {

	/**
	 * @since 1.10
	 */
	public enum PortState {
		OPENED, CLOSED
	}

	/**
	 * @since 1.10
	 */
	public abstract PortState getState();

	/**
	 * @since 1.10
	 */
	public abstract void setState(final PortState state);

	/**
	 * @since 1.10
	 */
	public abstract void setPortListener(final IPortListener portListener);

	/**
	 * @since 1.10
	 */
	public void close();
}
