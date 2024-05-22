package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.bh.cp.proxy.constants.ProxyConstants;

class DateUtilityTest {

	@InjectMocks
	DateUtility dateUtility;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetFromAndToDatesUTC() {
		dateUtility = new DateUtility();
		String[] outputDates = DateUtility.getFromAndToDatesUTC(ProxyConstants.DATE_RANGE_3M);
		String[] outputDates2 = DateUtility.getFromAndToDatesUTC("YTD");
		String[] outputDates3 = DateUtility.getFromAndToDatesUTC("1Y");
		String[] outputDates4 = DateUtility.getFromAndToDatesUTC("All");
		String[] outputDates5 = DateUtility.getFromAndToDatesUTC("Custom");
		dateUtility.customDateDifference("2024-03-25 0:0:0","2024-03-25 0:0:0");
		dateUtility.convertDateToUTC("02-03-2025 00:00:00");
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy").withZone(ZoneId.of("UTC"));
//		System.out.println(outputDates[0]);
//		System.out.println(outputDates[1]);
//		ChronoUnit.DAYS.between(null, null);
//		Period period = Period.between(LocalDate.parse(outputDates[1].split("\s")[0], dtf),
//				LocalDate.parse(outputDates[1].split("\s")[0], dtf));
		assertEquals(2, outputDates.length);
		assertEquals(2, outputDates2.length);
		assertEquals(2, outputDates3.length);
		assertEquals(2, outputDates4.length);
		assertEquals(2, outputDates5.length);
//		assertEquals(90, Math.abs(period.getDays()));
	}

}
