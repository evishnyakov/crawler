package org.company.crawler.parse;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.company.crawler.web.link.IWebPageLink;
import org.company.crawler.web.link.WebPageLink;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageParser {

	private IWebPageDocumentRetriveStrategy retrieveStrategy;
	
	public WebPageParser(IWebPageDocumentRetriveStrategy retrieveStrategy) {
		this.retrieveStrategy = Preconditions.checkNotNull(retrieveStrategy);
	}
	
	public WebPageParser() {
		this(new DefaultWebPageDocumentRetriveStrategy());
	}
	
	public List<IWebPageLink> parse(IWebPageLink webPageLink) {
		HttpURINormalizer uriNormalizer = new HttpURINormalizer();
		Document doc;
		try {
			doc = retrieveStrategy.getDocument(webPageLink);
		} catch (Exception e) {
			return Lists.newArrayList();
		}
		if(doc == null) {
			return Lists.newArrayList();
		}
		Elements links = doc.select("a[href]");
		return links.stream()
			.map(WebPageParser::toURI)
			.filter(Objects::nonNull)
			.filter(uriNormalizer::isHttpURI)
			.map(uriNormalizer::normalize)
			.collect(Collectors.toSet())
			.stream()
			.map(l -> new WebPageLink(l)).collect(Collectors.toList());
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
