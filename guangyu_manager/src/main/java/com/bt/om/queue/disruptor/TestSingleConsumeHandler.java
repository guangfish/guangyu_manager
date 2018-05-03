package com.bt.om.queue.disruptor;

import org.apache.log4j.Logger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class TestSingleConsumeHandler {
	private static final Logger logger = Logger.getLogger(TestSingleConsumeHandler.class);
	final long objSize = 1 << 10;

	public void testConsumeHandler() throws Exception {
		final DisruptorQueueImpl queue = new DisruptorQueueImpl("name", ProducerType.SINGLE, (int) objSize,
				new BlockingWaitStrategy());
		Thread producer = new Thread(new Runnable() {// 生产者
			@Override
			public void run() {
				long count = 1;
				try {
					while (true) {
						queue.publish(new TestObject(count++));
//						System.out.println("produce==" + Thread.currentThread().getName());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Thread consumer = new Thread(new Runnable() {// 消费者
			@Override
			public void run() {
				try {
					while (true) {
						queue.consumeBatch(new TestObjectAnalysisHandler());
//						System.out.println("consumer==" + Thread.currentThread().getName());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		producer.start();
		consumer.start();
	}

	public static void main(String[] args) throws Exception {
		TestSingleConsumeHandler testConsumeHandler = new TestSingleConsumeHandler();
		testConsumeHandler.testConsumeHandler();
	}

}
