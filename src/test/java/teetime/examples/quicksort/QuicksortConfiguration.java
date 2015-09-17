package teetime.examples.quicksort;

import java.util.List;

import teetime.framework.Configuration;
import teetime.framework.DivideAndConquerStage;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

public class QuicksortConfiguration extends Configuration {

	public QuicksortConfiguration(final List<QuicksortProblem> inputs, final List<QuicksortSolution> results) {
		// set up stages
		InitialElementProducer<QuicksortProblem> initialElementProducer = new InitialElementProducer<QuicksortProblem>(inputs);
		DivideAndConquerStage<QuicksortProblem, QuicksortSolution> quicksortStage = new DivideAndConquerStage<QuicksortProblem, QuicksortSolution>();
		CollectorSink<QuicksortSolution> collectorSink = new CollectorSink<QuicksortSolution>(results);
		this.declareActive(quicksortStage);
		quicksortStage.setThreshold(2); // set parallelism level to 2

		// connect ports
		connectPorts(initialElementProducer.getOutputPort(), quicksortStage.getInputPort());
		connectPorts(quicksortStage.getOutputPort(), collectorSink.getInputPort());
	}
}
