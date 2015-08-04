package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class TimeBoundaryMPushThroughput3DTest {

	@Test
	public void testWorkingService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateSingleTaskFarmMonitoringServiceWithBehavior();

		AbstractMonitoringDataExtraction extraction = new TimeBoundaryMPushThroughput3D(null, service);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,boundary,mpushthroughput");
		outputValues.add(",0.4,4.0");
		outputValues.add(",0.4,7.0");
		outputValues.add(",0.4,10.0");
		outputValues.add(",0.4," + (13.0 + 4.0) / 2.0);
		outputValues.add(",0.4," + (16.0 + 7.0) / 2.0);
		outputValues.add(",0.4," + (19.0 + 10.0) / 2.0);
		outputValues.add(",0.4," + (22.0 + 13.0 + 4.0) / 3.0);
		outputValues.add(",0.4," + (25.0 + 16.0 + 7.0) / 3.0);
		outputValues.add(",0.4," + (28.0 + 19.0 + 10.0) / 3.0);
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateEmptySingleTaskFarmMonitoringService();

		AbstractMonitoringDataExtraction extraction = new TimeBoundaryMPushThroughput3D(null, service);
		String result = extraction.extractToString();

		String header = "time,boundary,mpushthroughput"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
