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

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;

public class UnboundedSpScPipeFactory implements IPipeFactory {

	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort) {
		return this.create(sourcePort, targetPort, 0);
	}

	/**
	 * {@inheritDoc}
	 *
	 * The capacity is ignored.
	 */
	@Override
	public <T> IPipe create(final OutputPort<? extends T> sourcePort, final InputPort<T> targetPort, final int capacity) {
		return new UnboundedSpScPipe(sourcePort, targetPort);
	}

	@Override
	public ThreadCommunication getThreadCommunication() {
		return ThreadCommunication.INTER;
	}

	@Override
	public PipeOrdering getOrdering() {
		return PipeOrdering.QUEUE_BASED;
	}

	@Override
	public boolean isGrowable() {
		return true;
	}

}
