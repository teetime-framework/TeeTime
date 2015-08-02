package teetime.stage.taskfarm.monitoring;

import java.util.List;

public interface IMonitoringService<K, V extends IMonitoringData> {

	public void addMonitoredItem(final K item);

	public List<?> getData();

	public void addMonitoringData();

}
