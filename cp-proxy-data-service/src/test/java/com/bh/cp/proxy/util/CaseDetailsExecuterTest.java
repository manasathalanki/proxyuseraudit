package com.bh.cp.proxy.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.bh.cp.proxy.entity.ServicesDirectory;
import com.bh.cp.proxy.repository.ServicesDirectoryRepository;

import reactor.core.publisher.Mono;

class CaseDetailsExecuterTest {

	@InjectMocks
	private CaseDetailsExecuter handler;

	@Mock
	private WebClient webClient;
	@Mock
	private WebClient.RequestBodyUriSpec uriSpec;
	@Mock
	private WebClient.RequestBodyUriSpec headerSpec;

	@Mock
	private ServicesDirectoryRepository servicesDirectoryRepository;

	@Mock
	@SuppressWarnings("rawtypes")
	private WebClient.RequestHeadersSpec requestHeaderSpecMock;

	@Mock
	private WebClient.RequestBodyUriSpec requestHeadersUriSpecMock;

	@Mock
	private WebClient.RequestBodySpec requestBodySpecMock;

	@Mock
	private WebClient.ResponseSpec responseSpecMock;

	List<String> caseEdit;
	Map<String, String> InputData = new HashMap<>();
	Integer serviceId;
	String field;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		serviceId = 52;
		field = "vid";

		ReflectionTestUtils.setField(handler, "servicesDirectoryRepository", servicesDirectoryRepository);

	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Parse the Response - Giving response with Proper Output  for CommentList Edit")
	void testParse_EditCaseCommentsList() throws Exception {

		ServicesDirectory servicesDirectoryDB = new ServicesDirectory();
		servicesDirectoryDB.setUri("https://mercurius.np-0000029.npaeuw1.bakerhughes.com/cases/v1/editComment");

		caseEdit = new ArrayList<>();

		String JsonObject = "{\"caseId\":166761781,\"commentType\":\"FBKSITE\",\"action\":\"ADD\",\"commentDesc\":\"testingcomments\",\"userType\":\"CUSTOMER\",\"user\":\"kumuda.kurli@bakerhughes.com\",\"commentVisible\":\"Y\"}";

		caseEdit.add(JsonObject);

		String expectedResponse = "{\"issueId\":166761781,\"commentId\":2815,\"message\":\"Success\"}";

		when(servicesDirectoryRepository.findById(serviceId)).thenReturn(Optional.of(servicesDirectoryDB));

		when(webClient.post()).thenReturn(requestHeadersUriSpecMock);
		when(requestHeadersUriSpecMock.uri(Mockito.any(String.class))).thenReturn(requestBodySpecMock);
		when(requestBodySpecMock.contentType(Mockito.any())).thenReturn(requestBodySpecMock);
		when(requestBodySpecMock.bodyValue(Mockito.any())).thenReturn(requestHeaderSpecMock);
		when(requestHeaderSpecMock.retrieve()).thenReturn(responseSpecMock);
		when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
				.thenReturn(Mono.just(expectedResponse));

		List<String> result = handler.execute(caseEdit, serviceId);
		assertNotNull(result);

	}

}
