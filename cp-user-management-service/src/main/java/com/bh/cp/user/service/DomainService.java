package com.bh.cp.user.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.http.ResponseEntity;

import com.bh.cp.user.dto.response.DomainResponseDTO;
import com.bh.cp.user.exception.DeletionNotPermissableException;
import com.bh.cp.user.pojo.DomainAttribute;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;

public interface DomainService {

	List<DomainResponseDTO> getAllDomains(HttpServletRequest httpServletRequest);

	ResponseEntity<String> createDomain(GroupRepresentation request, HttpServletRequest httpServletRequest)
			throws JsonProcessingException;

	ResponseEntity<String> updateDomain(GroupRepresentation request, HttpServletRequest httpServletRequest)
			throws JsonProcessingException;

	ResponseEntity<GroupRepresentation> getDomain(String domainId, HttpServletRequest httpServletRequest)
			throws JsonProcessingException;

	Map<String, List<DomainAttribute>> viewDomain(String domainId, HttpServletRequest httpServletRequest)
			throws IOException;

	ResponseEntity<String> deleteDomain(String domainId, HttpServletRequest httpServletRequest)
			throws DeletionNotPermissableException, JsonProcessingException, InterruptedException, ExecutionException;

	Map<String, DomainResponseDTO> getDomainIdMap(HttpServletRequest httpServletRequest);

}
