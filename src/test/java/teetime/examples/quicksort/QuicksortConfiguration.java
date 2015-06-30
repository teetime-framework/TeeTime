package teetime.examples.quicksort;

import java.util.List;

import teetime.framework.Configuration;
import teetime.stage.ElementsToList;
import teetime.stage.QuicksortStage;
import teetime.stage.RandomIntegerGenerator;
import teetime.stage.io.Printer;

public class QuicksortConfiguration extends Configuration {

	public QuicksortConfiguration(final int upperBound, final int size, final String mode) {

		final RandomIntegerGenerator rig = new RandomIntegerGenerator(upperBound);
		final ElementsToList<Integer> itl = new ElementsToList<Integer>(size);
		final QuicksortStage qsort = new QuicksortStage(this.getContext());
		final Printer<List<Integer>> printer = new Printer<List<Integer>>();
		printer.setStreamName(mode);

		connectPorts(rig.getOutputPort(), itl.getInputPort());
		connectPorts(itl.getOutputPort(), qsort.getInputPort());
		connectPorts(qsort.getOutputPort(), printer.getInputPort());
	}

}
