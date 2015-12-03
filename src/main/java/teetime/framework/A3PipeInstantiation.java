/**
 * Copyright (C) 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.BoundedSynchedPipe;
import teetime.framework.pipe.DummyPipe;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.UnboundedSynchedPipe;
import teetime.framework.pipe.UnsynchedPipe;

/**
 * Automatically instantiates the correct pipes
 */
class A3PipeInstantiation implements ITraverserVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(A3PipeInstantiation.class);

	private final Set<IPipe<?>> visitedPipes = new HashSet<IPipe<?>>();

	@Override
	public VisitorBehavior visit(final AbstractStage stage) {
		return VisitorBehavior.CONTINUE;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		IPipe<?> pipe = port.getPipe();
		if (visitedPipes.contains(pipe)) {
			return VisitorBehavior.STOP;
		}
		visitedPipes.add(pipe);

		instantiatePipe(pipe);

		return VisitorBehavior.CONTINUE;
	}

	private <T> void instantiatePipe(final IPipe<T> pipe) {
		if (!(pipe instanceof InstantiationPipe)) { // if manually connected
			return;
		}

		Thread sourceStageThread = pipe.getSourcePort().getOwningStage().getOwningThread();
		Thread targetStageThread = pipe.getTargetPort().getOwningStage().getOwningThread();

		if (targetStageThread != null && sourceStageThread != targetStageThread) {
			// inter
			if (pipe.capacity() != 0) {
				new BoundedSynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort(), pipe.capacity());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Connected (bounded) " + pipe.getSourcePort() + " and " + pipe.getTargetPort());
				}
			} else {
				new UnboundedSynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Connected (unbounded) " + pipe.getSourcePort() + " and " + pipe.getTargetPort());
				}
			}
		} else {
			// normal or reflexive pipe => intra
			new UnsynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Connected (unsynch) " + pipe.getSourcePort() + " and " + pipe.getTargetPort());
			}
		}

	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Unconnected port " + port + " in stage " + port.getOwningStage().getId());
		}
	}

}
