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
package teetime.variant.methodcallWithPorts.examples.recordReader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import teetime.util.ConstructorClosure;
import teetime.variant.explicitScheduling.examples.throughput.TimestampObject;
import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.kieker.File2RecordFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;

import kieker.common.record.IMonitoringRecord;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class RecordReaderAnalysis extends Analysis {

	private int numInputObjects;
	private ConstructorClosure<TimestampObject> inputObjectCreator;
	private int numNoopFilters;

	private final List<IMonitoringRecord> timestampObjectsList = new LinkedList<IMonitoringRecord>();

	private Thread producerThread;

	private ClassNameRegistryRepository classNameRegistryRepository;

	@Override
	public void init() {
		super.init();
		Pipeline<File, Object> producerPipeline = this.buildProducerPipeline(this.numInputObjects, this.inputObjectCreator);
		this.producerThread = new Thread(new RunnableStage(producerPipeline));
	}

	private Pipeline<File, Object> buildProducerPipeline(final int numInputObjects, final ConstructorClosure<TimestampObject> inputObjectCreator) {
		this.classNameRegistryRepository = new ClassNameRegistryRepository();
		// create stages
		File2RecordFilter file2RecordFilter = new File2RecordFilter(this.classNameRegistryRepository);
		CollectorSink<IMonitoringRecord> collector = new CollectorSink<IMonitoringRecord>(this.timestampObjectsList);

		final Pipeline<File, Object> pipeline = new Pipeline<File, Object>();
		pipeline.setFirstStage(file2RecordFilter);
		pipeline.setLastStage(collector);

		SingleElementPipe.connect(file2RecordFilter.getOutputPort(), collector.getInputPort());

		return pipeline;
	}

	@Override
	public void start() {
		super.start();

		this.producerThread.start();

		try {
			this.producerThread.join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void setInput(final int numInputObjects, final ConstructorClosure<TimestampObject> inputObjectCreator) {
		this.numInputObjects = numInputObjects;
		this.inputObjectCreator = inputObjectCreator;
	}

	public int getNumNoopFilters() {
		return this.numNoopFilters;
	}

	public void setNumNoopFilters(final int numNoopFilters) {
		this.numNoopFilters = numNoopFilters;
	}

	public List<IMonitoringRecord> getTimestampObjectsList() {
		return this.timestampObjectsList;
	}

}
