package com.bh.cp.proxy.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.adapter.ServicesAdapter;
import com.bh.cp.proxy.adapter.impl.RestServicesAdapter;
import com.bh.cp.proxy.pojo.ServicesDirectory;

@Component
public class ServicesFactory {

	private RestServicesAdapter restServicesAdapter;

	public ServicesFactory(@Autowired RestServicesAdapter restServicesAdapter) {
		super();
		this.restServicesAdapter = restServicesAdapter;
	}

	public ServicesAdapter getInstanceOf(ServicesDirectory service) {
		return switch (service.getServiceType()) {
		case REST: {
			yield restServicesAdapter;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + service.getServiceType());
		};
	}

	static final String REST = "REST";
	static final String SOAP = "SOAP";
}
