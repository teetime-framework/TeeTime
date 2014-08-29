package teetime.variant.methodcallWithPorts.stage.kieker;

import java.io.File;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.stage.io.Directory2FilesFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryCreationFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;
import teetime.variant.methodcallWithPorts.stage.kieker.fileToRecord.BinaryFile2RecordFilter;

import kieker.common.record.IMonitoringRecord;

public class DirWithBin2RecordFilter extends Pipeline<ClassNameRegistryCreationFilter, BinaryFile2RecordFilter> {

	private ClassNameRegistryRepository classNameRegistryRepository;

	public DirWithBin2RecordFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
		this.classNameRegistryRepository = classNameRegistryRepository;

		final ClassNameRegistryCreationFilter classNameRegistryCreationFilter = new ClassNameRegistryCreationFilter(classNameRegistryRepository);
		final Directory2FilesFilter directory2FilesFilter = new Directory2FilesFilter();
		final BinaryFile2RecordFilter binaryFile2RecordFilter = new BinaryFile2RecordFilter(classNameRegistryRepository);

		this.setFirstStage(classNameRegistryCreationFilter);
		this.setLastStage(binaryFile2RecordFilter);
	}

	public DirWithBin2RecordFilter() {
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
