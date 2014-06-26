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

import teetime.variant.explicitScheduling.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.RunnableStage;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SingleElementPipe;
import teetime.variant.methodcallWithPorts.framework.core.pipe.SpScPipe;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.kieker.Dir2RecordsFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;

import kieker.common.record.IMonitoringRecord;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class RecordReaderAnalysis extends Analysis {

	private final List<IMonitoringRecord> elementCollection = new LinkedList<IMonitoringRecord>();

	private Thread producerThread;

	private ClassNameRegistryRepository classNameRegistryRepository;

	@Override
	public void init() {
		super.init();
		Pipeline<File, Object> producerPipeline = this.buildProducerPipeline();
		this.producerThread = new Thread(new RunnableStage(producerPipeline));
	}

	private Pipeline<File, Object> buildProducerPipeline() {
		this.classNameRegistryRepository = new ClassNameRegistryRepository();
		// create stages
		Dir2RecordsFilter file2RecordFilter = new Dir2RecordsFilter(this.classNameRegistryRepository);
		CollectorSink<IMonitoringRecord> collector = new CollectorSink<IMonitoringRecord>(this.elementCollection);

		final Pipeline<File, Object> pipeline = new Pipeline<File, Object>();
		pipeline.setFirstStage(file2RecordFilter);
		pipeline.setLastStage(collector);

		SingleElementPipe.connect(file2RecordFilter.getOutputPort(), collector.getInputPort());

		SpScPipe.connect(null, file2RecordFilter.getInputPort(), 1);
		file2RecordFilter.getInputPort().getPipe().add(new File("src/test/data/bookstore-logs"));

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

	public List<IMonitoringRecord> getElementCollection() {
		return this.elementCollection;
	}

}
