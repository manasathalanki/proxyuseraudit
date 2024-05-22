package com.bh.cp.dashboard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.dashboard.dto.request.DeleteWidgetRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetOrderRequestDTO;
import com.bh.cp.dashboard.dto.request.WidgetsRequestDTO;
import com.bh.cp.dashboard.dto.response.WidgetsResponseDTO;
import com.bh.cp.dashboard.service.WidgetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class WidgetControllerTest {

	@Mock
	private WidgetService widgetService;

	@InjectMocks
	private WidgetController widgetController;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Mock
	private HttpServletRequest request;

	DeleteWidgetRequestDTO deleteRequestDTO;

	List<WidgetsResponseDTO> responseDTO;

	List<Integer> widgetIDs;

	private WidgetsRequestDTO widgetsDto;

	private WidgetOrderRequestDTO widgetDto;

	private WidgetsResponseDTO dto;

	private List<WidgetOrderRequestDTO> requestList;

	private List<WidgetsResponseDTO> responseList;

	@Autowired
	MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(widgetController).build();
		deleteRequestDTO = new DeleteWidgetRequestDTO();
		deleteRequestDTO.setLevel("projects");
		deleteRequestDTO.setType("kpi");
		widgetIDs = new ArrayList<>();
		widgetIDs.add(1);
		widgetIDs.add(2);
		deleteRequestDTO.setWidgetIds(widgetIDs);
		widgetsDto = new WidgetsRequestDTO();
		widgetDto = new WidgetOrderRequestDTO();
		widgetsDto.setLevel("plants");
		widgetsDto.setType("kpi");
		requestList = new ArrayList<>();
		widgetDto.setOrderNumber(1);
		widgetDto.setWidgetId(1);
		widgetDto.setChecked(true);
		requestList.add(widgetDto);
		widgetsDto.setWidgets(requestList);
		dto = new WidgetsResponseDTO();
		dto.setHasAccess(true);
		dto.setCustomizationId(1);
		dto.setOrderNumber(1);
		dto.setStaticImageId(1);
		dto.setTitle("Test");
		dto.setWidgetId(1);
		dto.setChecked(true);
		dto.setHasAccess(true);
		responseList = new ArrayList<>();
		responseList.add(dto);
	}

	@Test
	void testDeleteWidgets() throws Exception {
		when(widgetService.deleteWidgets(deleteRequestDTO, request)).thenReturn(responseDTO);
		mockMvc.perform(MockMvcRequestBuilders.delete("/v1/widgets/").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer abc").content(objectMapper.writeValueAsString(deleteRequestDTO))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void testAddWidgets() throws Exception {
		Mockito.lenient().when(widgetService.addWidgets(widgetsDto, request, false)).thenReturn(responseDTO);
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/widgets/").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer abc").content(objectMapper.writeValueAsString(widgetsDto))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void testReorderWidgets() throws Exception {
		Mockito.lenient().when(widgetService.addWidgets(widgetsDto, request, true)).thenReturn(responseDTO);
		mockMvc.perform(MockMvcRequestBuilders.patch("/v1/widgets/").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer abc").content(objectMapper.writeValueAsString(widgetsDto))
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void testGetAllWidgets() throws Exception {
		Mockito.lenient().when(widgetService.getAllWidgets("projects", "summary", 1, request)).thenReturn(responseDTO);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/widgets?level=projects&type=summary&customization-id=16")
				.contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer abc")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}
