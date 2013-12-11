package at.ac.tuwien.mnsa.ue1.proxymidlet;

import javax.microedition.lcdui.TextBox;

class Tracer {
	private TextBox outputTextBox;

	public Tracer(TextBox outputTextBox) {
		this.outputTextBox = outputTextBox;
	}

	public void outln(String msg) {
		outputTextBox.setString((outputTextBox.getString()).concat(msg + "\n"));
	}

	public void out(String msg) {
		outputTextBox.setString((outputTextBox.getString()).concat(msg));
	}
}