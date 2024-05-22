package com.bh.cp.proxy.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bh.cp.proxy.handler.ResponseHandler;
import com.bh.cp.proxy.pojo.ServicesDirectory;

@Component
public class ResponseHandlerFactory {

	private ApplicationContext context;

	public ResponseHandlerFactory(@Autowired ApplicationContext context) {
		super();
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public ResponseHandler<Object> getInstanceOf(ServicesDirectory service)
			throws ClassNotFoundException, IllegalArgumentException, SecurityException {
		return (ResponseHandler<Object>) context.getBean(Class.forName(service.getOutputHandler()));
	}
}
