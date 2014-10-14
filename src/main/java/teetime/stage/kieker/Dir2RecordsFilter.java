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
package teetime.stage.kieker;

import java.io.File;

import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.OldPipeline;
import teetime.framework.pipe.PipeFactory;
import teetime.framework.pipe.SingleElementPipe;
import teetime.framework.pipe.PipeFactory.PipeOrdering;
import teetime.framework.pipe.PipeFactory.ThreadCommunication;
import teetime.stage.FileExtensionSwitch;
import teetime.stage.basic.merger.Merger;
import teetime.stage.io.Directory2FilesFilter;
import teetime.stage.kieker.className.ClassNameRegistryCreationFilter;
import teetime.stage.kieker.className.ClassNameRegistryRepository;
import teetime.stage.kieker.fileToRecord.BinaryFile2RecordFilter;
import teetime.stage.kieker.fileToRecord.DatFile2RecordFilter;
import teetime.stage.kieker.fileToRecord.ZipFile2RecordFilter;

import kieker.common.record.IMonitoringRecord;
import kieker.common.util.filesystem.BinaryCompressionMethod;
import kieker.common.util.filesystem.FSUtil;

/**
 * @author Christian Wulf
 *
 * @since 1.10
 */
public class Dir2RecordsFilter extends OldPipeline<ClassNameRegistryCreationFilter, Merger<IMonitoringRecord>> {

	private final PipeFactory pipeFactory = PipeFactory.INSTANCE;
	private ClassNameRegistryRepository classNameRegistryRepository;

	/**
	 * @since 1.10
	 */
	public Dir2RecordsFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
		this.classNameRegistryRepository = classNameRegistryRepository;

		// FIXME does not yet work with more than one thread due to classNameRegistryRepository: classNameRegistryRepository is set after the ctor
		// create stages
		final ClassNameRegistryCreationFilter classNameRegistryCreationFilter = new ClassNameRegistryCreationFilter(this.classNameRegistryRepository);
		final Directory2FilesFilter directory2FilesFilter = new Directory2FilesFilter();

		final FileExtensionSwitch fileExtensionSwitch = new FileExtensionSwitch();

		final DatFile2RecordFilter datFile2RecordFilter = new DatFile2RecordFilter(this.classNameRegistryRepository);
		final BinaryFile2RecordFilter binaryFile2RecordFilter = new BinaryFile2RecordFilter(this.classNameRegistryRepository);
		final ZipFile2RecordFilter zipFile2RecordFilter = new ZipFile2RecordFilter();

		final Merger<IMonitoringRecord> recordMerger = new Merger<IMonitoringRecord>();

		// store ports due to readability reasons
		final OutputPort<File> normalFileOutputPort = fileExtensionSwitch.addFileExtension(FSUtil.NORMAL_FILE_EXTENSION);
		final OutputPort<File> binFileOutputPort = fileExtensionSwitch.addFileExtension(BinaryCompressionMethod.NONE.getFileExtension());
		final OutputPort<File> zipFileOutputPort = fileExtensionSwitch.addFileExtension(FSUtil.ZIP_FILE_EXTENSION);

		// connect ports by pipes
		this.pipeFactory.create(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false, 1)
		.connectPorts(classNameRegistryCreationFilter.getOutputPort(), directory2FilesFilter.getInputPort());

		this.pipeFactory.create(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false, 1)
		.connectPorts(directory2FilesFilter.getOutputPort(), fileExtensionSwitch.getInputPort());

		SingleElementPipe.connect(normalFileOutputPort, datFile2RecordFilter.getInputPort());
		SingleElementPipe.connect(binFileOutputPort, binaryFile2RecordFilter.getInputPort());
		SingleElementPipe.connect(zipFileOutputPort, zipFile2RecordFilter.getInputPort());

		SingleElementPipe.connect(datFile2RecordFilter.getOutputPort(), recordMerger.getNewInputPort());
		SingleElementPipe.connect(binaryFile2RecordFilter.getOutputPort(), recordMerger.getNewInputPort());
		SingleElementPipe.connect(zipFile2RecordFilter.getOutputPort(), recordMerger.getNewInputPort());

		// prepare pipeline
		this.setFirstStage(classNameRegistryCreationFilter);
		this.setLastStage(recordMerger);
	}

	/**
	 * @since 1.10
	 */
	public Dir2RecordsFilter() {
		this(null);
	}

	public ClassNameRegistryRepository getClassNameRegistryRepository() {
		return this.classNameRegistryRepository;
	}

	public void setClassNameRegistryRepository(final ClassNameRegistryRepository classNameRegistryRepository) {
		this.classNameRegistryRepository = classNameRegistryRepository;
	}

	public InputPort<File> getInputPort() {
		return this.getFirstStage().getInputPort();
	}

	public OutputPort<IMonitoringRecord> getOutputPort() {
		return this.getLastStage().getOutputPort();
	}

}
