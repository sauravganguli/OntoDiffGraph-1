package application.ui;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaAppender extends WriterAppender{

	private final TextArea textArea;

	public TextAreaAppender(TextArea textArea){
		this.textArea = textArea;
		setLayout(new PatternLayout("%-5p - %m%n"));
	}

	@Override
	public void append(final LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);

		Platform.runLater(() -> textArea.appendText(message));
	}
}
