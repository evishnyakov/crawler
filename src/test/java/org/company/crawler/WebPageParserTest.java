package org.company.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.List;

import org.company.crawler.parse.IWebPageDocumentRetriveStrategy;
import org.company.crawler.parse.WebPageParser;
import org.company.crawler.web.link.IWebPageLink;
import org.company.crawler.web.link.WebPageLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageParserTest {

	private class TestWebPageDocumentRetriveStrategy implements IWebPageDocumentRetriveStrategy {
		@Override
		public Document getDocument(IWebPageLink webPageLink) throws Exception {
			File file = new File(getFileURL(webPageLink.getURI()).toURI());
			return Jsoup.parse(new FileInputStream(file), "UTF-8", "http://www.fake.com");
		}		
	}
	
	@Test
	public void parseHtml() throws Exception  {
		List<IWebPageLink> links = new WebPageParser(
			new TestWebPageDocumentRetriveStrategy()).parse(new WebPageLink("links_test.html"));

		Assert.assertEquals(6, links.size());
		ImmutableList<String> list = ImmutableList.of(
			"http://www.test.net/",
			"http://www.fake.com/page2.htm",
			"http://www.fake.com/../page1.htm",
			"http://www.test.net/t1.html",
			"http://www.test.net:8080/",
			"http://www.fake.com/");
		for(IWebPageLink link : links) {
			Assert.assertTrue(list.contains(link.getURI()));
		}
	}

	private URL getFileURL(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();
		return classLoader.getResource(fileName);
	}
}
