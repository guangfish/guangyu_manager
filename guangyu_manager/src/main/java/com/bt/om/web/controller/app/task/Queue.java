package com.bt.om.web.controller.app.task;

import org.apache.log4j.Logger;

import com.bt.om.entity.TkInfoTask;
import com.bt.om.queue.disruptor.DisruptorQueueImpl;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class Queue {
	private static final Logger logger = Logger.getLogger(Queue.class);
	// 初始化队列，定义队列长度
	final static DisruptorQueueImpl queue = new DisruptorQueueImpl("name", ProducerType.SINGLE, 256,
			new BlockingWaitStrategy());

	public static void put(TkInfoTask tkInfoTask) {

		logger.info("淘口令请求入队列");
		queue.publish(tkInfoTask);
	}

	public static Object get() {
		// logger.info("consumer..");
		logger.info("获取淘口令任务");
		return queue.poll();
	}
}
