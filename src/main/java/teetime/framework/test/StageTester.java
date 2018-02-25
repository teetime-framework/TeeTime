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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import teetime.framework.AbstractStage;
import teetime.framework.StageState;

/**
 * This class can be used to test single stages in JUnit test cases.
 *
 * @author Nils Christian Ehmke
 */
public class StageTester extends MinimalStageTestSetup {

	private final List<InputHolder<?>> inputHolders = new ArrayList<InputHolder<?>>();
	private final List<OutputHolder<?>> outputHolders = new ArrayList<OutputHolder<?>>();
	private final AbstractStage stage;

	private StageTester(final AbstractStage stage) {
		this.stage = stage;
	}

	public static StageTester test(final AbstractStage stage) { // NOPMD
		if (stage.getCurrentState() != StageState.CREATED) {
			throw new InvalidTestCaseSetupException("This stage has already been tested in this test method. Move this test into a new test method.");
		}
		return new StageTester(stage);
	}

	/**
	 * @param elements
	 *            which serve as input. If nothing should be sent, pass
	 *
	 *            <pre>
	 * Collections.&lt;your type&gt;emptyList().
	 *            </pre>
	 */
	@Override
	public <I> InputHolder<I> send(final Collection<I> elements) {
		final InputHolder<I> inputHolder = new InputHolder<I>(this, stage, elements);
		this.inputHolders.add(inputHolder);
		return inputHolder;
	}

	/**
	 * @param actualElements
	 *            which should be tested against the expected elements.
	 */
	@Override
	public <O> OutputHolder<O> receive(final List<O> actualElements) {
		final OutputHolder<O> outputHolder = new OutputHolder<O>(this, actualElements);
		this.outputHolders.add(outputHolder);
		return outputHolder;
	}

	/**
	 * Does nothing. Can be used to make the test more readable.
	 */
	public StageTester and() {
		return this;
	}

	/* default */ List<InputHolder<?>> getInputHolders() {
		return inputHolders;
	}

	/* default */ List<OutputHolder<?>> getOutputHolders() {
		return outputHolders;
	}

	/* default */ AbstractStage getStage() {
		return stage;
	}

}
