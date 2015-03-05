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

import teetime.framework.pipe.IPipe;

public abstract class AbstractPipe implements IPipe {

	/**
	 * Performance cache: Avoids the following method chain
	 *
	 * <pre>
	 * this.getPipe().getTargetPort().getOwningStage()
	 * </pre>
	 */
	protected Stage cachedTargetStage;

	private InputPort<?> targetPort;

	protected <T> AbstractPipe(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		this.targetPort = targetPort;
		if (null != targetPort) { // BETTER remove this check if migration is completed
			this.cachedTargetStage = targetPort.getOwningStage();
		}
		if (null != sourcePort) { // BETTER remove this check if migration is completed
			sourcePort.setPipe(this);
		}
		if (null != targetPort) { // BETTER remove this check if migration is completed
			targetPort.setPipe(this);
		}
	}

	@Override
	public InputPort<?> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public <T> void connectPorts(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		sourcePort.setPipe(this);
		targetPort.setPipe(this);
		this.targetPort = targetPort;
		this.cachedTargetStage = targetPort.getOwningStage();
	}

	@Override
	public final boolean hasMore() {
		return !isEmpty();
	}
}
