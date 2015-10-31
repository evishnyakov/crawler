package org.company.crawler.parse;

import org.company.crawler.web.link.IWebPageLink;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Evgeniy Vishnyakov
 */
public class DefaultWebPageDocumentRetriveStrategy implements IWebPageDocumentRetriveStrategy {

	@Override
	public Document getDocument(IWebPageLink webPageLink) throws Exception {
		Connection connection = Jsoup.connect(webPageLink.getURI());
		connection.request().method(Method.GET);
		org.jsoup.Connection.Response response = connection.execute();
		if(!response.contentType().contains("text/html")) {
			return null;
		}
		return response.parse();
	}

}
