package com.easy.nettyPush;

import java.util.Date;

public class OfferRunnable implements Runnable{

	private AppTest app;
	public OfferRunnable(AppTest app){
		this.app = app;
	}
	@Override
	public void run() {
			//app.offer("aaaaaaaaaaa");
		app.notify();
		System.out.println("offer:==="+new Date());
	}


}
