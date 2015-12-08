package org.company.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;

import org.company.crawler.web.WebLinksInputStreamReader;
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
			WebPageCrawler webPageCrawler = new WebPageCrawler();
			webPageCrawler.run(new WebLinksInputStreamReader().readLinks(in));
			
			PrintWriter writer = new PrintWriter(new File("crawler-result.txt"), "UTF-8");
			Collection<IWebPageLink> foundLinks = webPageCrawler.getFoundLinks();
			foundLinks.forEach(link -> {
				writer.write(link.getURI());
				writer.write("\n");
			});
			writer.write(" ===============  \n");
			writer.write("TOTAL:  " + foundLinks.size() + "\n");
			writer.write("TIME: " + (System.currentTimeMillis() - curm));
			writer.close();
		} finally {
			Closeables.closeQuietly(in);
		}
	}

}
