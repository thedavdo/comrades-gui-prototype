/**
 *
 */
package edu.purdue.comradesgui.javafx;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.TimeUnit;

public class MoveTimer extends AnimationTimer {


	private long durationLength;

	private long bufferTime;
	private long remainingTime;

	private long prevTimeEvent;

	private boolean isTimerActive = false;
	private boolean isTimerStarted = false;
	private boolean isTimerExpired = false;


	//private boolean bufferCountDown = false;

	private StringProperty remainingTimeProperty;

	private BooleanProperty bufferCountDown;

	public MoveTimer() {

		durationLength = 3570 * 1000;
		remainingTime = durationLength;
		bufferTime = 3000;
		prevTimeEvent = -1;

		remainingTimeProperty = new SimpleStringProperty();
		bufferCountDown = new SimpleBooleanProperty();
	}

	@Override
	public void start() {
	//	turnPressedTime = System.currentTimeMillis();
		super.start();
		prevTimeEvent = System.currentTimeMillis();
		isTimerStarted = true;
		isTimerActive = true;
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

	public boolean isTimerStarted() {
		return isTimerStarted;
	}

	public boolean isTimerExpired() {
		return isTimerExpired;
	}

	public boolean isTimerActive() {
		return isTimerActive;
	}

	public StringProperty getRemainingTime() {
		return remainingTimeProperty;
	}

	public BooleanProperty getBufferCountDownProperty() {
		return bufferCountDown;
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

				if(bufferCountDown.getValue()) {
					if (bufferTime <= deltaTime) {
						bufferCountDown.setValue(false);
						prevTimeEvent = currentTime;
					}
					remainingTimeProperty.setValue(generateTimeFormat(bufferTime - deltaTime));
				}
				else {
					remainingTime -= deltaTime;
					prevTimeEvent = currentTime;
					remainingTimeProperty.setValue(generateTimeFormat(remainingTime));
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
