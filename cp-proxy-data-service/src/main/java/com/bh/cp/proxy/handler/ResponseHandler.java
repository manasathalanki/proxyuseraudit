package com.bh.cp.proxy.handler;

import java.util.Map;

import com.bh.cp.proxy.exception.ProxyException;

public interface ResponseHandler<T> {
	public Object format(T responseJson, Map<String, Object> request) throws ProxyException;
}
