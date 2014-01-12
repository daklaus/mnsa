package at.ac.tuwien.mnsa.ue3.csv;

import java.io.IOException;
import java.util.List;

public interface CsvService {

	List<SMS> getSMSList() throws IOException;

}