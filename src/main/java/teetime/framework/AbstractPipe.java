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

import teetime.framework.pipe.IPipe;

public abstract class AbstractPipe implements IPipe {

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	protected final Stage cachedTargetStage;

	private final OutputPort<?> sourcePort;
	private final InputPort<?> targetPort;

	protected <T> AbstractPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
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
	public OutputPort<?> getSourcePort() {
		return sourcePort;
	}

	@Override
	public InputPort<?> getTargetPort() {
		return targetPort;
	}

	@Override
	public final boolean hasMore() {
		return !isEmpty();
	}
}
