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

import java.util.HashSet;
import java.util.Set;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.IPipe;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.InstantiationPipe;
import teetime.framework.pipe.SingleElementPipeFactory;
import teetime.framework.pipe.SpScPipeFactory;
import teetime.framework.pipe.UnboundedSpScPipeFactory;

public class A3PipeInstantiation implements ITraverserVisitor {

	private static final IPipeFactory interBoundedThreadPipeFactory = new SpScPipeFactory();
	private static final IPipeFactory interUnboundedThreadPipeFactory = new UnboundedSpScPipeFactory();
	private static final IPipeFactory intraThreadPipeFactory = new SingleElementPipeFactory();

	private final Set<IPipe<?>> visitedPipes = new HashSet<IPipe<?>>();

	@Override
	public VisitorBehavior visit(final Stage stage) {
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
				interBoundedThreadPipeFactory.create(pipe.getSourcePort(), pipe.getTargetPort(), pipe.capacity());
			} else {
				interUnboundedThreadPipeFactory.create(pipe.getSourcePort(), pipe.getTargetPort(), 4);
			}
			return;
		} else {
			// normal or reflexive pipe => intra
		}

		intraThreadPipeFactory.create(pipe.getSourcePort(), pipe.getTargetPort(), 4);
	}

}
