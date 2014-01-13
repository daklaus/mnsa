package at.ac.tuwien.mnsa.ue3.smsapp.properties;

public abstract class PropertiesServiceFactory {

	public static PropertiesService getPropertiesService() {
		return SmsPropertiesService.getInstance();
	}
}
