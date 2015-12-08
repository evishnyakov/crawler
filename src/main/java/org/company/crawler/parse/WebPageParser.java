package org.company.crawler.parse;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.company.crawler.web.link.IWebPageLink;
import org.company.crawler.web.link.WebPageLink;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageParser {

	private IWebPageDocumentRetriveStrategy retrieveStrategy;
	private IWebPageLink webPageLink;
	private Document document;
	private boolean parsed;
	private boolean unknownHost;
	
	public WebPageParser(IWebPageDocumentRetriveStrategy retrieveStrategy, IWebPageLink webPageLink) {
		this.retrieveStrategy = Preconditions.checkNotNull(retrieveStrategy);
		this.webPageLink = Preconditions.checkNotNull(webPageLink);
	}
	
	public WebPageParser(IWebPageLink webPageLink) {
		this(new DefaultWebPageDocumentRetriveStrategy(), webPageLink);
	}
	
	public void parse() {
		if(parsed) {
			return ;
		}
		try {
			document = retrieveStrategy.getDocument(webPageLink);
		} catch (WebPageParseException e) {
			if(e.getCause() instanceof UnknownHostException) {
				unknownHost = true;
			}
		} finally {
			parsed = true;
		}
	}
	
	public boolean isUnknownHost() {
		checkParsedState();
		return unknownHost;
	}
	
	public boolean isCorrectDocument() {
		checkParsedState();
		return document != null;
	}
	
	public List<IWebPageLink> getLinks() {
		checkParsedState();
		HttpURINormalizer uriNormalizer = new HttpURINormalizer();
		Elements links = document.select("a[href]");
		return links.stream()
			.map(WebPageParser::toURI)
			.filter(Objects::nonNull)
			.filter(uriNormalizer::isHttpURI)
			.map(uriNormalizer::normalize)
			.collect(Collectors.toSet())
			.stream()
			.map(l -> new WebPageLink(l)).collect(Collectors.toList());
	}
	
	private void checkParsedState() {
		if(!parsed) {
			throw new IllegalStateException("Document should be parsed first.");
		}
	}
	

	private static URI toURI(Element link) {
		try {
			return new URI(link.attr("abs:href"));
		} catch(Exception e) {
			// Ignore
		}
		return null;
	}

}
