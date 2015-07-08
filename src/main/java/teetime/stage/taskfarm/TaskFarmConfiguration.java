package teetime.stage.taskfarm;

/**
 * This class contains the configuration of a single Task Farm.
 *
 * @author Christian Claus Wiechmann
 *
 * @param <I>
 *            Input type of Task Farm
 * @param <O>
 *            Output type of Task Farm
 * @param <T>
 *            Type of enclosed stage
 */
public class TaskFarmConfiguration<I, O, T extends ITaskFarmDuplicable<I, O>> {

	private int analysisWindow = 3;

	TaskFarmConfiguration() {}

	public int getAnalysisWindow() {
		return this.analysisWindow;
	}

	public void setAnalysisWindow(final int analysisWindow) {
		this.analysisWindow = analysisWindow;
	}
}
