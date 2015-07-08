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
package teetime.stage;

import java.nio.charset.Charset;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class ByteArray2String extends AbstractConsumerStage<byte[]> {

	private final OutputPort<String> outputPort = this.createOutputPort();

	@Override
	protected void execute(final byte[] element) {
		outputPort.send(new String(element, Charset.forName("UTF-8")));
	}

	public OutputPort<? extends String> getOutputPort() {
		return this.outputPort;
	}
}
