package teetime.variant.methodcallWithPorts.stage.kieker;

import java.io.File;

import teetime.variant.methodcallWithPorts.framework.core.InputPort;
import teetime.variant.methodcallWithPorts.framework.core.OutputPort;
import teetime.variant.methodcallWithPorts.framework.core.Pipeline;
import teetime.variant.methodcallWithPorts.stage.io.Directory2FilesFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryCreationFilter;
import teetime.variant.methodcallWithPorts.stage.kieker.className.ClassNameRegistryRepository;
import teetime.variant.methodcallWithPorts.stage.kieker.fileToRecord.DatFile2RecordFilter;

import kieker.common.record.IMonitoringRecord;

public class DirWithDat2RecordFilter extends Pipeline<ClassNameRegistryCreationFilter, DatFile2RecordFilter> {

	private ClassNameRegistryRepository classNameRegistryRepository;

	public DirWithDat2RecordFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
		this.classNameRegistryRepository = classNameRegistryRepository;

		final ClassNameRegistryCreationFilter classNameRegistryCreationFilter = new ClassNameRegistryCreationFilter(classNameRegistryRepository);
		final Directory2FilesFilter directory2FilesFilter = new Directory2FilesFilter();
		final DatFile2RecordFilter datFile2RecordFilter = new DatFile2RecordFilter(classNameRegistryRepository);

		this.setFirstStage(classNameRegistryCreationFilter);
		this.setLastStage(datFile2RecordFilter);
	}

	public DirWithDat2RecordFilter() {
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
