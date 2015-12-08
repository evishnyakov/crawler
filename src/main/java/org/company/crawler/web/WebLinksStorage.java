package org.company.crawler.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.company.crawler.web.link.IWebPageLink;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebLinksStorage {
	
	private ConcurrentMap<String, Set<IWebPageLink>> host2links = new ConcurrentHashMap<>();
	private final int linksFromHostLimit;

	/**
	 * @param linksFromHostLimit max number of links from one host that storage can contain.
	 */
	public WebLinksStorage(int linksFromHostLimit) {
		Preconditions.checkArgument(linksFromHostLimit > 0);
		this.linksFromHostLimit = linksFromHostLimit;
	}
	
	public boolean canStore(IWebPageLink link) {
		host2links.putIfAbsent(link.getHost(), Sets.newLinkedHashSet());
		Set<IWebPageLink> links = host2links.get(link.getHost());
		synchronized (links) {
			if(links.size() < linksFromHostLimit) {
				return true;
			}
			return !links.contains(link);
		}
	}
	
	public boolean storeWebPageLink(IWebPageLink link) {
		host2links.putIfAbsent(link.getHost(), Sets.newLinkedHashSet());
		Set<IWebPageLink> links = host2links.get(link.getHost());
		synchronized (links) {
			if(links.size() >= linksFromHostLimit) {
				return false;
			}
			return links.add(link);
		}
	}

	public Collection<IWebPageLink> getLinkes() {
		Collection<IWebPageLink> resuls = new ArrayList<IWebPageLink>();
		host2links.forEach((host, links) -> {
			synchronized (links) {
				resuls.addAll(links);
			}
		});
		return resuls;
	}
	
}