/**
 *
 */
package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;

import java.util.concurrent.TimeUnit;

public class MoveTimer extends AnimationTimer {


	private long durationLength;

	private long bufferTime;
	private long remainingTime;

	private long prevTimeEvent;

	private boolean isTimerActive = false;
	private boolean isTimerStarted = false;
	private boolean isTimerExpired = false;


	private boolean bufferCountDown = false;

	public MoveTimer() {

		durationLength = 300 * 1000;
		remainingTime = durationLength;
		bufferTime = 3000;
		prevTimeEvent = -1;
	}

	@Override
	public void start() {
	//	turnPressedTime = System.currentTimeMillis();
		super.start();
		prevTimeEvent = System.currentTimeMillis();
		isTimerStarted = true;
		isTimerActive = true;
		bufferCountDown = (bufferTime != -1);
	}

	public void pause() {
		isTimerActive = false;
		prevTimeEvent = -1;
	}

	public void resume() {
		isTimerActive = true;
		prevTimeEvent = System.currentTimeMillis();
		bufferCountDown = (bufferTime != -1);
	}

	public boolean isTimerStarted() {
		return isTimerStarted;
	}

	public boolean isTimerExpired() {
		return isTimerExpired;
	}

	public boolean isTimerActive() {
		return isTimerActive;
	}

//	@Override
//	public void stop() {
//		super.stop();
//	}

	@Override
	public void handle(long now) {

		if(isTimerActive && !isTimerExpired) {

			if (remainingTime <= 0) {
				remainingTime = 0;
				isTimerExpired = true;
			}
			else {
				long currentTime = System.currentTimeMillis();
				long deltaTime = (currentTime - prevTimeEvent);


				if(bufferCountDown) {
					if (bufferTime != -1) {
						if (bufferTime > deltaTime)
							System.out.println(bufferTime - deltaTime);
						else
							bufferCountDown = false;
					}
				}
				else {
					remainingTime -= deltaTime;
					prevTimeEvent = currentTime;
					System.out.println(remainingTime);
				}
			}
		}
	}

	public void setBufferTime(long buffer) {
		bufferTime = buffer;
	}

	public void addTime(long time) {
		remainingTime += time;
	}

	private String generateTimeFormat(long count) {
		long hours = TimeUnit.MILLISECONDS.toHours(count);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(count) % 60;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(count) % 60;
		long milsec = (count % 1000) / 10;

		return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milsec);
	}
}
