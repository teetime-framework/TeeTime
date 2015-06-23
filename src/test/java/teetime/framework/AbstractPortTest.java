package teetime.framework;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AbstractPortTest {

	@Test
	public void testNameing() {
		NameingTestStage stage = new NameingTestStage();
		assertThat(stage.namedPort.toString(), is("Testname"));
	}

	private class NameingTestStage extends AbstractConsumerStage<Object> {

		public OutputPort<Object> namedPort = createOutputPort("Testname");

		@Override
		protected void execute(final Object element) {
			// TODO Auto-generated method stub

		}

	}

}
