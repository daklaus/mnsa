package at.ac.tuwien.mnsa.ue1.properties;

public abstract class PropertiesServiceFactory {

	public static PropertiesService getPropertiesService() {
		return USBConnectionPropertiesService.getInstance();
	}
}
