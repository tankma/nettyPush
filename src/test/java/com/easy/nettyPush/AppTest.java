package com.easy.nettyPush;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest
{
	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public void poll() throws InterruptedException{
		String s = queue.poll(10, TimeUnit.SECONDS);
		System.out.println(s+"========"+new Date());
	}
	public void offer(String text){
		queue.offer(text);
	}
	public static void main(String[] args) throws InterruptedException {
		System.out.println(new Date());
		final AppTest test = new AppTest();
		Runnable pr = new PollRunnable(test);
		new Thread(pr).start();
		
		
		Thread.sleep(5000L);
		System.out.println("============="+new Date());
		Runnable or = new OfferRunnable(test);
		new Thread(or).start();
	}
}
