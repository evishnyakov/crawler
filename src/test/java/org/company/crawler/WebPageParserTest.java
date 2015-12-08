package org.company.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.company.crawler.parse.IWebPageDocumentRetriveStrategy;
import org.company.crawler.parse.WebPageParser;
import org.company.crawler.web.link.IWebPageLink;
import org.company.crawler.web.link.WebPageLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageParserTest {

	private class TestWebPageDocumentRetriveStrategy implements IWebPageDocumentRetriveStrategy {
		@Override
		public Document getDocument(IWebPageLink webPageLink) {
			try {
				File file = new File(getFileURL(webPageLink.getURI()).toURI());
				return Jsoup.parse(new FileInputStream(file), "UTF-8", "http://www.fake.com");	
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}		
	}
	
	@Test
	public void parseHtml() throws Exception  {
		WebPageParser webPageParser = 
				new WebPageParser(new TestWebPageDocumentRetriveStrategy(), new WebPageLink("links_test.html"));
		webPageParser.parse();
		List<IWebPageLink> links = webPageParser.getLinks(); 

		Assert.assertEquals(6, links.size());
		List<String> list = Arrays.asList(
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
