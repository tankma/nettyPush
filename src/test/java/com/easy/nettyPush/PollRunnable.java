package com.easy.nettyPush;

public class PollRunnable implements Runnable{

	private AppTest app;
	public PollRunnable(AppTest app){
		this.app = app;
	}
	@Override
	public void run() {
		try {
			app.poll();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
