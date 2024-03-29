/**
 * Copyright © 2015 Christian Wulf, Nelson Tavares de Sousa (http://teetime-framework.github.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package teetime.framework.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;

import teetime.framework.AbstractStage;
import teetime.framework.CompositeStage;
import teetime.framework.Configuration;
import teetime.framework.Execution;
import teetime.framework.ExecutionException;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;

/**
 * This class can be used to test single stages in JUnit test cases.
 *
 * @author Christian Wulf, Nils Christian Ehmke
 */
public final class StageTester {

	private final StageUnderTest stageUnderTest;
	private final Map<InputPort<Object>, List<Object>> inputElementsByPort = new HashMap<>();
	private final Map<OutputPort<Object>, List<Object>> outputElementsByPort = new HashMap<>();

	private StageTester(final StageUnderTest stageUnderTest) {
		this.stageUnderTest = stageUnderTest;
	}

	/**
	 * Prepares to test the given stage.
	 *
	 * @param stage to be tested
	 * @return a stage test builder
	 */
	public static StageTester test(final AbstractStage stage) { // NOPMD not a junit test
		StageUnderTest stageUnderTest = new PrimitiveStageUnderTest(stage);

		return new StageTester(stageUnderTest);
	}

	/**
	 * Prepares to test the given composite stage.
	 *
	 * @param compositeStage to be tested
	 * @return a stage test builder
	 */
	public static StageTester test(final CompositeStage compositeStage) { // NOPMD not a junit test
		StageUnderTest stageUnderTest = new CompositeStageUnderTest(compositeStage);

		return new StageTester(stageUnderTest);
	}

	/**
	 * @param elements which serve as input. If nothing should be sent, pass
	 */
	@SafeVarargs
	public final <I> InputHolder<I> send(final I... elements) {
		return this.send(Arrays.asList(elements));
	}

	/**
	 * @param elements which serve as input. If nothing should be sent, pass
	 *
	 *                 <pre>
	 * Collections.&lt;your type&gt;emptyList().
	 *                 </pre>
	 */
	public <I> InputHolder<I> send(final List<I> elements) {
		return new InputHolder<I>(this, elements);
	}

	/**
	 * @param actualElements which should be tested against the expected elements.
	 *
	 * @deprecated since 3.0. Use the following code instead:
	 *
	 *             <pre>
	 * {@code
	 * import static StageTester.*;
	 * ...
	 * assertThat(stage.getOutputPort(), produces(1,2,3));
	 * }
	 *             </pre>
	 *
	 */
	@Deprecated
	public <O> OutputHolder<O> receive(final List<O> actualElements) {
		return new OutputHolder<O>(this, actualElements);
	}

	/**
	 * Does nothing. Can be used to make the test more readable.
	 */
	public StageTester and() {
		return this;
	}

	/**
	 * This method will start the test and block until it is finished.
	 *
	 * @throws ExecutionException if at least one exception in one thread has
	 *                            occurred within the analysis. The exception
	 *                            contains the pairs of thread and throwable.
	 *
	 */
	public void start() {
		final Configuration configuration = new TestConfiguration(this);
		final Execution<Configuration> analysis = new Execution<Configuration>(configuration);
		analysis.executeBlocking();
	}

	/* default */ StageUnderTest getStageUnderTest() {
		return stageUnderTest;
	}

	/* default */ Map<InputPort<Object>, List<Object>> getInputElementsByPort() {
		return inputElementsByPort;
	}

	/* default */ Map<OutputPort<Object>, List<Object>> getOutputElementsByPort() {
		return outputElementsByPort;
	}

	@SafeVarargs
	public static <T> Matcher<OutputPort<T>> produces(final T... values) {
		return new Produces<T, OutputPort<T>>(values);
	}

	public static <T> Matcher<OutputPort<T>> producesNothing() {
		return new Produces<T, OutputPort<T>>();
	}

}
