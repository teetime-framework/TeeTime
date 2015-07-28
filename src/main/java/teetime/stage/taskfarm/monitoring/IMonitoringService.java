package teetime.stage.taskfarm.monitoring;

import java.util.List;
import java.util.Map;

public interface IMonitoringService<K, V extends IMonitoringData> {

	public void addMonitoredItem(final K item);

	public Map<K, List<V>> getData();

	public void addMonitoringData();

}
