package com.template.api.configuration.logging;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggableEntityListener {
    private static final String LOG_PATTERN = "{} action for {}#={} {}";

    @PrePersist
    public void onPrePersist(Object object) {
        if (object instanceof LoggableEntity entity) {
            logAction(entity, LoggableAction.INSERT);
        }
    }

    @PreUpdate
    public void onPreUpdate(Object object) {
        if (object instanceof LoggableEntity entity) {
            logAction(entity, LoggableAction.UPDATE);
        }
    }

    @PreRemove
    public void onPreRemove(Object object) {
        if (object instanceof LoggableEntity entity) {
            logAction(entity, LoggableAction.DELETE);
        }
    }

    private void logAction(LoggableEntity entity, LoggableAction action) {
        log.info(LOG_PATTERN, action, entity.getClass().getSimpleName(), entity.getId(), entity.getLogBody());
    }

    enum LoggableAction {
        INSERT,
        UPDATE,
        DELETE
    }
}