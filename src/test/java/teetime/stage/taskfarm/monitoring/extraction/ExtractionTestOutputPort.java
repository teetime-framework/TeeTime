package teetime.stage.taskfarm.monitoring.extraction;

import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.stage.NoopFilter;

class ExtractionTestOutputPort<T> extends OutputPort<T> {

	@Override
	public Stage getOwningStage() {
		return new NoopFilter<T>();
	}

}
