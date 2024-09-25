package ithaic.imitate_os;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class RedirectOutput extends OutputStream {
    private final TextArea textArea;

    public RedirectOutput(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
    }
}
