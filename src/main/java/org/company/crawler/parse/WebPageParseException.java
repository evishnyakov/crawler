package org.company.crawler.parse;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebPageParseException(String message, Throwable t) {
		super(message, t);
	}
	
	public WebPageParseException(String message) {
		super(message);
	}

}
