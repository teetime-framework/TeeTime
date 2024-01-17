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
package teetime.stage;

import java.io.File;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.google.common.io.Files;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class FileExtensionSwitch extends AbstractConsumerStage<File> {

	private final OutputPort<File> unknownFileExtensionOutputPort = createOutputPort(File.class);

	private final ObjectObjectMap<String, OutputPort<File>> fileExtensions = new ObjectObjectHashMap<>();

	@Override
	protected void execute(final File file) {
		String fileExtension = Files.getFileExtension(file.getAbsolutePath());
		OutputPort<File> outputPort = this.fileExtensions.getOrDefault(fileExtension, unknownFileExtensionOutputPort);
		outputPort.send(file);
	}

	@SuppressWarnings("PMD.AvoidReassigningParameters")
	public OutputPort<File> addFileExtension(String fileExtension) {
		if (fileExtension.startsWith(".")) {
			fileExtension = fileExtension.substring(1);
		}
		OutputPort<File> outputPort = this.createOutputPort();
		this.fileExtensions.put(fileExtension, outputPort);
		return outputPort;
	}

}
