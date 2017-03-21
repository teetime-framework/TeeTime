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
package teetime.framework.scheduling.pushpullmodel;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.*;
import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.*;
import teetime.framework.scheduling.PipeScheduler;

/**
 * Automatically instantiates the correct pipes
 */
class A3PipeInstantiation implements ITraverserVisitor, PipeScheduler {

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
			return VisitorBehavior.STOP; // NOPMD two returns are better
		}
		visitedPipes.add(pipe);

		instantiatePipe(pipe);

		return VisitorBehavior.CONTINUE;
	}

	@Override
	public void visit(final DummyPipe pipe, final AbstractPort<?> port) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Unconnected port {} in stage {}", port, port.getOwningStage().getId());
		}
	}

	private <T> void instantiatePipe(final IPipe<T> pipe) {
		IPipe<T> instantiatedPipe;

		AbstractStage sourceStage = pipe.getSourcePort().getOwningStage();
		AbstractStage targetStage = pipe.getTargetPort().getOwningStage();

		if (!(pipe instanceof InstantiationPipe)) { // if manually connected
			instantiatedPipe = pipe;
		} else if (!targetStage.isActive() || sourceStage == targetStage) { // NOPMD .equals() can't be used here
			// normal or reflexive pipe => intra
			instantiatedPipe = new UnsynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort());
			LOGGER.debug("Connected (unsynch) {} and {}", pipe.getSourcePort(), pipe.getTargetPort());
		} else if (pipe.capacity() == 0) {
			// synchronized, unlimited capacity
			instantiatedPipe = new UnboundedSynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort());
			LOGGER.debug("Connected (unbounded) {} and {}", pipe.getSourcePort(), pipe.getTargetPort());
		} else {
			// synchronized, limited capacity
			instantiatedPipe = new BoundedSynchedPipe<T>(pipe.getSourcePort(), pipe.getTargetPort(), pipe.capacity());
			LOGGER.debug("Connected (bounded) {} and {}", pipe.getSourcePort(), pipe.getTargetPort());
		}

		instantiatedPipe.setScheduler(this);
	}

	@Override
	public void onElementAdded(final AbstractUnsynchedPipe<?> pipe) {
		pipe.getCachedTargetStage().executeByFramework();
	}

	@Override
	public void onElementAdded(final AbstractSynchedPipe<?> pipe) {
		// do nothing
	}

}
