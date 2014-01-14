package at.ac.tuwien.mnsa.ue3.smsapp.csv;

public abstract class CsvServiceFactory {

	public static CsvService getCsvService() {
		return SmsCsvService.getInstance();
	}
}