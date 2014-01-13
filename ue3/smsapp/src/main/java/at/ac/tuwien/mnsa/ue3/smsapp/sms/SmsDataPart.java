package at.ac.tuwien.mnsa.ue3.smsapp.sms;

import at.ac.tuwien.common.binary.NumberConverter;

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
		// TODO stub
		return 21;
	}

	public byte[] getPdu() {
		// TODO stub
		return NumberConverter
				.hexToBytes("0011000C913466641361980000A708D4F29C0E8212AB");
	}
}
