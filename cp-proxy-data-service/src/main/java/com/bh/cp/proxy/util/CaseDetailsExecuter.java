package com.bh.cp.proxy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bh.cp.proxy.repository.ServicesDirectoryRepository;

import reactor.core.publisher.Mono;

@Component
public class CaseDetailsExecuter {

	private WebClient webclient;

	private ServicesDirectoryRepository servicesDirectoryRepository;

	public CaseDetailsExecuter(@Autowired WebClient webclient,
			@Autowired ServicesDirectoryRepository servicesDirectoryRepository) {
		super();
		this.webclient = webclient;
		this.servicesDirectoryRepository = servicesDirectoryRepository;
	}

	List<String> execute(List<String> caseEdit, Integer serviceId) {

		List<String> response = new ArrayList<>();

		caseEdit.forEach(data -> response.add(getExecute(data, serviceId)));
		return response;
	}

	public String getExecute(Object data, Integer serviceId) throws SecurityException {

		String url = null;
		Optional<com.bh.cp.proxy.entity.ServicesDirectory> servicesDirectoryDB = servicesDirectoryRepository
				.findById(serviceId);

		if (!servicesDirectoryDB.isEmpty()) {

			url = servicesDirectoryDB.get().getUri();
		}

		String res = null;

		try {
			if (url != null) {
				res = webclient.post().uri(url).contentType(MediaType.APPLICATION_JSON).bodyValue(data).retrieve()
						.bodyToMono(String.class).onErrorResume(Mono::error).block();
			}
		} catch (Exception e) {
			res = "Internal Server Error. Please Contact support";
		}
		return res;
	}

}
