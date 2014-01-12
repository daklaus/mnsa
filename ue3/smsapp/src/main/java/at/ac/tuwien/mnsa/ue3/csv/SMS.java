package at.ac.tuwien.mnsa.ue3.csv;

public class SMS {

	private String recipient;
	private String message;

	public SMS(String recipient, String message) {
		this.recipient = recipient;
		this.message = message;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}