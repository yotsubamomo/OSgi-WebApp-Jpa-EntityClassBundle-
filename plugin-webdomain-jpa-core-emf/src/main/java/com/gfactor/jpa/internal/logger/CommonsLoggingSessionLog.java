package com.gfactor.jpa.internal.logger;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonsLoggingSessionLog extends AbstractSessionLog {
	public static final Logger LOG = LoggerFactory.getLogger(CommonsLoggingSessionLog.class);

	public void log(SessionLogEntry sessionLogEntry) {
		switch (sessionLogEntry.getLevel()) {
		case SEVERE:
			LOG.error(sessionLogEntry.getMessage());
			break;
		case WARNING:
			LOG.warn(sessionLogEntry.getMessage());
			break;
		case INFO:
			LOG.info(sessionLogEntry.getMessage());
			break;
		default:
			LOG.debug(sessionLogEntry.getMessage());
		}
	}
}
