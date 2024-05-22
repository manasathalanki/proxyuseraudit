package com.bh.cp.proxy.handler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bh.cp.proxy.aop.AuditTrailAspect;
import com.bh.cp.proxy.exception.ProxyException;
import com.bh.cp.proxy.pojo.AuditDate;

public abstract class JsonResponseHandler<T> implements ResponseHandler<T> {

	private static final Logger logger = LoggerFactory.getLogger(JsonResponseHandler.class);

	@Autowired
	private AuditTrailAspect auditTrailAspect;

	private T t;

	public void setT(T t) {
		this.t = t;
	}

	public T getT() {
		return t;
	}

	protected JsonResponseHandler(T obj) {
		this.t = obj;
	}

	@Override
	public Object format(T responseJson, Map<String, Object> request) throws ProxyException {
		Timestamp startTime = Timestamp.valueOf(LocalDateTime.now());
		boolean statusFlag = true;
		try {
			setT(responseJson);
		} catch (Exception e) {
			statusFlag = false;
			logger.error(e.getMessage());
		}
		try {
			return parse(request);
		} finally {
			if (!request.containsValue(-1)) {
				Timestamp endTime = Timestamp.valueOf(LocalDateTime.now());
				long executionTime = endTime.getTime() - startTime.getTime();
				auditTrailAspect.saveAuditTrailPerformance(
						(new StringBuilder(this.getClass().getCanonicalName()).append(".")
								.append(new Throwable().getStackTrace()[0].getMethodName())).toString(),
						null, new AuditDate(startTime, endTime, executionTime), statusFlag);
			}
		}
	}

	protected abstract Object parse(Map<String, Object> request);
}
