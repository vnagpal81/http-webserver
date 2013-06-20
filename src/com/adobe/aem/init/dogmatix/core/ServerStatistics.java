package com.adobe.aem.init.dogmatix.core;

import static java.util.concurrent.TimeUnit.NANOSECONDS;


public class ServerStatistics {
	private static final long MEGABYTE = 1024L * 1024L;

	private static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	private static long startupTime = 0;

	public static void serverStarted() {
		if (startupTime == 0) {
			startupTime = System.nanoTime();
		}
	}

	private static String nanosDurationPrettyPrint(long duration) {
		long days = NANOSECONDS.toDays(duration);
		long hours = NANOSECONDS.toHours(duration);
		long minutes = NANOSECONDS.toMinutes(duration);
		long seconds = NANOSECONDS.toSeconds(duration);
		
		String durationStr = "";
		
		if(days > 0) {
			durationStr += days + " Days ";
		}
		
		if(hours > 0) {
			durationStr += hours + " Hours ";
		}
		
		if(minutes > 0) {
			durationStr += minutes + " Minutes ";
		}
		
		if(seconds > 0) {
			durationStr += seconds + " Seconds ";
		}
		
		return durationStr;
	}
	
	public static String getStatsAsJSON() {
		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = allocatedMemory - freeMemory;
		long currentTime = System.nanoTime();
		long timeElapsed = currentTime - startupTime;
		
		return "{" +
				"\"duration\":\"" + nanosDurationPrettyPrint(timeElapsed) + "\"," +
				"\"memory\":\"" + bytesToMegabytes(usedMemory) + " MB\"" +
				"}";
	}

}
