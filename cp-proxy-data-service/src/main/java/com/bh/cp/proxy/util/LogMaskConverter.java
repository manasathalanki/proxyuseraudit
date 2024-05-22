package com.bh.cp.proxy.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

public class LogMaskConverter extends CompositeConverter<ILoggingEvent> {

	@Override
	protected String transform(ILoggingEvent event, String in) {
		in = in.replaceAll("((?<='Bearer )[^']+(?='))", "****");
		in = in.replaceAll("(?s)((?<=')(\\[\\{)).*.\\}\\]+(?=')", "Asset Hierarchy of current User");
		return in;
	}

}