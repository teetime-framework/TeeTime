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
package teetime.examples.experiment19;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teetime.framework.AnalysisConfiguration;
import teetime.framework.OldHeadPipeline;
import teetime.framework.pipe.OrderedGrowableArrayPipe;
import teetime.framework.pipe.SpScPipe;
import teetime.stage.CollectorSink;
import teetime.stage.NoopFilter;
import teetime.stage.ObjectProducer;
import teetime.stage.Relay;
import teetime.stage.StartTimestampFilter;
import teetime.stage.StopTimestampFilter;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.io.EveryXthPrinter;
import teetime.util.ConstructorClosure;
import teetime.util.TimestampObject;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class MethodCallThroughputAnalysis19 extends AnalysisConfiguration {

	private static final int SPSC_INITIAL_CAPACITY = 100100;
	private static final int NUM_WORKER_THREADS = Runtime.getRuntime().availableProcessors();

	private final List<List<TimestampObject>> timestampObjectsList = new LinkedList<List<TimestampObject>>();

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private final int numNoopFilters;
	private int numWorkerThreads;

	public MethodCallThroughputAnalysis19(final int numWorkerThreads, final int numNoopFilters) {
		this.numWorkerThreads = numWorkerThreads;
		this.numNoopFilters = numNoopFilters;
	}

	public void build() {
		OldHeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> producerPipeline = this.buildProducerPipeline(this.numInputObjects,
				this.inputObjectCreator);
		addThreadableStage(producerPipeline);

		this.numWorkerThreads = Math.min(NUM_WORKER_THREADS, this.numWorkerThreads);

		for (int i = 0; i < numWorkerThreads; i++) {
			List<TimestampObject> resultList = new ArrayList<TimestampObject>(this.numInputObjects);
			this.timestampObjectsList.add(resultList);

			OldHeadPipeline<?, ?> pipeline = this.buildPipeline(producerPipeline.getLastStage(), resultList);
			addThreadableStage(pipeline);
		}
	}

	private OldHeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> buildProducerPipeline(final int numInputObjects,
			final ConstructorClosure<TimestampObject> inputObjectCreator) {
		final ObjectProducer<TimestampObject> objectProducer = new ObjectProducer<TimestampObject>(numInputObjects, inputObjectCreator);
		Distributor<TimestampObject> distributor = new Distributor<TimestampObject>();

		final OldHeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>> pipeline = new OldHeadPipeline<ObjectProducer<TimestampObject>, Distributor<TimestampObject>>();
		pipeline.setFirstStage(objectProducer);
		pipeline.setLastStage(distributor);

		OrderedGrowableArrayPipe.connect(objectProducer.getOutputPort(), distributor.getInputPort());

		return pipeline;
	}

	private OldHeadPipeline<?, ?> buildPipeline(final Distributor<TimestampObject> previousStage, final List<TimestampObject> timestampObjects) {
		Relay<TimestampObject> relay = new Relay<TimestampObject>();
		@SuppressWarnings("unchecked")
		final NoopFilter<TimestampObject>[] noopFilters = new NoopFilter[this.numNoopFilters];
		// create stages
		final StartTimestampFilter startTimestampFilter = new StartTimestampFilter();
		for (int i = 0; i < noopFilters.length; i++) {
			noopFilters[i] = new NoopFilter<TimestampObject>();
		}
		final StopTimestampFilter stopTimestampFilter = new StopTimestampFilter();
		EveryXthPrinter<TimestampObject> everyXthPrinter = new EveryXthPrinter<TimestampObject>(100000);
		final CollectorSink<TimestampObject> collectorSink = new CollectorSink<TimestampObject>(timestampObjects);

		final OldHeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>> pipeline = new OldHeadPipeline<Relay<TimestampObject>, CollectorSink<TimestampObject>>();
		pipeline.setFirstStage(relay);
		pipeline.setLastStage(collectorSink);

		SpScPipe.connect(previousStage.getNewOutputPort(), relay.getInputPort(), SPSC_INITIAL_CAPACITY);

		OrderedGrowableArrayPipe.connect(relay.getOutputPort(), startTimestampFilter.getInputPort());

		OrderedGrowableArrayPipe.connect(startTimestampFilter.getOutputPort(), noopFilters[0].getInputPort());
		for (int i = 0; i < noopFilters.length - 1; i++) {
			OrderedGrowableArrayPipe.connect(noopFilters[i].getOutputPort(), noopFilters[i + 1].getInputPort());
		}
		OrderedGrowableArrayPipe.connect(noopFilters[noopFilters.length - 1].getOutputPort(), stopTimestampFilter.getInputPort());
		OrderedGrowableArrayPipe.connect(stopTimestampFilter.getOutputPort(), everyXthPrinter.getInputPort());
		OrderedGrowableArrayPipe.connect(everyXthPrinter.getNewOutputPort(), collectorSink.getInputPort());

		return pipeline;
	}

	public void setInput(final int numInputObjects, final ConstructorClosure<TimestampObject> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	public int getNumNoopFilters() {
		return this.numNoopFilters;
	}

	public List<List<TimestampObject>> getTimestampObjectsList() {
		return this.timestampObjectsList;
	}

	public int getNumWorkerThreads() {
		return this.numWorkerThreads;
	}

}
