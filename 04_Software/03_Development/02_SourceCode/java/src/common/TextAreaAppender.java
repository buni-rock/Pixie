/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javax.swing.JTextArea;

/**
 * Implementation of an appender which is displaying the logging messages into a
 * text area.
 *
 * @author Olimpia Popica
 */
public class TextAreaAppender extends AppenderBase<ILoggingEvent> {

    /**
     * The text area component where the logger should output its messages.
     */
    private JTextArea jTALogger;

    /**
     * The text area component where the logger should output its messages.
     */
    private JTextArea jTAShortLogger;

    /**
     * Set the text area component where the logger should output its messages
     * and exceptions.
     *
     * @param jTALogger      the JTextArea component where to output messages
     * @param jTAShortLogger the j ta short logger
     */
    public void setTextArea(JTextArea jTALogger, JTextArea jTAShortLogger) {
        this.jTALogger = jTALogger;
        this.jTAShortLogger = jTAShortLogger;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        // add the new text
        jTALogger.append(eventObject.getFormattedMessage() + "\n");

        // always scroll down to see the latest text
        jTALogger.setCaretPosition(jTALogger.getDocument().getLength());

        // add the new text
        jTAShortLogger.append(eventObject.getFormattedMessage() + "\n");

        // always scroll down to see the latest text
        jTAShortLogger.setCaretPosition(jTALogger.getDocument().getLength());
    }
}
