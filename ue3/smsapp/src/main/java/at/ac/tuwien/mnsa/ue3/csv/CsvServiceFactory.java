package at.ac.tuwien.mnsa.ue3.csv;

public abstract class CsvServiceFactory {

	public static CsvService getCsvService() {
		return SMSCsvService.getInstance();
	}
}