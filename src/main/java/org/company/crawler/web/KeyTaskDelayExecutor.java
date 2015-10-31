package org.company.crawler.web;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Evgeniy Vishnyakov
 */
class KeyTaskDelayExecutor {
	
	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
	private ExecutorService taskExecutorPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	private ConcurrentHashMap<String, List<Runnable>> host2Linkes = new ConcurrentHashMap<>();
	
	private AtomicLong runningTasks = new AtomicLong();
	
	public KeyTaskDelayExecutor() {
		
	}
	
	public void start() {
		scheduledPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				host2Linkes.values().stream().filter(c -> !c.isEmpty()).forEach(c -> {
					Runnable r = c.remove(0);
					taskExecutorPool.execute(() -> {
						runningTasks.incrementAndGet();
						r.run();
						runningTasks.decrementAndGet();
					});
				});
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
	public void addHost(String host, Runnable runnable) {
		host2Linkes.putIfAbsent(host, new CopyOnWriteArrayList<>());
		List<Runnable> list = host2Linkes.get(host);
		list.add(runnable);
	}
	
	public void stop() {
		while(runningTasks.get() > 0 || host2Linkes.values().stream().filter(c -> !c.isEmpty()).findAny().isPresent()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		scheduledPool.shutdown();
		taskExecutorPool.shutdown();
	}
	
}