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

import teetime.variant.methodcallWithPorts.framework.core.Analysis;
import teetime.variant.methodcallWithPorts.framework.core.Configuration;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.framework.core.StageWithPort;
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

	private ClassNameRegistryRepository classNameRegistryRepository;

	@Override
	public void init() {
		Configuration configuration = this.buildConfiguration();
		this.setConfiguration(configuration);

		super.init();
	}

	private Configuration buildConfiguration() {
		Configuration localConfiguration = new Configuration();

		StageWithPort producerPipeline = this.buildProducerPipeline();
		localConfiguration.getFiniteProducerStages().add(producerPipeline);

		return localConfiguration;
	}

	private StageWithPort buildProducerPipeline() {
		this.classNameRegistryRepository = new ClassNameRegistryRepository();
		// create stages
		Dir2RecordsFilter dir2RecordsFilter = new Dir2RecordsFilter(this.classNameRegistryRepository);
		CollectorSink<IMonitoringRecord> collector = new CollectorSink<IMonitoringRecord>(this.elementCollection);

		final Pipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>> pipeline = new Pipeline<Dir2RecordsFilter, CollectorSink<IMonitoringRecord>>();
		pipeline.setFirstStage(dir2RecordsFilter);
		pipeline.setLastStage(collector);

		dir2RecordsFilter.getInputPort().setPipe(new SpScPipe<File>(1));
		SingleElementPipe.connect(dir2RecordsFilter.getOutputPort(), collector.getInputPort());

		dir2RecordsFilter.getInputPort().getPipe().add(new File("src/test/data/bookstore-logs"));

		return pipeline;
	}

	public List<IMonitoringRecord> getElementCollection() {
		return this.elementCollection;
	}

}
