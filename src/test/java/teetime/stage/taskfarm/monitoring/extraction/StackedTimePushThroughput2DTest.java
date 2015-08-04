package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;

public class StackedTimePushThroughput2DTest {

	@Test
	public void testWorkingService() {
		PipeMonitoringService service = ExtractorTestHelper.generate4PipeMonitoringServiceWithBehavior();

		AbstractMonitoringDataExtraction extraction = new StackedTimePushThroughput2D(service, null);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,pushthroughput0,pushthroughput1,pushthroughput2,pushthroughput3");
		// as the exact processing time is slightly nondeterministic, we only check for the other values
		outputValues.add(",4,0,0,0");
		outputValues.add(",7,0,0,0");
		outputValues.add(",10,4,0,0");
		outputValues.add(",13,0,4,0");
		outputValues.add(",16,0,7,0");
		outputValues.add(",19,0,0,0");
		outputValues.add(",22,0,0,4");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		PipeMonitoringService service = ExtractorTestHelper.generateEmpty5PipeMonitoringService();

		AbstractMonitoringDataExtraction extraction = new StackedTimePushThroughput2D(service, null);
		String result = extraction.extractToString();

		String header = "time,pushthroughput0,pushthroughput1,pushthroughput2,pushthroughput3,pushthroughput4"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}

}
