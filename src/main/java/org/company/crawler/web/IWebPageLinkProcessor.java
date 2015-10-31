package org.company.crawler.web;

import org.company.crawler.web.link.IWebPageLink;

/**
 * @author Evgeniy Vishnyakov
 */
public interface IWebPageLinkProcessor {
	
	void process(IWebPageLink webPageLink);
	
}