package com.bt.om.queue.disruptor;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;

public class TestSingle {
	private static final Logger logger = Logger.getLogger(TestSingle.class);
	final long objSize = 1 << 10;

	public void testDisruptorQueue() throws Exception {
		final DisruptorQueueImpl queue = new DisruptorQueueImpl("name", ProducerType.SINGLE, (int) objSize,
				new BlockingWaitStrategy());
		Thread producer = new Thread(new Runnable() {// 生产者
			@Override
			public void run() {
				try {
					for (long i = 1; i <= objSize; i++) {
						queue.publish(new TestObject(i));
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
					TestObject readObj = null;
					for (long i = 1; i <= objSize; i++) {
						// do something
						readObj = (TestObject) queue.take();
						if(readObj!=null){
							logger.info(readObj.getValue());
						}else{
							System.out.println("readObj is null");
						}
						System.out.println(queue.getState());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		long timeStart = System.currentTimeMillis();
		producer.start();
		consumer.start();
		consumer.join();
		producer.join();
		long timeEnd = System.currentTimeMillis();
		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
		logger.info((timeEnd - timeStart) + "/" + df.format(objSize) + " = "
				+ df.format(objSize / (timeEnd - timeStart) * 1000));
	}

	public static void main(String[] args) throws Exception {
		TestSingle test = new TestSingle();
		test.testDisruptorQueue();
	}

}
