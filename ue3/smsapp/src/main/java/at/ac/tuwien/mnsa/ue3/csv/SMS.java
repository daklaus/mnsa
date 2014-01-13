package at.ac.tuwien.mnsa.ue3.csv;

public class SMS {

	private final String recipient;
	private final String message;

	public SMS(String recipient, String message) {
		if (recipient == null || message == null)
			throw new IllegalArgumentException("recipient or message is null");
		if (message.trim().isEmpty())
			throw new IllegalArgumentException("The message is empty");
		recipient = recipient.trim();
		if (!recipient.matches("^\\+\\d{3,}"))
			throw new IllegalArgumentException(
					"The recipient is not in international format (e.g. +436641234567)");

		this.recipient = recipient;
		this.message = message;
	}

	public String getRecipient() {
		return recipient;
	}

	public String getMessage() {
		return message;
	}
}