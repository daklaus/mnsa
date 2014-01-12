package at.ac.tuwien.mnsa.ue3.properties;

public abstract class PropertiesServiceFactory {

	public static PropertiesService getPropertiesService() {
		return SMSPropertiesService.getInstance();
	}
}
