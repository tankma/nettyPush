package com.easy.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtil {
	private  static Logger logger = LoggerFactory
	.getLogger(ExceptionUtil.class);

	public static String getDetailMessage(Throwable e) {
		StringWriter writer = new StringWriter();
		try {
			e.printStackTrace(new PrintWriter(writer));
			return writer.getBuffer().toString();
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception ex) {
					logger.error(ex.toString());
				}
		}
	}
}
