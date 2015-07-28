package teetime.stage.taskfarm.monitoring.extraction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;
import teetime.stage.taskfarm.monitoring.TaskFarmMonitoringService;

public abstract class AbstractMonitoringDataExtraction {

	private final static String NEWLINE = System.getProperty("line.separator");
	private final PipeMonitoringService pipeMonitoringService;
	private final TaskFarmMonitoringService taskFarmMonitoringService;

	public AbstractMonitoringDataExtraction(final PipeMonitoringService pipeMonitoringService, final TaskFarmMonitoringService taskFarmMonitoringService) {
		this.pipeMonitoringService = pipeMonitoringService;
		this.taskFarmMonitoringService = taskFarmMonitoringService;
	}

	protected abstract void extractToWriter(Writer writer);

	public void extractToFile(final File file) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(file);

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

		this.extractToWriter(writer);

		writer.close();
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

	public TaskFarmMonitoringService getTaskFarmMonitoringService() {
		return taskFarmMonitoringService;
	}
}
