package com.adobe.aem.init.dogmatix.core;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * A Utility class which maintains the server runtime statistics
 * 
 * Currently only 2 parameters consist of the statistics
 * 
 * <ul>
 * 	<li>Server Up Time</li>
 * 	<li>Memory Consumption</li>
 * </ul>
 * 
 * @author vnagpal
 *
 */
public class ServerStatistics {
	
	private static final long MEGABYTE = 1024L * 1024L;

	/**
	 * Converts bytes value to megabytes using the conversion factor
	 * 
	 * @param bytes number of bytes
	 * @return equivalent number of megabytes
	 */
	private static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	/**
	 * Server startup timestamp in nanos elapsed since epoch
	 */
	private static long startupTime = 0;

	/**
	 * Sets the server startup timestamp. Should be invoked at server launch.
	 */
	public static void serverStarted() {
		if (startupTime == 0) {
			startupTime = System.nanoTime();
		}
	}

	/**
	 * Converts a nanosecond duration into human readable 
	 * (D days, H hours, M minutes, S seconds) format
	 * 
	 * @param duration time period to convert 
	 * @return formatted string equivalent
	 */
	private static String nanosDurationPrettyPrint(long duration) {
		long days = NANOSECONDS.toDays(duration);
		long hours = NANOSECONDS.toHours(duration);
		long minutes = NANOSECONDS.toMinutes(duration);
		long seconds = NANOSECONDS.toSeconds(duration);

		String durationStr = "";

		if (days > 0) {
			durationStr += days + " Days ";
			hours = hours - 24 * days;
			minutes = minutes - 24 * 60 * days;
			seconds = seconds - 24 * 60 * 60 * days;
		}

		if (hours > 0) {
			durationStr += hours + " Hours ";
			minutes = minutes - 60 * hours;
			seconds = seconds - 60 * 60 * hours;
		}

		if (minutes > 0) {
			durationStr += minutes + " Minutes ";
			seconds = seconds - 60 * minutes;
		}

		if (seconds > 0) {
			durationStr += seconds + " Seconds ";
		}

		return durationStr;
	}

	/**
	 * Since the statistics are required at runtime, they will often be requested via AJAX
	 * and hence a JSON response is appropriate.
	 * This method converts the statistics (duration & memory) into a json string.
	 * 
	 * @return {"duration":"D days, H hours, M minutes, S seconds", "memory":"X MB"}
	 */
	public static String getStatsAsJSON() {
		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = allocatedMemory - freeMemory;
		long currentTime = System.nanoTime();
		long timeElapsed = currentTime - startupTime;

		return "{\"duration\":\"" + nanosDurationPrettyPrint(timeElapsed)
				+ "\", \"memory\":\"" + bytesToMegabytes(usedMemory)
				+ " MB\"}";
	}

}
