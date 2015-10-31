package org.company.crawler.parse;

import org.company.crawler.web.link.IWebPageLink;
import org.jsoup.nodes.Document;

/**
 * @author Evgeniy Vishnyakov
 */
public interface IWebPageDocumentRetriveStrategy {

	Document getDocument(IWebPageLink webPageLink) throws Exception;
	
}
