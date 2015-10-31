package org.company.crawler.web;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.company.crawler.web.link.IWebPageLink;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Evgeniy Vishnyakov
 */
class WebLinksStorage {
	
	private Set<IWebPageLink> links = Sets.newHashSet();
	private Map<String, Integer> host2Count = Maps.newHashMap();
	private ReadWriteLock lock = new ReentrantReadWriteLock();

	public Collection<IWebPageLink> getLinkes() {
		lock.readLock().lock();
		try {
			return Sets.newHashSet(links);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void storeWebPageLink(IWebPageLink link) {
		lock.writeLock().lock();
		try {
			links.add(link);
			String host = link.getHost();
			Integer count = host2Count.get(host);
			if(count == null) {
				count = 1;
			} else {
				count++;
			}
			host2Count.put(host, count);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public boolean shouldProcess(IWebPageLink link) {
		lock.readLock().lock();
		try {
			if(links.contains(link)) {
				return false;
			}
			String host = link.getHost();
			Integer count = host2Count.get(host);
			return count == null || count < 100;
		} finally {
			lock.readLock().unlock();
		}
	}
	
}