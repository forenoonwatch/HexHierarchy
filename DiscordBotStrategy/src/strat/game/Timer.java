package strat.game;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;

public class Timer {
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEEE MMMM d',' y G");
	
	public static String formatDate(GregorianCalendar date) {
		return DATE_FORMATTER.format(date.getTime());
	}
	
	private ZonedDateTime startDate;
	private Duration timePerTurn;
	
	public Timer(Duration timePerTurn) {
		this.timePerTurn = timePerTurn;
		
		startDate = ZonedDateTime.now();
		startDate = startDate.minusHours(startDate.getHour()).minusMinutes(startDate.getMinute()).minusSeconds(startDate.getSecond());
		
		shouldAdvanceTurn();
	}
	
	public boolean shouldAdvanceTurn() {
		long sec = ChronoUnit.SECONDS.between(startDate, ZonedDateTime.now());
		
		if (sec >= timePerTurn.getSeconds()) {
			long dPassed = sec / timePerTurn.getSeconds();
			startDate = startDate.plus(timePerTurn.multipliedBy(dPassed));
			
			return true;
		}
		
		return false;
	}
}
