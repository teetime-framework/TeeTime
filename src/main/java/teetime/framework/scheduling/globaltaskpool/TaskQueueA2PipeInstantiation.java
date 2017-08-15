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
package teetime.framework.scheduling.globaltaskpool;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.AbstractPort;
import teetime.framework.AbstractStage;
import teetime.framework.ITraverserVisitor;
import teetime.framework.Traverser;
import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.InstantiationPipe;

/**
 * Created by nilsziermann on 30.12.16.
 */
class TaskQueueA2PipeInstantiation implements ITraverserVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskQueueA2PipeInstantiation.class);

	private final Set<IPipe<?>> visitedPipes = new HashSet<IPipe<?>>();

	@Override
	public Traverser.VisitorBehavior visit(final AbstractStage stage) {
		return Traverser.VisitorBehavior.CONTINUE_BACK_AND_FORTH;
	}

	@Override
	public Traverser.VisitorBehavior visit(final AbstractPort<?> port) {
		IPipe<?> pipe = port.getPipe();
		if (visitedPipes.contains(pipe)) {
			return Traverser.VisitorBehavior.STOP; // NOPMD two returns are better
		}
		visitedPipes.add(pipe);

		instantiatePipe(pipe);

		return Traverser.VisitorBehavior.CONTINUE_BACK_AND_FORTH;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Unconnected port " + port + " in stage " + port.getOwningStage().getId());
		}
	}

	private <T> void instantiatePipe(final IPipe<T> pipe) {
		if (!(pipe instanceof InstantiationPipe)) { // if manually connected
			return;
		}

		new UnboundedMpMcSynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort());
		LOGGER.debug("Connected (unbounded MpMc) {} and {}", pipe.getSourcePort(), pipe.getTargetPort());
	}
}
