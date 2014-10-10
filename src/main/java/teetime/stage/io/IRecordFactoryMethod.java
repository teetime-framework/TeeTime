package teetime.stage.io;

import java.nio.ByteBuffer;

import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.ILookup;

public interface IRecordFactoryMethod {

	IMonitoringRecord create(ByteBuffer buffer, ILookup<String> stringRegistry);

}
