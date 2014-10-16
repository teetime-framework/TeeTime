package teetime.framework.pipe;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides PipeFactories, which are used to connect stages.
 * The instance of this singleton class is saved in <code>PipeFactoryRegistry.INSTANCE</code>.
 * <p>
 * To get a PipeFactory instance, call {@link #getPipeFactory(ThreadCommunication, PipeOrdering, boolean)}.
 *
 */
public class PipeFactoryRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipeFactoryRegistry.class);

	/**
	 * Communication type between two connected stages
	 */
	public enum ThreadCommunication {
		INTER, INTRA
	}

	/**
	 * Specifies the ordering behavior of the pipe
	 */
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

	public static PipeFactoryRegistry INSTANCE = new PipeFactoryRegistry();

	private PipeFactoryRegistry() {
		try {
			List<IPipeFactory> pipeFactories = PipeFactoryLoader.loadFromFile("conf/pipe-factories.conf");
			for (IPipeFactory pipeFactory : pipeFactories) {
				this.register(pipeFactory);
			}
		} catch (IOException e) {
			LOGGER.warn("Could not load pipe factories from file", e);
		}
	}

	/**
	 * @deprecated Use {@link IPipeFactory#create(OutputPort, InputPort, int)} instead,
	 *             after obtaining a PipeFactory with {@link #getPipeFactory(ThreadCommunication, PipeOrdering, boolean)}.
	 *
	 * @param tc
	 * @param ordering
	 * @param growable
	 * @param capacity
	 * @return
	 */
	@Deprecated
	public IPipe create(final ThreadCommunication tc, final PipeOrdering ordering, final boolean growable, final int capacity) {
		IPipeFactory pipeFactory = getPipeFactory(tc, ordering, growable);
		return pipeFactory.create(capacity);
	}

	/**
	 * Returns a PipeFactory Instance.
	 *
	 * @param tc
	 *            Communication type between two connected stages. These are defined in PipeFactoryRegistry.ThreadCommunication
	 * @param ordering
	 *            Specifies the ordering behavior of the pipe. See PipeFactoryRegistry.PipeOrdering
	 * @param growable
	 *            Whether the queue size is fixed or not.
	 * @return
	 *         A PipeFactory, which provides suitable pipes.
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
	 * Registers a new PipeFactory to the registry.<br />
	 * The new PipeFactory will be automatically selected by the Registry, if it is the most suitable Factory
	 * corresponding to the requirements.
	 *
	 * @param pipeFactory
	 *            A PipeFactory which will be added to the registry
	 */
	public void register(final IPipeFactory pipeFactory) {
		String key = this.buildKey(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
		this.pipeFactories.put(key, pipeFactory);
		LOGGER.info("Registered pipe factory: " + pipeFactory.getClass().getCanonicalName());
	}

	private String buildKey(final ThreadCommunication tc, final PipeOrdering ordering, final boolean growable) {
		return tc.toString() + ordering.toString() + growable;
	}
}
