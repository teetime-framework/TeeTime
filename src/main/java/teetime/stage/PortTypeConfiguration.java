package teetime.stage;

import teetime.util.TimestampObject;

public class PortTypeConfiguration {

	public static <T> void setPortTypes(final ObjectProducer<T> stage, final Class<T> clazz) {
		stage.getOutputPort().setType(clazz);
	}

	public static <T> void setPortTypes(final CollectorSink<T> stage, final Class<T> clazz) {
		stage.getInputPort().setType(clazz);
	}

	public static <T> void setPortTypes(final StartTimestampFilter stage) {
		stage.getInputPort().setType(TimestampObject.class);
		stage.getOutputPort().setType(TimestampObject.class);
	}
}
