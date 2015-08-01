/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://christianwulf.github.io/teetime)
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
package teetime.framework;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.exceptionHandling.AbstractExceptionListener;
import teetime.framework.exceptionHandling.AbstractExceptionListener.FurtherExecution;
import teetime.framework.exceptionHandling.TerminateException;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;

/**
 * Represents a minimal Stage, with some pre-defined methods.
 * Implemented stages need to adapt all abstract methods with own implementations.
 */
@SuppressWarnings("PMD.AbstractNaming")
public abstract class Stage {

	private static final ConcurrentMap<String, Integer> INSTANCES_COUNTER = new ConcurrentHashMap<String, Integer>();
	private static final NotEnoughInputException NOT_ENOUGH_INPUT_EXCEPTION = new NotEnoughInputException();

	private final String id;
	/**
	 * A unique logger instance per stage instance
	 */
	@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
	protected final Logger logger;

	protected AbstractExceptionListener exceptionListener;

	/** The owning thread of this stage if this stage is directly executed by a {@link AbstractRunnableStage}, <code>null</code> otherwise. */
	private Thread owningThread;

	private ConfigurationContext owningContext;

	ConfigurationContext getOwningContext() {
		return owningContext;
	}

	void setOwningContext(final ConfigurationContext owningContext) {
		this.owningContext = owningContext;
	}

	protected Stage() {
		this.id = this.createId();
		this.logger = LoggerFactory.getLogger(this.getClass().getCanonicalName() + ":" + id);
	}

	/**
	 * @return an identifier that is unique among all stage instances. It is especially unique among all instances of the same stage type.
	 */
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + ": " + this.getId();
	}

	private String createId() {
		String simpleName = this.getClass().getSimpleName();

		Integer numInstances = INSTANCES_COUNTER.get(simpleName);
		if (null == numInstances) {
			numInstances = 0;
		}

		String newId = simpleName + "-" + numInstances;
		INSTANCES_COUNTER.put(simpleName, ++numInstances);
		return newId;
	}

	@SuppressWarnings("PMD.DefaultPackage")
	static void clearInstanceCounters() {
		INSTANCES_COUNTER.clear();
	}

	// public abstract Stage getParentStage();
	//
	// public abstract void setParentStage(Stage parentStage, int index);

	protected final void returnNoElement() {
		throw NOT_ENOUGH_INPUT_EXCEPTION;
	}

	/**
	 * This should check, if the OutputPorts are connected correctly. This is needed to avoid NullPointerExceptions and other errors.
	 *
	 * @param invalidPortConnections
	 *            <i>(Passed as parameter for performance reasons)</i>
	 */
	public abstract void validateOutputPorts(List<InvalidPortConnection> invalidPortConnections);

	protected void executeStage() {
		try {
			this.execute();
		} catch (NotEnoughInputException e) {
			throw e;
		} catch (TerminateException e) {
			throw e;
		} catch (Exception e) {
			final FurtherExecution furtherExecution = this.exceptionListener.onStageException(e, this);
			if (furtherExecution == FurtherExecution.TERMINATE) {
				throw TerminateException.INSTANCE;
			}
		}
	}

	protected abstract void execute();

	protected abstract void onSignal(ISignal signal, InputPort<?> inputPort);

	protected abstract TerminationStrategy getTerminationStrategy();

	protected abstract void terminate();

	protected abstract boolean shouldBeTerminated();

	public abstract StageState getCurrentState();

	public Thread getOwningThread() {
		return owningThread;
	}

	void setOwningThread(final Thread owningThread) {
		if (this.owningThread != null && this.owningThread != owningThread) {
			// checks also for "crossing threads"
			// throw new IllegalStateException("Attribute owningThread was set twice each with another thread");
		}
		this.owningThread = owningThread;
	}

	protected abstract List<InputPort<?>> getInputPorts();

	protected abstract List<OutputPort<?>> getOutputPorts();

	// events

	public abstract void onValidating(List<InvalidPortConnection> invalidPortConnections);

	/**
	 * Event that is triggered within the initialization phase of the analysis.
	 * It does not count to the execution time.
	 *
	 * @throws Exception
	 *             an arbitrary exception if an error occurs during the initialization
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public abstract void onInitializing() throws Exception;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public abstract void onStarting() throws Exception;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public abstract void onTerminating() throws Exception;

	protected final void setExceptionHandler(final AbstractExceptionListener exceptionHandler) {
		this.exceptionListener = exceptionHandler;
	}

	protected abstract void removeDynamicPort(OutputPort<?> outputPort);

	protected abstract void removeDynamicPort(InputPort<?> inputPort);

}
