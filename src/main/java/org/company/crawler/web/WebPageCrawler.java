package org.company.crawler.web;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.company.crawler.parse.WebPageParser;
import org.company.crawler.web.link.IWebPageLink;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageCrawler {

	private KeyTaskDelayExecutor keyTaskDelayExecutor = new KeyTaskDelayExecutor();
	private WebLinksStorage linksStorage = new WebLinksStorage();
	
	public WebPageCrawler() { }

	public void run(Consumer<IWebPageLinkProcessor> supplier) throws Exception {
		keyTaskDelayExecutor.start();
		try {
			supplier.accept(new IWebPageLinkProcessor() {
				@Override
				public void process(IWebPageLink webPageLink) {
					keyTaskDelayExecutor.addHost(webPageLink.getHost(), createRunnable(webPageLink));
				}
			});
		} finally {
			keyTaskDelayExecutor.stop();	
		}
	}
	
	private Runnable createRunnable(IWebPageLink webPageLink) {
		return () -> {
			if(linksStorage.shouldProcess(webPageLink)) {
				linksStorage.storeWebPageLink(webPageLink);
				//System.out.println("F: " + webPageLink.getURI());
				List<IWebPageLink> parse = new WebPageParser().parse(webPageLink);
				parse.stream().filter(linksStorage::shouldProcess)
				.forEach(pL -> {
					keyTaskDelayExecutor.addHost(pL.getHost(), createRunnable(pL));	
				});
			}
		};
	}
	
	public Collection<IWebPageLink> getFoundLinks() {
		return linksStorage.getLinkes();
	}
	
}
