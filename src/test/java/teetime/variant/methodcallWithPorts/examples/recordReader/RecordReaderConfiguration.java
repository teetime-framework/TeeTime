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

import teetime.variant.methodcallWithPorts.framework.core.Configuration;
import teetime.variant.methodcallWithPorts.framework.core.HeadPipeline;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.PipeOrdering;
import teetime.variant.methodcallWithPorts.framework.core.pipe.PipeFactory.ThreadCommunication;
import teetime.variant.methodcallWithPorts.stage.CollectorSink;
import teetime.variant.methodcallWithPorts.stage.InitialElementProducer;
import teetime.variant.methodcallWithPorts.stage.kieker.Dir2RecordsFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;

import kieker.common.record.IMonitoringRecord;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class RecordReaderConfiguration extends Configuration {

	private final List<IMonitoringRecord> elementCollection = new LinkedList<IMonitoringRecord>();
	private final PipeFactory pipeFactory = PipeFactory.INSTANCE;

	public void buildConfiguration() {
		HeadPipeline<?, ?> producerPipeline = this.buildProducerPipeline();
		this.getFiniteProducerStages().add(producerPipeline);
	}

	private HeadPipeline<?, ?> buildProducerPipeline() {
		ClassNameRegistryRepository classNameRegistryRepository = new ClassNameRegistryRepository();
		File logDir = new File("src/test/data/bookstore-logs");
		// create stages
		InitialElementProducer<File> initialElementProducer = new InitialElementProducer<File>(logDir);
		Dir2RecordsFilter dir2RecordsFilter = new Dir2RecordsFilter(classNameRegistryRepository);
		CollectorSink<IMonitoringRecord> collector = new CollectorSink<IMonitoringRecord>(this.elementCollection);

		final HeadPipeline<InitialElementProducer<File>, CollectorSink<IMonitoringRecord>> pipeline = new HeadPipeline<InitialElementProducer<File>, CollectorSink<IMonitoringRecord>>();
		pipeline.setFirstStage(initialElementProducer);
		pipeline.setLastStage(collector);

		this.pipeFactory.create(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false, 1)
				.connectPorts(initialElementProducer.getOutputPort(), dir2RecordsFilter.getInputPort());

		this.pipeFactory.create(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false, 1)
				.connectPorts(dir2RecordsFilter.getOutputPort(), collector.getInputPort());

		return pipeline;
	}

	public List<IMonitoringRecord> getElementCollection() {
		return this.elementCollection;
	}

}
