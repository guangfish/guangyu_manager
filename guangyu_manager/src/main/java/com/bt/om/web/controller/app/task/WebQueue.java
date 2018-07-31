package com.bt.om.web.controller.app.task;

import org.apache.log4j.Logger;

import com.bt.om.entity.TkInfoTask;
import com.bt.om.queue.disruptor.DisruptorQueueImpl;
import com.bt.om.selenium.ProductUrlTrans;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
/**
 * 
 * @author Lenovo
 * 网页爬虫的任务队列
 */
public class WebQueue {
	private static final Logger logger = Logger.getLogger(WebQueue.class);
	// 初始化队列，定义队列长度
	final static DisruptorQueueImpl queue = new DisruptorQueueImpl("WebQueue", ProducerType.MULTI, 256,
			new BlockingWaitStrategy());
	static{
		new ProductUrlTrans();
	}

	public static DisruptorQueueImpl getQueue(){
		return queue;
	}
	public static void put(TkInfoTask tkInfoTask) {
		logger.info("请求入队列");
		queue.publish(tkInfoTask);
	}

	public static Object get() {
		logger.info("获取任务");
		return queue.poll();
	}
	
	public static long getSize() {
		return queue.population();
	}
}
