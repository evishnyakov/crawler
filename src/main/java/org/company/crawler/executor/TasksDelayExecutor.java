package org.company.crawler.executor;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Evgeniy Vishnyakov
 */
public class TasksDelayExecutor {
	
	private ScheduledExecutorService scheduledPool = newScheduledThreadPool(1);
	private ExecutorService tasksExecutorPool = newFixedThreadPool(
			Runtime.getRuntime().availableProcessors() + 1);
	private ConcurrentHashMap<String, List<Runnable>> taskGroupId2Tasks = new ConcurrentHashMap<>();
	
	private final Lock lock = new ReentrantLock();
	private final Condition stopWait = lock.newCondition();

	private AtomicLong runningTasks = new AtomicLong();
	private final long millisecondsDelay;
	
	public TasksDelayExecutor(long millisecondsDelay) {
		this.millisecondsDelay = millisecondsDelay;
	}
	
	public void start() {
		scheduledPool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				taskGroupId2Tasks.values().stream().filter(tasks -> !tasks.isEmpty()).forEach(tasks -> {
					Runnable r = tasks.remove(0);
					tasksExecutorPool.execute(() -> {
						runningTasks.incrementAndGet();
						r.run();
						runningTasks.decrementAndGet();
					});
				});
				lock.lock();
				try {
					if(!hasTasks()) {
						stopWait.signalAll();
					}
				} finally {
					lock.unlock();
				}
			}
		}, 0, millisecondsDelay, TimeUnit.MILLISECONDS);
	}

	public void addTask(String taskGroupId, Runnable task) {
		taskGroupId2Tasks.putIfAbsent(taskGroupId, new CopyOnWriteArrayList<>());
		List<Runnable> list = taskGroupId2Tasks.get(taskGroupId);
		list.add(task);
	}
	
	public void removeTasks(String taskGroupId) {
		taskGroupId2Tasks.remove(taskGroupId);
	}
	
	private boolean hasTasks() {
		return runningTasks.get() > 0 || taskGroupId2Tasks.values().stream().filter(c -> !c.isEmpty()).findAny().isPresent();
	}
	
	public void stop() {
		lock.lock();
		try {
			while(hasTasks()) {
				try {
					stopWait.await();
				} catch (InterruptedException e) {
					// Ignore
				}
			}
			scheduledPool.shutdown();
			tasksExecutorPool.shutdown();
		} finally {
			lock.unlock();
		}		
	}
	
}