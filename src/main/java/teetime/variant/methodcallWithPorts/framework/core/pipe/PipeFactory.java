package teetime.variant.methodcallWithPorts.framework.core.pipe;

import java.util.HashMap;
import java.util.Map;

public class PipeFactory {

	public enum ThreadCommunication {
		INTER, INTRA
	}

	public enum Ordering {
		/**
		 * FIFO
		 */
		QUEUE_BASED,
		/**
		 * LIFO
		 */
		STACK_BASED,
		ARBITRARY
	}

	private final Map<String, IPipeFactory> pipeFactories = new HashMap<String, IPipeFactory>();

	/**
	 * Creates a new FIFO-ordered, growable pipe with an initial capacity of 1. <br>
	 * <i>This method is suitable for most programmers.</i>
	 *
	 * @param tc
	 * @return
	 */
	public <T> IPipe<T> create(final ThreadCommunication tc) {
		return this.create(tc, Ordering.QUEUE_BASED, true, 1);
	}

	public <T> IPipe<T> create(final ThreadCommunication tc, final Ordering ordering, final boolean growable, final int capacity) {
		String key = this.buildKey(tc, ordering, growable);
		IPipeFactory pipeClass = this.pipeFactories.get(key);
		return pipeClass.create(capacity);
	}

	private String buildKey(final ThreadCommunication tc, final Ordering ordering, final boolean growable) {
		return tc.toString() + ordering.toString() + growable;
	}

	public void register(final IPipeFactory pipeFactory, final ThreadCommunication tc, final Ordering ordering, final boolean growable) {
		String key = this.buildKey(tc, ordering, growable);
		this.pipeFactories.put(key, pipeFactory);
	}
}
