package com.bh.cp.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.GroupRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.pojo.DomainAttribute;
import com.bh.cp.user.service.GenericAssetHierarchyFilterService;
import com.bh.cp.user.service.impl.DomainServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class DomainControllerTest {

	@InjectMocks
	private DomainController domainController;

	@Mock
	private DomainServiceImpl domainService;

	@Mock
	private GenericAssetHierarchyFilterService assetHierarchyFilterService;
	
	@Autowired
	private MockMvc mockMvc;

	@Mock
	private HttpServletRequest httpServletRequest;

	private List<DomainResponseDTO> domains;

	private GroupRepresentation domain;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(domainController).build();
		domains = new ArrayList<>();
		domains.add(new DomainResponseDTO("test-domain-id", "test-domain"));
		domain = new GroupRepresentation();
		domain.setName("test-domain");
		domain.setId("test-domain-id");
	}

	@Test
	void testCreateDomain() throws JsonProcessingException, Exception {
		   when(domainService.createDomain(any(GroupRepresentation.class),any(HttpServletRequest.class))).thenReturn(new ResponseEntity<>(domain.toString(),HttpStatus.OK));
	        mockMvc.perform(MockMvcRequestBuilders.post("/v1/domains/").header("Authorization", "Bearer abc")
	        		.content("{ \"name\": \"test-domain\" }")
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
	}

	@Test
	void testUpdateDomain() throws JsonProcessingException, Exception {
		   when(domainService.updateDomain(any(GroupRepresentation.class),any(HttpServletRequest.class))).thenReturn(new ResponseEntity<>(domain.toString(),HttpStatus.OK));
	        mockMvc.perform(MockMvcRequestBuilders.post("/v1/domains/").header("Authorization", "Bearer abc")
	        		.content("{ \"id\": \"test-domain-id\", \"name\": \"test-domain\"}")
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
	}

	@Test
	void testGetAllDomains() throws JsonProcessingException, Exception {
		        when(domainService.getAllDomains(any(HttpServletRequest.class))).thenReturn(domains);
		        mockMvc.perform(MockMvcRequestBuilders.get("/v1/domains").header("Authorization", "Bearer abc")
						.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
								MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
								MockMvcResultMatchers.content()
										.string(new ObjectMapper().writeValueAsString(domains)));
		        verify(domainService).getAllDomains(any(HttpServletRequest.class));
	}

	@Test
	void testUpdateDomains() throws Exception {
		 when(domainService.getDomain("/domainId", null)).thenReturn(ResponseEntity.ok(new GroupRepresentation ()));
		 mockMvc.perform(MockMvcRequestBuilders.get("/v1/domains/domainId")
				 .contentType(MediaType.APPLICATION_JSON_VALUE))
	        		.andExpect(MockMvcResultMatchers.status().isOk());
		
	}

	@Test
	void testViewDomains() throws Exception {
		DomainAttribute domainAttribute1 = new DomainAttribute("domainAttribute1", "domainAttribute2", false);
		DomainAttribute domainAttribute2 = new DomainAttribute("domainAttribute1", "domainAttribute2", false);
		List<DomainAttribute> domainAttributelist = Arrays.asList(domainAttribute1, domainAttribute2);
		Map<String, List<DomainAttribute>> mockDomainData = new HashMap<>();
		mockDomainData.put("samplekey", domainAttributelist);
		when(domainService.getDomain("/domainId", null)).thenReturn(ResponseEntity.ok(new GroupRepresentation()));
		mockMvc.perform(
				MockMvcRequestBuilders.get("/v1/domains/domainId/view").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	void testdeleteDomain() throws Exception {
		 when(domainService.deleteDomain(any(String.class),any(HttpServletRequest.class) )).thenReturn(new ResponseEntity<>(domain.toString(),HttpStatus.OK));
		 mockMvc.perform(MockMvcRequestBuilders.delete("/v1/domains/1")
				 .contentType(MediaType.APPLICATION_JSON_VALUE))
	        		.andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	@Test
	void testupdateDomain() throws JsonProcessingException, Exception {
		   when(domainService.updateDomain(any(GroupRepresentation.class),any(HttpServletRequest.class))).thenReturn(new ResponseEntity<>(domain.toString(),HttpStatus.OK));
	        mockMvc.perform(MockMvcRequestBuilders.put("/v1/domains/").header("Authorization", "Bearer abc")
	        		.content("{ \"name\": \"test-domain\" }")
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());
	}
	
	@Test
	void testretrieveFullHierarchyByLevel() throws JsonProcessingException, Exception {
		String Level="lineup";
		   when(assetHierarchyFilterService.getAssetHierarchyByLevel(Level)).thenReturn(Level);
	        mockMvc.perform(MockMvcRequestBuilders.get("/v1/domains/hierarchy/lineup")
	        		 .contentType(MediaType.APPLICATION_JSON_VALUE))
    		.andExpect(MockMvcResultMatchers.status().isOk());
	}
}
