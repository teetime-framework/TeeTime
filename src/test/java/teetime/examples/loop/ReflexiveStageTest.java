package teetime.examples.loop;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static teetime.framework.test.StageTester.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import teetime.stage.StatelessCounter;

public class ReflexiveStageTest {

	private StatelessCounter<Integer> reflexiveStage;

	@Before
	public void before() {
		reflexiveStage = new StatelessCounter<Integer>();
	}

	@Test(timeout = 200)
	@Ignore // remove ignore if loop detection is merged into this master branch
	public void reflexiveStageShouldExecute() throws Exception {
		final List<Integer> INPUT_ELEMENTS = Arrays.asList(1, 2, 3, 4, 5);
		final List<Integer> EXPECTED_OUTPUT_ELEMENTS = new ArrayList<Integer>(INPUT_ELEMENTS);

		List<Integer> outputElements = new ArrayList<Integer>();

		test(reflexiveStage).and()
				.send(INPUT_ELEMENTS).to(reflexiveStage.getInputPort()).and()
				.receive(outputElements).from(reflexiveStage.getOutputPort()).and()
				.start();

		assertThat(outputElements, is(EXPECTED_OUTPUT_ELEMENTS));
	}
}
