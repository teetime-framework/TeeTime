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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import teetime.framework.Traverser.VisitorBehavior;
import teetime.framework.pipe.DummyPipe;

/**
 * Connects unconnected ports to a dummy pipe
 */
class A0UnconnectedPort implements ITraverserVisitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(A0UnconnectedPort.class);

	@Override
	public VisitorBehavior visit(final Stage stage) {
		return VisitorBehavior.CONTINUE;
	}

	@Override
	public VisitorBehavior visit(final AbstractPort<?> port) {
		if (port.getPipe() == null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Unconnected output port: " + port + ". Connecting with a dummy output port.");
			}
			port.setPipe(DummyPipe.INSTANCE);
			return VisitorBehavior.STOP;
		}
		return VisitorBehavior.CONTINUE;
	}
}
