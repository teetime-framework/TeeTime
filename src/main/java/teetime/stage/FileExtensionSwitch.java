package teetime.stage;

import java.io.File;
import java.util.Map;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;
import teetime.util.HashMapWithDefault;
import teetime.util.concurrent.hashmap.ValueFactory;

import com.google.common.io.Files;

public final class FileExtensionSwitch extends AbstractConsumerStage<File> {

	private final OutputPort<File> unknownFileExtensionOutputPort = createOutputPort();

	// BETTER use the hppc ObjectObjectMap that provide getOrDefault()
	private final Map<String, OutputPort<File>> fileExtensions = new HashMapWithDefault<String, OutputPort<File>>(new ValueFactory<OutputPort<File>>() {
		@Override
		public OutputPort<File> create() {
			return unknownFileExtensionOutputPort;
		}
	});

	@Override
	protected void execute(final File file) {
		String fileExtension = Files.getFileExtension(file.getAbsolutePath());
		if (logger.isDebugEnabled()) {
			this.logger.debug("fileExtension: " + fileExtension);
		}

		OutputPort<File> outputPort = this.fileExtensions.get(fileExtension);
		outputPort.send(file);
	}

	public OutputPort<File> addFileExtension(String fileExtension) {
		if (fileExtension.startsWith(".")) {
			fileExtension = fileExtension.substring(1);
		}
		OutputPort<File> outputPort = this.createOutputPort();
		this.fileExtensions.put(fileExtension, outputPort);
		this.logger.debug("SUCCESS: Registered output port for '" + fileExtension + "'");
		return outputPort;
	}

}
