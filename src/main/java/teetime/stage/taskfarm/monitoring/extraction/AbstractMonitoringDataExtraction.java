package teetime.stage.taskfarm.monitoring.extraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public abstract class AbstractMonitoringDataExtraction {

	private final static String NEWLINE = System.getProperty("line.separator");
	private final PipeMonitoringService pipeMonitoringService;
	private final SingleTaskFarmMonitoringService taskFarmMonitoringService;

	public AbstractMonitoringDataExtraction(final PipeMonitoringService pipeMonitoringService, final SingleTaskFarmMonitoringService taskFarmMonitoringService) {
		this.pipeMonitoringService = pipeMonitoringService;
		this.taskFarmMonitoringService = taskFarmMonitoringService;
	}

	protected abstract void extractToWriter(Writer writer);

	public void extractToFile(final File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter writer = new FileWriter(file, false);

		this.extractToWriter(writer);

		writer.close();
	}

	public void extractToFile(final String filepath) throws IOException {
		File file = new File(filepath);
		extractToFile(file);
	}

	public String extractToString() {
		StringWriter writer = new StringWriter();

		this.extractToWriter(writer);

		return writer.toString();
	}

	public static void addCSVLineToWriter(final Writer writer, final String... args) throws IOException {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < args.length; i++) {
			builder.append(args[i]);
			if (i != args.length - 1) {
				builder.append(",");
			}
		}

		builder.append(NEWLINE);

		writer.append(builder.toString());
	}

	public PipeMonitoringService getPipeMonitoringService() {
		return pipeMonitoringService;
	}

	public SingleTaskFarmMonitoringService getTaskFarmMonitoringService() {
		return taskFarmMonitoringService;
	}
}
