package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class TimeBoundaryStages3DTest {

	@Test
	public void testWorkingService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateSingleTaskFarmMonitoringServiceWithBehavior();

		AbstractMonitoringDataExtraction extraction = new TimeBoundaryStages3D(null, service);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,boundary,stages");
		outputValues.add(",0.4,1");
		outputValues.add(",0.4,1");
		outputValues.add(",0.4,1");
		outputValues.add(",0.4,2");
		outputValues.add(",0.4,2");
		outputValues.add(",0.4,2");
		outputValues.add(",0.4,3");
		outputValues.add(",0.4,3");
		outputValues.add(",0.4,3");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateEmptySingleTaskFarmMonitoringService();

		AbstractMonitoringDataExtraction extraction = new TimeBoundaryStages3D(null, service);
		String result = extraction.extractToString();

		String header = "time,boundary,stages"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
