package at.ac.tuwien.mnsa.ue3.smsapp.csv;

import java.io.IOException;
import java.util.List;

import at.ac.tuwien.mnsa.ue3.smsapp.sms.Sms;

public interface CsvService {

	List<Sms> getSMSList() throws IOException;

}