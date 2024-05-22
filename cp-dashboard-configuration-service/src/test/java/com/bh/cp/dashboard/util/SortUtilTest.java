package com.bh.cp.dashboard.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bh.cp.dashboard.dto.response.LayoutWidgetResponseDTO;
import com.bh.cp.dashboard.entity.Widgets;

class SortUtilTest {

	@Test
	@DisplayName("Test sortBySpanWidth -- Check sorted list")
	void testSortBySpanWidth_Positive1() {

		List<LayoutWidgetResponseDTO> widgetResponseList = new ArrayList<>();
		AtomicInteger counter = new AtomicInteger(1);
		for (int i = 1; i < 10; i++) {
			if (i % 3 == 0) {
				counter = new AtomicInteger(1);
			}

			Widgets widgets = new Widgets();
			widgets.setId(i);
			widgets.setWidthSpan(i != 1 ? counter.getAndIncrement() : 4);
			LayoutWidgetResponseDTO responseDTO = new LayoutWidgetResponseDTO(widgets, null);
			responseDTO.setOrderNumber(i != 1 ? counter.getAndIncrement() : 4);
			widgetResponseList.add(responseDTO);
		}

		List<LayoutWidgetResponseDTO> output = SortUtil.sortBySpanWidth(widgetResponseList);
		assertEquals(3, output.get(0).getWidthSpan());
		assertEquals(1, output.get(output.size() - 1).getWidthSpan());
	}

}
