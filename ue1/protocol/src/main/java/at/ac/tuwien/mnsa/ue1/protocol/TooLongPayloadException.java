package at.ac.tuwien.mnsa.ue1.protocol;

public class TooLongPayloadException extends Exception {
	private static final long serialVersionUID = 1L;

	public TooLongPayloadException(int length) {
		super(
				"The length of the payload ("
						+ length
						+ ") has exceeded the maximum length for a serial packet which is "
						+ SerialPacket.MAX_LENGTH);
	}

}
