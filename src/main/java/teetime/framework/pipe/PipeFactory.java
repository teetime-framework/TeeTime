package teetime.framework.pipe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipeFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipeFactory.class);

	public enum ThreadCommunication {
		INTER, INTRA
	}

	public enum PipeOrdering {
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

	public static PipeFactory INSTANCE = new PipeFactory();

	private PipeFactory() {
		try {
			List<IPipeFactory> pipeFactories = PipeFactoryLoader.loadFromFile("conf/pipe-factories.conf");
			for (IPipeFactory pipeFactory : pipeFactories) {
				this.register(pipeFactory);
			}
		} catch (IOException e) {
			LOGGER.warn("Could not load pipe factories from file", e);
		}
	}

	@Deprecated
	public IPipe create(final ThreadCommunication tc, final PipeOrdering ordering, final boolean growable, final int capacity) {
		IPipeFactory pipeFactory = getPipeFactory(tc, ordering, growable);
		return pipeFactory.create(capacity);
	}

	/**
	 * Returns a PipeFactory Instance
	 *
	 * @param tc
	 *            Communication type between two connected stages
	 * @param ordering
	 *            Specifies the ordering behavior of the pipe
	 * @param growable
	 *            Whether the queue size is fixed or not
	 * @return
	 *         A PipeFactory, which provides suitable pipes
	 */
	public IPipeFactory getPipeFactory(final ThreadCommunication tc, final PipeOrdering ordering, final boolean growable) {
		String key = this.buildKey(tc, ordering, growable);
		IPipeFactory pipeFactory = this.pipeFactories.get(key);
		if (null == pipeFactory) {
			throw new CouldNotFindPipeImplException(key);
		}
		return pipeFactory;
	}

	/**
	 *
	 * @param pipeFactory
	 */
	public void register(final IPipeFactory pipeFactory) {
		String key = this.buildKey(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
		this.pipeFactories.put(key, pipeFactory);
		LOGGER.info("Registered pipe factory: " + pipeFactory.getClass().getCanonicalName());
	}

	/**
	 *
	 * @param tc
	 * @param ordering
	 * @param growable
	 * @return
	 */
	private String buildKey(final ThreadCommunication tc, final PipeOrdering ordering, final boolean growable) {
		return tc.toString() + ordering.toString() + growable;
	}
}
