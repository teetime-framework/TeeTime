package teetime.variant.methodcallWithPorts.stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import teetime.variant.methodcallWithPorts.framework.core.ConsumerStage;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;

import com.google.common.io.Files;

public class FileExtensionSwitch extends ConsumerStage<File, File> {

	private final Map<String, OutputPort<File>> fileExtensions = new HashMap<String, OutputPort<File>>();

	@Override
	protected void execute5(final File file) {
		String fileExtension = Files.getFileExtension(file.getAbsolutePath());
		OutputPort<File> outputPort = this.fileExtensions.get(fileExtension);
		outputPort.send(file);
	}

	public OutputPort<File> addFileExtension(final String fileExtension) {
		OutputPort<File> outputPort = new OutputPort<File>();
		this.fileExtensions.put(fileExtension, outputPort);
		return outputPort;
	}

}
