package teetime.examples.quicksort;

import teetime.framework.Configuration;
import teetime.stage.QuicksortStage;

public class QuicksortConfiguration extends Configuration {

	public QuicksortConfiguration(final int[] arr) {

		final ArrayToQuicksortProblem ltqsp = new ArrayToQuicksortProblem(arr);
		final QuicksortStage qsort = new QuicksortStage(this.getDynamicContext());
		final QuicksortProblemToList qsptl = new QuicksortProblemToList();

		connectPorts(ltqsp.getOutputPort(), qsort.getInputPort());
		connectPorts(qsort.getOutputPort(), qsptl.getInputPort());

	}

}
