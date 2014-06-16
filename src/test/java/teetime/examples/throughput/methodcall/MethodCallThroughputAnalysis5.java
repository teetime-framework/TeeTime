/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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
 ***************************************************************************/
package teetime.examples.throughput.methodcall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import teetime.examples.throughput.TimestampObject;
import teetime.examples.throughput.methodcall.stage.CollectorSink;
import teetime.examples.throughput.methodcall.stage.NoopFilter;
import teetime.examples.throughput.methodcall.stage.ObjectProducer;
import teetime.examples.throughput.methodcall.stage.StartTimestampFilter;
import teetime.examples.throughput.methodcall.stage.StopTimestampFilter;
import teetime.framework.core.Analysis;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class MethodCallThroughputAnalysis5 extends Analysis {

	public abstract class WrappingPipeline {

		public abstract boolean execute();

	}

	private long numInputObjects;
	private Callable<TimestampObject> inputObjectCreator;
	private int numNoopFilters;
	private List<TimestampObject> timestampObjects;
	private Runnable runnable;

	@Override
	public void init() {
		super.init();
		this.runnable = this.buildPipeline();
	}

	/**
	 * @param numNoopFilters
	 * @since 1.10
	 */
	private Runnable buildPipeline() {
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		// create stages
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(this.numInputObjects, this.inputObjectCreator);
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(this.timestampObjects);

		final List<Stage> stageList = new ArrayList<Stage>();
		stageList.add(objectProducer);
		stageList.add(startTimestampFilter);
		stageList.addAll(Arrays.asList(noopFilters));
		stageList.add(stopTimestampFilter);
		stageList.add(collectorSink);

		// using an array decreases the performance from 60ms to 440ms (by 7x)
		final Stage[] stages = stageList.toArray(new Stage[0]);
		for (int i = 0; i < stages.length - 1; i++) {
			stages[i].setSuccessor(stages[i + 1]);
		}
		final Stage startStage = stages[0];

		final WrappingPipeline pipeline = new WrappingPipeline() {
			@Override
			public boolean execute() {
				Object element = null;
				Stage stage = startStage;

				element = stage.execute(element);
				if (element == null) {
					return false;
				}
				stage = stage.next();

				do {
					element = stage.execute(element);
					stage = stage.next();
				} while (stage != null);
				return true;
			}

		};

		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				boolean success;
				do {
					success = pipeline.execute();
				} while (success);
			}
		};
		return runnable;
	}

	@Override
	public void start() {
		super.start();
		this.runnable.run();
	}

	public void setInput(final int numInputObjects, final Callable<TimestampObject> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	public int getNumNoopFilters() {
		return this.numNoopFilters;
	}

	public void setNumNoopFilters(final int numNoopFilters) {
		this.numNoopFilters = numNoopFilters;
	}

	public List<TimestampObject> getTimestampObjects() {
		return this.timestampObjects;
	}

	public void setTimestampObjects(final List<TimestampObject> timestampObjects) {
		this.timestampObjects = timestampObjects;
	}
}
