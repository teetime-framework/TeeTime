/**
 * Copyright (C) 2015 TeeTime (http://teetime.sourceforge.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.pipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Registry which provides PipeFactories that are used to create pipes.
 * The instance of this singleton class is saved in {@link PipeFactoryRegistry#INSTANCE}.
 * <p>
 * To get a PipeFactory instance, call {@link #getPipeFactory(ThreadCommunication, PipeOrdering, boolean)}.
 *
 */
public final class PipeFactoryRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(PipeFactoryRegistry.class);

	/**
	 * Represent a communication type between two connected stages
	 */
	public enum ThreadCommunication {
		INTER, INTRA
	}

	/**
	 * Represents the ordering behavior of a pipe
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

	/**
	 * The singleton instance of PipeFactoryRegistry
	 */
	public static final PipeFactoryRegistry INSTANCE = new PipeFactoryRegistry("pipe-factories.conf");

	private PipeFactoryRegistry(final String configFileName) {
		final List<IPipeFactory> pipeFactories = PipeFactoryLoader.loadPipeFactoriesFromClasspath(configFileName);
		for (IPipeFactory pipeFactory : pipeFactories) {
			this.register(pipeFactory);
		}
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
		final String key = this.buildKey(tc, ordering, growable);
		final IPipeFactory pipeFactory = this.pipeFactories.get(key);
		if (null == pipeFactory) {
			throw new CouldNotFindPipeImplException(key);
		}
		return pipeFactory;
	}

	/**
	 * Adds a new PipeFactory to the registry.<br />
	 * The new PipeFactory will be automatically selected by the Registry, if it is the most suitable Factory
	 * corresponding to the requirements.
	 *
	 * @param pipeFactory
	 *            A PipeFactory which will be added to the registry
	 */
	public void register(final IPipeFactory pipeFactory) {
		final String key = this.buildKey(pipeFactory.getThreadCommunication(), pipeFactory.getOrdering(), pipeFactory.isGrowable());
		this.pipeFactories.put(key, pipeFactory);
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Registered pipe factory: " + pipeFactory.getClass().getCanonicalName());
		}
	}

	private String buildKey(final ThreadCommunication tc, final PipeOrdering ordering, final boolean growable) {
		return tc.toString() + ordering.toString() + growable;
	}
}
