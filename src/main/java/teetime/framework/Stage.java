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
package teetime.framework;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/** The owning thread of this stage if this stage is directly executed by a {@link AbstractRunnableStage}, <code>null</code> otherwise. */
	protected Thread owningThread;

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

	protected abstract void executeStage();

	protected abstract void onSignal(ISignal signal, InputPort<?> inputPort);

	protected abstract TerminationStrategy getTerminationStrategy();

	protected abstract void terminate();

	protected abstract boolean shouldBeTerminated();

	public abstract StageState getCurrentState();

	public Thread getOwningThread() {
		return owningThread;
	}

	@SuppressWarnings("PMD.DefaultPackage")
	void setOwningThread(final Thread owningThread) {
		this.owningThread = owningThread;
	}

	protected abstract InputPort<?>[] getInputPorts();

	// events

	public abstract void onValidating(List<InvalidPortConnection> invalidPortConnections);

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public abstract void onStarting() throws Exception;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public abstract void onTerminating() throws Exception;

}
