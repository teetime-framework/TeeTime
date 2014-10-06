package teetime.stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import teetime.framework.ConsumerStage;
import teetime.framework.OutputPort;

import com.google.common.io.Files;

public class FileExtensionSwitch extends ConsumerStage<File> {

	private final Map<String, OutputPort<File>> fileExtensions = new HashMap<String, OutputPort<File>>();

	@Override
	protected void execute(final File file) {
		String fileExtension = Files.getFileExtension(file.getAbsolutePath());
		this.logger.debug("fileExtension: " + fileExtension);
		OutputPort<File> outputPort = this.fileExtensions.get(fileExtension);
		if (outputPort != null) {
			this.send(outputPort, file);
		}
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
