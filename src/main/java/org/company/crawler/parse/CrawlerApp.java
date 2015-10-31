package org.company.crawler.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.company.crawler.web.WebLinksInputStreamReader;
import org.company.crawler.web.WebPageCrawler;
import org.company.crawler.web.link.IWebPageLink;

import com.google.common.io.Closeables;

/**
 * @author Evgeniy Vishnyakov
 */
public class CrawlerApp {

	public static void main(String[] args) throws Exception {
		ClassLoader classLoader = CrawlerApp.class.getClassLoader();
		URL resource = classLoader.getResource("100DomainsForCrawling.txt");
		File file = new File(resource.toURI());
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			long curm = System.currentTimeMillis();
			Iterator<IWebPageLink> linksIt = new WebLinksInputStreamReader().readLinks(in);
			WebPageCrawler webPageCrawler = new WebPageCrawler();
			webPageCrawler.run(linkProcessor -> {
				while(linksIt.hasNext()) {
					IWebPageLink nextLink = linksIt.next();
					linkProcessor.process(nextLink);
				}
			});
			webPageCrawler.getFoundLinks().forEach(link -> {
				System.out.println(link.getURI());
			});
			System.out.println("TIME: " + (System.currentTimeMillis() - curm));
		} finally {
			Closeables.closeQuietly(in);
		}
	}

}
