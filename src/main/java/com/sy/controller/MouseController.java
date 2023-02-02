package com.sy.controller;

import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Java实现鼠标随机移动
 */
public class MouseController implements Runnable {

	private boolean isStop = false;


	@SneakyThrows
	@Override
	public void run() {
		Robot robot=new Robot();

		int x;
		int y;
		Random random = new Random();
		while (!isStop) {
			//随机生成坐标。
			x = 1473;  // 1000
			y = 552;  //1000
			//开始移动鼠标
			robot.mouseMove(x, y);

//          robot.mousePress(KeyEvent.BUTTON3_DOWN_MASK);     // 模拟按下鼠标右键
//          robot.mouseRelease(KeyEvent.BUTTON3_DOWN_MASK);   // 模拟释放鼠标右键

			//鼠标点击
			robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
			//鼠标抬起
			robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);

			int num=random.nextInt(20);
			//每5秒一次操作
			System.out.println("延迟"+(6+num)+"秒");
			robot.delay(1000*(6+num));

		}

	}


	public static void main(String[] args) {
		MouseController m = new MouseController();
		m.run();
//		m.getPointInfo();
	}

	private static void getPointInfo(){
		int x = 0;
		int y = 0;
		while(true){
			PointerInfo pinfo = MouseInfo.getPointerInfo();
			int mx = pinfo.getLocation().x;
			int my = pinfo.getLocation().y;
			if (x!=mx||y!=my){
				x=mx;
				y=my;
				System.out.println("x:"+mx+",y:"+my);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}


}

