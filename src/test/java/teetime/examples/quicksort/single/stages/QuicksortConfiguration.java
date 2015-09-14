package teetime.examples.quicksort.single.stages;

import java.util.List;

import teetime.framework.CombineStage;
import teetime.framework.Configuration;
import teetime.framework.DivideStage;
import teetime.framework.SimpleDivideAndConquerStage;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

public class QuicksortConfiguration extends Configuration {

	public QuicksortConfiguration(final List<QuicksortProblem> inputs, final List<QuicksortSolution> results) {
		// set up stages
		InitialElementProducer<QuicksortProblem> initialElementProducer = new InitialElementProducer<QuicksortProblem>(inputs);
		DivideStage<QuicksortProblem, QuicksortSolution> divide = new DivideStage<QuicksortProblem, QuicksortSolution>();
		SimpleDivideAndConquerStage<QuicksortProblem, QuicksortSolution> solveOne = new SimpleDivideAndConquerStage<QuicksortProblem, QuicksortSolution>();
		SimpleDivideAndConquerStage<QuicksortProblem, QuicksortSolution> solveTwo = new SimpleDivideAndConquerStage<QuicksortProblem, QuicksortSolution>();
		CombineStage<QuicksortProblem, QuicksortSolution> combine = new CombineStage<QuicksortProblem, QuicksortSolution>();
		CollectorSink<QuicksortSolution> collectorSink = new CollectorSink<QuicksortSolution>(results);
		this.declareActive(divide);
		this.declareActive(solveOne);
		this.declareActive(solveTwo);
		this.declareActive(combine);

		// connect ports
		connectPorts(initialElementProducer.getOutputPort(), divide.getInputPort());
		connectPorts(divide.getFirstOutputPort(), solveOne.getInputPort());
		connectPorts(divide.getSecondOutputPort(), solveTwo.getInputPort());
		connectPorts(solveOne.getOutputPort(), combine.getFirstInputPort());
		connectPorts(solveTwo.getOutputPort(), combine.getSecondInputPort());
		connectPorts(combine.getOutputPort(), collectorSink.getInputPort());
	}
}
