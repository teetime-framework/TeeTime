package teetime.stage.io;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import kieker.common.exception.MonitoringRecordException;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.ILookup;

public final class RecordFactory {

	private final Map<String, IRecordFactoryMethod> recordFactoryMethods = new HashMap<String, IRecordFactoryMethod>();

	public IMonitoringRecord create(final int clazzId, final ByteBuffer buffer, final ILookup<String> stringRegistry) throws MonitoringRecordException {
		String recordClassName = stringRegistry.get(clazzId);
		IRecordFactoryMethod recordFactoryMethod = this.recordFactoryMethods.get(recordClassName);
		if (recordFactoryMethod == null) {
			throw new IllegalStateException("recordClassName: " + recordClassName);
		}
		return recordFactoryMethod.create(buffer, stringRegistry);
	}

	public void register(final String recordClassName, final IRecordFactoryMethod recordFactoryMethod) {
		this.recordFactoryMethods.put(recordClassName, recordFactoryMethod);
	}
}
