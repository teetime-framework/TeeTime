package teetime.stage.basic;

import teetime.framework.core.AbstractFilter;
import teetime.framework.core.Context;
import teetime.framework.core.IInputPort;
import teetime.framework.core.IOutputPort;

public class Delay<T> extends AbstractFilter<Delay<T>> {

	public final IInputPort<Delay<T>, Object> signalInputPort = createInputPort();
	public final IInputPort<Delay<T>, T> objectInputPort = createInputPort();

	public final IOutputPort<Delay<T>, T> relayOutputPort = createOutputPort();

	@Override
	protected boolean execute(final Context<Delay<T>> context) {
		final Object signal = context.tryTake(signalInputPort);
		if (signal == null) {
			return false;
		}

		final T object = context.tryTake(objectInputPort);
		if (object == null) {
			return false;
		}

		context.put(relayOutputPort, object);

		return true;
	}

}
