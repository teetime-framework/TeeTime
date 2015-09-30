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
package teetime.framework.validation;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;

public class InvalidPortConnection {

	private final OutputPort<?> sourcePort;
	private final InputPort<?> targetPort;

	public InvalidPortConnection(final OutputPort<?> sourcePort, final InputPort<?> targetPort) {
		super();
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
	}

	public OutputPort<?> getSourcePort() {
		return this.sourcePort;
	}

	public InputPort<?> getTargetPort() {
		return this.targetPort;
	}

	@Override
	public String toString() {
		final String sourcePortTypeName = (this.sourcePort.getType() == null) ? "null" : this.sourcePort.getType().getName();
		final String targetPortTypeName = (this.targetPort.getType() == null) ? "null" : this.targetPort.getType().getName();
		return "Source port type does not match target port type: " + sourcePortTypeName + " != " + targetPortTypeName;
	}

}
