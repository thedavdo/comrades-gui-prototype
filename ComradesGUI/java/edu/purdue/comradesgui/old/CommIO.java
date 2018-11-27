package edu.purdue.comradesgui.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommIO {
	ComradesFrame frame;
	BufferedReader READER;
	PrintWriter WRITER;
	StringBuffer LOG_SB;
	boolean LOGGING = true;

	public CommIO(ComradesFrame cf) {
		frame = cf;
		LOG_SB = new StringBuffer("");
	}

	public void SleepFor(int x) {
		try {
			Thread.sleep(x);
		}
		catch (InterruptedException int_exc) {
			frame.TellInfo("Internalized sleep failure!");
		}
	}

	public boolean IsReady() {
		try {
			return READER.ready();
		}
		catch (IOException io_exc) {
			frame.TellInfo("Error in reader");
			return false;
		}
	}

	public StringBuffer GetTime() // hh:mm:ss.xyz
	{
		Date DATE = new Date();
		SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss.SSS");
		StringBuffer SB = new StringBuffer();
		SDF.format(DATE, SB, new FieldPosition(0));
		return SB;
	}

	public void RelevateSize() {
		int sz = LOG_SB.length();
		if (sz > (16 << 20)) /* 16mb */
			LOG_SB = new StringBuffer(LOG_SB.substring(4 << 20));
	}

	public String DemandLine() {
		try {
			String S = READER.readLine();
			if (S.startsWith("DEBUG ")) /* incur */
				System.out.println(S);
			if (LOGGING) {
				LOG_SB.append("<--[");
				LOG_SB.append(GetTime());
				LOG_SB.append("]:");
				LOG_SB.append(S);
				LOG_SB.append("\n");
				RelevateSize();
			}
			return S;
		}
		catch (IOException io_exc) {
			frame.TellInfo("Error in reading line (reader)");
			return null;
		}
	}

	public void SendTo(String S, boolean flush) {
		if (LOGGING) {
			LOG_SB.append("[");
			LOG_SB.append(GetTime());
			LOG_SB.append("]:-->");
			LOG_SB.append(S);
			if (flush)
				LOG_SB.append(" [flush]");
			LOG_SB.append("\n");
			RelevateSize();
		}
		WRITER.println(S);
		if (flush)
			WRITER.flush();
	}

	public void ReadyOK(boolean show) // UCI
	{
		SendTo("isready", true);
		WaitForThroughPut("readyok", -1, show);
	}

	public boolean WaitForThroughPut(String S, int time_out, boolean show) {
		int x = 0;
		while (true) {
			while (!IsReady()) {
				SleepFor(10); // milli
				x += 10;
				if (time_out > 0 && x > time_out) {
					frame.TellInfo("Time out " + time_out + " seconds waiting  for " + S);
					return false;
				}
			}
			String L = DemandLine();
			if (show)
				frame.TellInfo(L);
			if (L.equals(S))
				return true;
		}
	}
}
