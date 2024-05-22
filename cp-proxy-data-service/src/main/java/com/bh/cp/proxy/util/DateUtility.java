package com.bh.cp.proxy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bh.cp.proxy.constants.ProxyConstants;

public class DateUtility {

	private static final Logger logger = LoggerFactory.getLogger(DateUtility.class);

	public static String[] getFromAndToDatesUTC(String dateRange) {
		return getFromAndToDatesUTC(DATE_FORMAT_UTC, dateRange);
	}

	public static String[] getFromAndToDatesYYYYMMDDUTC(String dateRange) {
		return getFromAndToDatesUTC(DATE_FORMAT_YYYYMMDD_UTC, dateRange);
	}

	public static String[] getFromAndToDatesYYYYMMDDHHMISSUTC(String dateRange) {
		return getFromAndToDatesUTC(DATE_FORMAT_YYYYMMDDHHMISS_UTC, dateRange);
	}

	public static String[] getFromAndToDatesYYYYMMDDTHHMISSZUTC(String dateRange) {
		return getFromAndToDatesUTC(DATE_FORMAT_YYYYMMDDTHHMISSZ_UTC, dateRange);
	}

	public static String[] getFromAndToDatesUTC(DateTimeFormatter formatter, String dateRange) {
		DateRanges dateRanges = DateRanges.valueOfLabel(dateRange);
		return switch (dateRanges) {
		case ALL: {
			yield new String[] { format(formatter, minDBDateTimeUTC()), format(formatter, nowUTC()) };
		}
		case CUSTOM: {
			yield new String[] { format(formatter, minDBDateTimeUTC()), format(formatter, nowUTC()) };
		}
		case THREE_MONTHS: {
			yield new String[] { format(formatter, addMonthsToNowUTC(-3)), format(formatter, nowUTC()) };
		}
		case SIX_MONTHS: {
			yield new String[] { format(formatter, addMonthsToNowUTC(-6)), format(formatter, nowUTC()) };
		}
		case YEAR_TO_DATE: {
			yield new String[] { format(formatter, firstDayOfYearUTC()), format(formatter, nowUTC()) };
		}
		case ONE_YEAR: {
			yield new String[] { format(formatter, addMonthsToNowUTC(-12)), format(formatter, nowUTC()) };
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + dateRanges);
		};
	}

	public static Instant firstDayOfYearUTC() {
		return Year.now(ZoneId.of("UTC")).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
	}

	public static Instant addMonthsToNowUTC(int months) {
		return LocalDateTime.ofInstant(nowUTC(), ZoneOffset.ofHours(0)).plus(months, ChronoUnit.MONTHS)
				.toInstant(ZoneOffset.ofHours(0));
	}

	public static Instant nowUTC() {
		return Clock.systemUTC().instant();
	}

	public static Instant minDBDateTimeUTC() {
		return new Date(544429800000L).toInstant().atZone(ZoneId.ofOffset("", ZoneOffset.UTC)).toInstant();
	}

	public static String nowFormattedUTC() {
		return DATE_FORMAT_UTC.format(nowUTC());
	}

	public static String formatUTC(Instant instant) {
		return format(DATE_FORMAT_UTC, instant);
	}

	public static String format(DateTimeFormatter formatter, Instant instant) {
		return formatter.format(instant);
	}

	public static final DateTimeFormatter DATE_FORMAT_UTC = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
			.withZone(ZoneId.of("UTC"));
	public static final DateTimeFormatter DATE_FORMAT_YYYYMMDD_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd")
			.withZone(ZoneId.of("UTC"));
	public static final DateTimeFormatter DATE_FORMAT_YYYYMMDDHHMISS_UTC = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
	public static final DateTimeFormatter DATE_FORMAT_YYYYMMDDTHHMISSZ_UTC = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));

	public enum DateRanges {
		THREE_MONTHS("3M"), SIX_MONTHS("6M"), YEAR_TO_DATE("YTD"), ONE_YEAR("1Y"), ALL("All"), CUSTOM("Custom");

		public final String label;

		private DateRanges(String label) {
			this.label = label;
		}

		public static DateRanges valueOfLabel(String label) {
			for (DateRanges dateRange : values()) {
				if (dateRange.label.equals(label)) {
					return dateRange;
				}
			}
			return null;
		}
	}

	public Map<String, Integer> customDateDifference(String startDate, String endDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		HashMap<String, Integer> dateMap = new HashMap<>();

		try {
			// Parse the strings to Date objects
			Date date1 = dateFormat.parse(startDate);
			Date date2 = dateFormat.parse(endDate);
			// Calculate the difference in milliseconds
			long differenceInMillis = date2.getTime() - date1.getTime();
			// Calculate the difference in days
			long daysDifference = differenceInMillis / (24 * 60 * 60 * 1000);
			// Calculate the difference in weeks
			long weeksDifference = daysDifference / 7;
			// Calculate the difference in months
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date1);
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);
			int monthsDifference = 0;
			while (cal1.before(cal2)) {
				cal1.add(Calendar.MONTH, 1);
				if (cal1.before(cal2)) {
					monthsDifference++;
				}
			}
			// Calculate the difference in years
			int yearsDifference = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
			// Calculate the difference in quarters
			int quartersDifference = (monthsDifference + 2) / 3;

			dateMap.put("days", (int) daysDifference);
			dateMap.put("weeks", (int) weeksDifference);
			dateMap.put("months", monthsDifference);
			dateMap.put("years", yearsDifference);
			dateMap.put("quarter", quartersDifference);

		} catch (ParseException e) {
			logger.info(e.getMessage());
		}
		return dateMap;

	}

	public ZonedDateTime convertDateToUTC(String suggestedDate) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ProxyConstants.DATE_FORMAT_FOR_UTC, Locale.ENGLISH);
		LocalDateTime localDateTime = LocalDateTime.parse(suggestedDate, formatter);
		ZoneId zoneId = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
		return zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
	}
	
	public static String convertStringDateToUTCDateFormat(String date)
	{
	    String dateFormat = "yyyy-MM-dd H:m:s";
	    LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateFormat));
	    ZonedDateTime systemZoneDateTime = localDateTime.atZone(ZoneId.systemDefault());
	    Instant utcTimestamp = systemZoneDateTime.toInstant();
	    return utcTimestamp.toString().replace("Z",".000Z");
	}
}