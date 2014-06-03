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
package teetime.stage.kieker.fileToRecord;

import java.io.File;

import teetime.common.record.IMonitoringRecord;
import teetime.framework.concurrent.ConcurrentWorkStealingPipe;
import teetime.framework.concurrent.ConcurrentWorkStealingPipeFactory;
import teetime.framework.core.CompositeFilter;
import teetime.framework.core.IInputPort;
import teetime.framework.core.IOutputPort;
import teetime.stage.io.File2TextLinesFilter;
import teetime.stage.kieker.className.ClassNameRegistryRepository;
import teetime.stage.kieker.fileToRecord.textLine.TextLine2RecordFilter;
import teetime.stage.util.TextLine;

/**
 * @author Christian Wulf
 * 
 * @since 1.10
 */
public class DatFile2RecordFilter extends CompositeFilter {

	public final IInputPort<File2TextLinesFilter, File> fileInputPort;

	public final IOutputPort<TextLine2RecordFilter, IMonitoringRecord> recordOutputPort;

	public DatFile2RecordFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
		final File2TextLinesFilter file2TextLinesFilter = new File2TextLinesFilter();
		final TextLine2RecordFilter textLine2RecordFilter = new TextLine2RecordFilter(classNameRegistryRepository);

		// FIXME extract pipe implementation
		final ConcurrentWorkStealingPipeFactory<TextLine> concurrentWorkStealingPipeFactory = new ConcurrentWorkStealingPipeFactory<TextLine>();
		final ConcurrentWorkStealingPipe<TextLine> pipe = concurrentWorkStealingPipeFactory.create();
		pipe.connect(file2TextLinesFilter.textLineOutputPort, textLine2RecordFilter.textLineInputPort);

		this.fileInputPort = file2TextLinesFilter.fileInputPort;
		this.recordOutputPort = textLine2RecordFilter.recordOutputPort;

		this.schedulableStages.add(file2TextLinesFilter);
		this.schedulableStages.add(textLine2RecordFilter);
	}
}
