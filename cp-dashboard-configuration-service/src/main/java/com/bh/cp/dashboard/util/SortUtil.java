package com.bh.cp.dashboard.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import com.bh.cp.dashboard.dto.response.LayoutWidgetResponseDTO;

public class SortUtil {

	private SortUtil() {
		super();
	}

	public static List<LayoutWidgetResponseDTO> sortBySpanWidth(List<LayoutWidgetResponseDTO> widgetResponseList) {
		List<LayoutWidgetResponseDTO> spans = new ArrayList<>(widgetResponseList);
		List<LayoutWidgetResponseDTO> optimal = new ArrayList<>();
		Collections.sort(spans, Comparator.comparing(LayoutWidgetResponseDTO::getWidthSpan).reversed());
		int orderNumber = 1;
		for (int i = 0; i < spans.size(); i++) {
			LayoutWidgetResponseDTO dto = spans.get(i);
			dto.setOrderNumber(orderNumber++);
			Integer span = dto.getWidthSpan();
			switch (span) {
			case 2:
				optimal.add(dto);
				int indexOfOne = IntStream.range(0, spans.size()).filter(o -> spans.get(o).getWidthSpan().equals(1))
						.findFirst().orElse(-1);
				if (indexOfOne != -1) {
					LayoutWidgetResponseDTO dtoOne = spans.get(indexOfOne);
					dtoOne.setOrderNumber(orderNumber++);
					optimal.add(dtoOne);
					spans.remove(indexOfOne);
				}
				break;
			case 1, 3:
				optimal.add(dto);
				break;
			default:
				continue;
			}
		}
		return optimal;
	}
}
