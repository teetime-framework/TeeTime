package teetime.examples.quicksort.single.stages;

import java.util.List;

import teetime.framework.Configuration;
import teetime.framework.divideandconquer.stages.DivideAndConquerCombineStage;
import teetime.framework.divideandconquer.stages.DivideAndConquerDivideStage;
import teetime.framework.divideandconquer.stages.DivideAndConquerSolveStage;
import teetime.stage.CollectorSink;
import teetime.stage.InitialElementProducer;
import teetime.stage.quicksort.QuicksortProblem;
import teetime.stage.quicksort.QuicksortSolution;

public class QuicksortConfiguration extends Configuration {

	public QuicksortConfiguration(final List<QuicksortProblem> inputs, final List<QuicksortSolution> results) {
		// set up stages
		InitialElementProducer<QuicksortProblem> initialElementProducer = new InitialElementProducer<QuicksortProblem>(inputs);
		DivideAndConquerDivideStage<QuicksortProblem, QuicksortSolution> divide = new DivideAndConquerDivideStage<QuicksortProblem, QuicksortSolution>();
		DivideAndConquerSolveStage<QuicksortProblem, QuicksortSolution> solveOne = new DivideAndConquerSolveStage<QuicksortProblem, QuicksortSolution>();
		DivideAndConquerSolveStage<QuicksortProblem, QuicksortSolution> solveTwo = new DivideAndConquerSolveStage<QuicksortProblem, QuicksortSolution>();
		DivideAndConquerCombineStage<QuicksortProblem, QuicksortSolution> combine = new DivideAndConquerCombineStage<QuicksortProblem, QuicksortSolution>();
		CollectorSink<QuicksortSolution> collectorSink = new CollectorSink<QuicksortSolution>(results);
		divide.declareActive();
		solveOne.declareActive();
		solveTwo.declareActive();
		combine.declareActive();

		// connect ports
		connectPorts(initialElementProducer.getOutputPort(), divide.getInputPort());
		connectPorts(divide.getFirstOutputPort(), solveOne.getInputPort());
		connectPorts(divide.getSecondOutputPort(), solveTwo.getInputPort());
		connectPorts(solveOne.getOutputPort(), combine.getFirstInputPort());
		connectPorts(solveTwo.getOutputPort(), combine.getSecondInputPort());
		connectPorts(combine.getOutputPort(), collectorSink.getInputPort());
	}
}
