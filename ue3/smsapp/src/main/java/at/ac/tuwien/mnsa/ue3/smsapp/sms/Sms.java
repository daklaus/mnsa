package at.ac.tuwien.mnsa.ue3.smsapp.sms;

public class Sms {

	private final String recipient;
	private final String message;

	public Sms(String recipient, String message) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((recipient == null) ? 0 : recipient.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sms other = (Sms) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (recipient == null) {
			if (other.recipient != null)
				return false;
		} else if (!recipient.equals(other.recipient))
			return false;
		return true;
	}
}