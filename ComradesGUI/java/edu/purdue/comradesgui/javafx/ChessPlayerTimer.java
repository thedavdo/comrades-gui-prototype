package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;

import java.util.concurrent.TimeUnit;

public class ChessPlayerTimer extends AnimationTimer {


	private long durationLength;

	private long bufferTime;
	private long remainingTime;

	private long prevTimeEvent;

	private boolean isTimerActive = false;
	private boolean isTimerStarted = false;
	private boolean isTimerExpired = false;

	private StringProperty timerDisplayProperty;
	private BooleanProperty bufferCountDown;

	public ChessPlayerTimer() {

		durationLength = 300 * 1000;
		remainingTime = durationLength;
		bufferTime = -1;
		prevTimeEvent = -1;

		timerDisplayProperty = new SimpleStringProperty();
		bufferCountDown = new SimpleBooleanProperty();
	}

	public void setDurationLength(long duration) {
		this.durationLength = duration;
		remainingTime = duration;
	}

	public long getDurationLength() {
		return durationLength;
	}

	public void setBufferTime(long buffer) {
		bufferTime = buffer;
	}

	public long getBufferTime() {
		return bufferTime;
	}

	public long getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(long time) {
		remainingTime = time;
	}

	public void decrementTime(long time) {
		remainingTime -= time;
	}

	public void incrementTime(long time) {
		remainingTime += time;
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

	public StringProperty getTimerDisplayProperty() {
		return timerDisplayProperty;
	}

	public BooleanProperty getBufferCountDownProperty() {
		return bufferCountDown;
	}

	/**
	 * Initializes the Timer but does not activate the timer countdown
	 */
	public void initialize() {
		super.start();

		isTimerStarted = true;
		isTimerActive = false;
	}


	/**
	 * Initializes and activates the timer countdown.
	 */
	public void start() {

		if(!this.isTimerStarted)
			this.initialize();

		prevTimeEvent = System.currentTimeMillis();
		bufferCountDown.setValue(bufferTime != -1);
		isTimerActive = true;
	}

	public void reset() {
		remainingTime = durationLength;
		prevTimeEvent = -1;
		bufferCountDown.setValue(bufferTime != -1);
	}

	public void pause() {
		isTimerActive = false;
		prevTimeEvent = -1;
	}

	public void resume() {
		isTimerActive = true;
		prevTimeEvent = System.currentTimeMillis();
		bufferCountDown.setValue(bufferTime != -1);
	}

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

				String timeStr = "Time Left: ";

				if(bufferCountDown.getValue()) {
					if (bufferTime <= deltaTime) {
						bufferCountDown.setValue(false);
						prevTimeEvent = currentTime;
					}
					timerDisplayProperty.setValue(generateTimeFormat(remainingTime) + " (" + generateTimeFormat(bufferTime - deltaTime) + ")");
				}
				else {
					remainingTime -= deltaTime;
					prevTimeEvent = currentTime;
					timerDisplayProperty.setValue(generateTimeFormat(remainingTime));
				}
			}
		}
	}

	private String generateTimeFormat(long time) {

		long hours = TimeUnit.MILLISECONDS.toHours(time);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
		long milsec = (time % 1000) / 10;

		String str = String.format("%02d.%02d", seconds, milsec);

		if(minutes != 0 || hours != 0)
			str =String.format("%02d",minutes) + ":" + str;

		if(hours != 0)
			str = String.format("%02d",hours) + ":" + str;

		return str;
	}
}
