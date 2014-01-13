package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import java.util.List;

public class SmsService {

	/**
	 * Assembles the a list of SMS parts (each containing max. 160 characters of
	 * the SMS) which have to be sent one by one.
	 * 
	 * @param sms
	 *            The SMS containing the recipient and the full text
	 * @return a list of parts in which the original SMS is split into
	 */
	public static List<SmsDataPart> getSmsDataParts(Sms sms) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO This service should also handle the alphabet conversions and PDU
	// creation and storage into the SmsDataParts

}
