package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.SingleTaskFarmMonitoringService;

public class TimeBoundaryMPullThroughput3DTest {

	@Test
	public void testWorkingService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateSingleTaskFarmMonitoringServiceWithBehavior();

		AbstractMonitoringDataExtraction extraction = new TimeBoundaryMPullThroughput3D(null, service);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,boundary,mpullthroughput");
		outputValues.add(",0.4,6.0");
		outputValues.add(",0.4,11.0");
		outputValues.add(",0.4,16.0");
		outputValues.add(",0.4," + (21.0 + 6.0) / 2.0);
		outputValues.add(",0.4," + (26.0 + 11.0) / 2.0);
		outputValues.add(",0.4," + (31.0 + 16.0) / 2.0);
		outputValues.add(",0.4," + (36.0 + 21.0 + 6.0) / 3.0);
		outputValues.add(",0.4," + (41.0 + 26.0 + 11.0) / 3.0);
		outputValues.add(",0.4," + (46.0 + 31.0 + 16.0) / 3.0);
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		SingleTaskFarmMonitoringService service = ExtractorTestHelper.generateEmptySingleTaskFarmMonitoringService();

		AbstractMonitoringDataExtraction extraction = new TimeBoundaryMPullThroughput3D(null, service);
		String result = extraction.extractToString();

		String header = "time,boundary,mpullthroughput"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
