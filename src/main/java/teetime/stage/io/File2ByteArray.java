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
package teetime.stage.io;

import java.io.File;
import java.io.IOException;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

import com.google.common.io.Files;

public class File2ByteArray extends AbstractConsumerStage<File> {

	private final OutputPort<byte[]> outputPort = this.createOutputPort();

	@Override
	protected void execute(final File element) {
		try {
			byte[] cache = Files.toByteArray(element);
			outputPort.send(cache);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public OutputPort<? extends byte[]> getOutputPort() {
		return this.outputPort;
	}

}
