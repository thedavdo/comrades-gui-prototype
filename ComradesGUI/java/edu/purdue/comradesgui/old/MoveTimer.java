/**
 *
 */
package edu.purdue.comradesgui.old;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

/**
 * This class creates a move timer
 *
 * @author Rick Perry
 */
public class MoveTimer implements ActionListener {

	private static final int RATE = 1; // Set timer update rate to 1 millisecond
	private static Color PAUSE_COLOR = Color.black;
	private static Color ACTIVE_COLOR = new Color(0, 153, 0);
	private static Color EXPIRED_COLOR = Color.red;
	private long count = 0; // Current time on timer in milliseconds
	private long startTime = 300000; // Start time in milliseconds. Default is 5 mins.
	private boolean isTimerActive = false;
	private boolean isTimerStarted = false;
	private boolean isTimerExpired = false;
	private Timer tmr = new Timer(RATE, this);
	private JLabel timeLabel;
	private String labelPrefix;
	private ComradesFrame CF;
	private long incrementTime;

	/**
	 * Default class constructor
	 */
	public MoveTimer() {
		// Default constructor
		count = startTime;
		labelPrefix = "";
	}

	/**
	 * Class constructor
	 *
	 * @param start       Time in seconds to use as start time
	 * @param textLabel   Label to write time to
	 * @param labelPrefix Text to use as label prefix
	 */
	public MoveTimer(ComradesFrame cf, int start, int increment, JLabel textLabel, String textPrefix) {
		CF = cf;
		count = TimeUnit.SECONDS.toMillis(start);
		startTime = start * 1000;
		incrementTime = increment * 1000;
		timeLabel = textLabel;
		labelPrefix = textPrefix;
		updateLabel();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {

		// Respond to timer tick event
		if (isTimerActive && count > 0) {
			count--;
			if (count <= 0) {
				isTimerExpired = true;
				stop();
				updateLabel();
				CF.checkGameOver();
				return;
			}

			updateLabel();
		}
	}

	/**
	 * Sets the time start time
	 *
	 * @param time The new start time
	 */
	public void setStartTime(int time) {
		startTime = time;
	}

	/**
	 * Gets the current start time
	 *
	 * @return The current start time for the timer
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Gets the current time left on this timer
	 *
	 * @return The current time
	 */
	public long getTime() {
		return count;
	}

	/**
	 * Gets the current time left on this timer as a string
	 *
	 * @return The current time formatted as a string
	 */
	public String toString() {
		return TimeFormat(count);
	}

	/**
	 * Starts the timer
	 */
	public void start() {
		setTimerColor(ACTIVE_COLOR);
		count = startTime;
		isTimerActive = true;
		tmr.start();
		isTimerStarted = true;
		isTimerExpired = false;
	}

	/**
	 * Resumes the timer if it is paused
	 */
	public void resume() {
		setTimerColor(ACTIVE_COLOR);
		isTimerActive = true;
		tmr.restart();
	}

	/**
	 * Stops the timer
	 */
	public void stop() {
		setTimerColor(EXPIRED_COLOR);
		isTimerActive = false;
		isTimerStarted = false;
		tmr.stop();
	}

	/**
	 * Pauses the timer
	 */
	public void pause() {
		setTimerColor(PAUSE_COLOR);
		isTimerActive = false;
	}

	/**
	 * Resets the timer
	 */
	public void reset() {
		tmr.stop();
		setTimerColor(PAUSE_COLOR);
		count = startTime;
		isTimerActive = false;
		isTimerStarted = false;
		isTimerExpired = false;
	}

	/**
	 * Determines if timer has been started
	 *
	 * @return True if timer has already started or false otherwise
	 */
	public boolean isStarted() {
		return isTimerStarted;
	}

	/**
	 * Determines if time has expired
	 *
	 * @return True if timer has expired or false otherwise
	 */
	public boolean isExpired() {
		return isTimerExpired;
	}

	/**
	 * Determines if timer is active
	 *
	 * @return True is timer is running or false otherwise
	 */
	public boolean isActive() {
		return isTimerActive;
	}

	/**
	 * Set the timer text color
	 *
	 * @param sColor Color to use for timer text
	 */
	private void setTimerColor(Color sColor) {
		timeLabel.setForeground(sColor);
	}

	/**
	 * Converts time count to hh:mm:ss format
	 *
	 * @param count Time to convert
	 * @return String containing converted time in hh:mm:ss format
	 */
	private String TimeFormat(long count) {
		long hours = TimeUnit.MILLISECONDS.toHours(count);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(count) % 60;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(count) % 60;
		long milsec = (count % 1000) / 10;

		return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milsec);
	}

	public void updateLabel() {
		timeLabel.setText(labelPrefix + TimeFormat(count));
	}

	// increments the timer by the increment in options
	public void incrementCount () {
		count += incrementTime;
	}

}
