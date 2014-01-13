package at.ac.tuwien.mnsa.ue3.smsapp.sms;

public class SmsDataPart {

	private final Sms parentSms;

	// TODO implement

	public SmsDataPart(Sms parentSms) {
		if (parentSms == null)
			throw new IllegalArgumentException("parentSms is null");

		this.parentSms = parentSms;
	}

	public Sms getParentSms() {
		return parentSms;
	}

	public int getMsgByteLengthWithoutSmscPart() {
		// TODO implement
		return 0;
	}

	public byte[] getPdu() {
		// TODO implement
		return null;
	}
}
