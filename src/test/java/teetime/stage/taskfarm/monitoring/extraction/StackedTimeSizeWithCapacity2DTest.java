package teetime.stage.taskfarm.monitoring.extraction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import teetime.stage.taskfarm.monitoring.PipeMonitoringService;

public class StackedTimeSizeWithCapacity2DTest {

	@Test
	public void testWorkingService() {
		PipeMonitoringService service = ExtractorTestHelper.generate4PipeMonitoringServiceWithBehavior();

		AbstractMonitoringDataExtraction extraction = new StackedTimeSizeWithCapacity2D(service, null);
		String result = extraction.extractToString();

		List<String> outputValues = new LinkedList<String>();
		outputValues.add("time,capacity,size0,size1,size2,size3");
		// as the exact processing time is slightly nondeterministic, we only check for the other values
		outputValues.add(",1000,2,0,0,0");
		outputValues.add(",1000,4,0,0,0");
		outputValues.add(",1000,8,2,0,0");
		outputValues.add(",1000,16,0,2,0");
		outputValues.add(",1000,32,0,4,0");
		outputValues.add(",1000,64,0,0,0");
		outputValues.add(",1000,128,0,0,2");
		assertThat(result, stringContainsInOrder(outputValues));
	}

	@Test
	public void testEmptyService() {
		PipeMonitoringService service = ExtractorTestHelper.generateEmpty5PipeMonitoringService();

		AbstractMonitoringDataExtraction extraction = new StackedTimeSizeWithCapacity2D(service, null);
		String result = extraction.extractToString();

		String header = "time,capacity,size0,size1,size2,size3,size4"
				+ System.getProperty("line.separator");
		assertThat(result, is(equalTo(header)));
	}
}
