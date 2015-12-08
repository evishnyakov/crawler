package org.company.crawler;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

import org.company.crawler.executor.TasksDelayExecutor;
import org.company.crawler.parse.WebPageParser;
import org.company.crawler.web.WebLinksStorage;
import org.company.crawler.web.link.IWebPageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageCrawler {

	final static Logger logger = LoggerFactory.getLogger(WebPageCrawler.class);
	
	private static final long HOST_MILLI_SECONDS_DELAY = 1000;
	private static final int LINKS_FROM_HOST_LIMIT = 100;

	private Set<IWebPageLink> corruptedLinks = Sets.newConcurrentHashSet();
	private Set<String> ignoreHosts = Sets.newConcurrentHashSet();
	
	private TasksDelayExecutor linksParserExecutor = new TasksDelayExecutor(HOST_MILLI_SECONDS_DELAY);
	private WebLinksStorage linksStorage = new WebLinksStorage(LINKS_FROM_HOST_LIMIT);
	
	public WebPageCrawler() { }

	public void run(Iterator<IWebPageLink> linksIt) throws Exception {
		logger.debug("Start crawling");
		linksParserExecutor.start();
		try {
			while(linksIt.hasNext()) {
				IWebPageLink webPageLink = linksIt.next();
				logger.debug("Process web site : {}", webPageLink.getURI());
				linksParserExecutor.addTask(webPageLink.getHost(), createRunnable(webPageLink));
			}
		} finally {
			linksParserExecutor.stop();
			logger.debug("Stop crawling");
		}
	}
	
	private Runnable createRunnable(IWebPageLink webPageLink) {
		return () -> {
			if(!ignoreHosts.contains(webPageLink.getHost()) && linksStorage.canStore(webPageLink)) {
				WebPageParser webPageParser = new WebPageParser(webPageLink);
				webPageParser.parse();
				if(webPageParser.isCorrectDocument()) {
					if(linksStorage.storeWebPageLink(webPageLink)) {
						logger.debug("Web page link is stored : {}", webPageLink.getURI());
						webPageParser.getLinks()
							.stream()
							.filter(pl -> webPageLink.getHost().equals(pl.getHost()))
							.filter(pl -> 
								!pl.getURI().endsWith(".jpeg") && 
								!pl.getURI().endsWith(".jpg") && 
								!pl.getURI().endsWith(".png") &&
								!pl.getURI().endsWith(".pdf") && 
								!pl.getURI().endsWith(".doc"))
							.filter(((Predicate<IWebPageLink>)corruptedLinks::contains).negate())
							.filter(linksStorage::canStore)
							.forEach(pL -> linksParserExecutor.addTask(pL.getHost(), createRunnable(pL)));
					} else {
						linksParserExecutor.removeTasks(webPageLink.getHost());
						ignoreHosts.add(webPageLink.getHost());
					}
				} else {
					if(webPageParser.isUnknownHost()) {
						ignoreHosts.add(webPageLink.getHost());
					}
					corruptedLinks.add(webPageLink);
				}
			}
		};
	}
	
	public Collection<IWebPageLink> getFoundLinks() {
		return linksStorage.getLinkes();
	}
	
}
