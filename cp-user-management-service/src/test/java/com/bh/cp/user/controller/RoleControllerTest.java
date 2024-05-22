package com.bh.cp.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bh.cp.user.dto.response.RoleResponseDTO;
import com.bh.cp.user.pojo.Role;
import com.bh.cp.user.service.RoleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

class RoleControllerTest {

	@InjectMocks
	private RoleController roleController;

	@Mock
	private MockMvc mockMvc;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private RoleService rolesService;

	private Role role;

	private List<String> privileges;

	private RoleResponseDTO roleResponseDto;

	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(roleController).build();

		roleResponseDto = new RoleResponseDTO("test_id", "test_role");
		privileges = new ArrayList<>();
		privileges.add("test-privilege");
		role = new Role();
		role.setClientRole(true);
		role.setId("test-id");
		role.setName("test-name");
		role.setPrivileages(privileges);
	}

	@Test
	void testGetAllRoles() throws Exception {
		when(rolesService.getAllRoles(any(HttpServletRequest.class))).thenReturn(List.of(roleResponseDto));
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/roles")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(List.of(roleResponseDto))));
	}

	@Test
	void testGetPrivilegesForRole() throws Exception {
		when(rolesService.getPrivilegesForRole(any(HttpServletRequest.class),anyString())).thenReturn(roleResponseDto);
		mockMvc.perform(MockMvcRequestBuilders.get("/v1/roles/{roleId}","test_id")
				).andExpectAll(MockMvcResultMatchers.status().isOk(),
						MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
						MockMvcResultMatchers.content().string(objectMapper
								.writeValueAsString(roleResponseDto)));
	}

	@Test
	void testCreateRole() throws JsonProcessingException, Exception {
		   when(rolesService.createRoleAssociatePrivileges(any(Role.class))).thenReturn(role);
	        mockMvc.perform(MockMvcRequestBuilders.post("/v1/roles/").header("Authorization", "Bearer abc")
	        		.content("{\"id\": null,\"name\": \"test-sanjay04\",\"clientRole\": true,\"privileages\": [\"test1\"]}")
					.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpectAll(MockMvcResultMatchers.status().isOk(),
							MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE),
							MockMvcResultMatchers.content()
									.string(new ObjectMapper().writeValueAsString(role)));
	        verify(rolesService).createRoleAssociatePrivileges(any(Role.class));
	}

	@Test
	void testUpdateRole() throws JsonProcessingException, Exception {
		   when(rolesService.updateRoleAssociatePrivileges(any(Role.class))).thenReturn(new ResponseEntity<Object>(HttpStatus.CREATED));
	        mockMvc.perform(MockMvcRequestBuilders.put("/v1/roles/").header("Authorization", "Bearer abc")
	        		.content("{\"id\": null,\"name\": \"test-sanjay04\",\"clientRole\": true,\"privileages\": [\"test1\"]}")
	        		.contentType(MediaType.APPLICATION_JSON_VALUE))
	        	      .andExpect(status().isCreated());
	}

	@Test
	void testDeleteRole() throws JsonProcessingException, Exception {
		   when(rolesService.deleteRole(any(String.class))).thenReturn(new ResponseEntity<Object>(HttpStatus.NO_CONTENT));
	        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/roles/1").header("Authorization", "Bearer abc")
	        		.contentType(MediaType.APPLICATION_JSON_VALUE))
	        	      .andExpect(status().isNoContent());
	}

}
