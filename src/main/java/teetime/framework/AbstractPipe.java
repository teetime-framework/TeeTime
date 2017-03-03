/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework;

import teetime.framework.pipe.IPipe;

/**
 * Represents an abstract implementation of a {@link IPipe}.
 *
 * @author Christian Wulf (chw)
 *
 * @param <T>
 *            the type of the elements which this pipe should transfer.
 */
public abstract class AbstractPipe<T> implements IPipe<T> {

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	protected final AbstractStage cachedTargetStage;

	private final OutputPort<? extends T> sourcePort;
	private final InputPort<T> targetPort;

	protected AbstractPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		if (sourcePort == null) {
			throw new IllegalArgumentException("sourcePort may not be null");
		}
		if (targetPort == null) {
			throw new IllegalArgumentException("targetPort may not be null");
		}

		sourcePort.setPipe(this);
		targetPort.setPipe(this);

		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
		this.cachedTargetStage = targetPort.getOwningStage();
	}

	@Override
	public final OutputPort<? extends T> getSourcePort() {
		return sourcePort;
	}

	@Override
	public final InputPort<T> getTargetPort() {
		return targetPort;
	}

	@Override
	public final boolean hasMore() {
		return !isEmpty();
	}

	@Override
	public String toString() {
		return sourcePort.getOwningStage().getId() + " -> " + targetPort.getOwningStage().getId() + " (" + super.toString() + ")";
	}

	@Override
	public void close() {
		close(cachedTargetStage);
	}

	/* default */ static void close(final AbstractStage stage) {
		int numOpenInputPorts = stage.getNumOpenedInputPorts().decrementAndGet();
		stage.logger.debug("numOpenedInputPorts (dec): {}", numOpenInputPorts);
		if (numOpenInputPorts <= 0) {
			stage.terminateStageByFramework();
		}
	}
}
