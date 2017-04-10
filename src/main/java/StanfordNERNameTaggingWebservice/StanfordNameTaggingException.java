/**
 *
 * @author alvin
 */
package StanfordNERNameTaggingWebservice;

import java.io.Serializable;

/**
 * <p>
 * StandfordNERTaggingException
 * </p>
 *
 * This class is used to hold the necessary informations for an Exception.
 */
public class StanfordNameTaggingException extends Exception implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4180632264216555028L;

	/** The exception message. */
	private String exceptionMessage;

	/** The detailed message. */
	private String detailedMessage;

	/**
	 *
	 */
	public StanfordNameTaggingException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StanfordNameTaggingException(String detailedMessage, String message, Throwable cause) {
		super(message, cause);
		this.detailedMessage = detailedMessage;
		exceptionMessage = message;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StanfordNameTaggingException(String message, Throwable cause) {
		super(message, cause);
		detailedMessage = message;
		exceptionMessage = message;
	}

	/**
	 * @param message
	 */
	public StanfordNameTaggingException(String message) {
		super(message);
		detailedMessage = message;
		exceptionMessage = message;
	}

	/**
	 * @param cause
	 */
	public StanfordNameTaggingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the exceptionMessage
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * @param exceptionMessage
	 *            the exceptionMessage to set
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	/**
	 * @return the detailedMessage
	 */
	public String getDetailedMessage() {
		return detailedMessage;
	}

	/**
	 * @param detailedMessage
	 *            the detailedMessage to set
	 */
	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

}
