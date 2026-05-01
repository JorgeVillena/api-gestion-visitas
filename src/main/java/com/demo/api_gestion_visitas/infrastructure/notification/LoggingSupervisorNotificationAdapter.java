package com.demo.api_gestion_visitas.infrastructure.notification;

import com.demo.api_gestion_visitas.application.port.SupervisorNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingSupervisorNotificationAdapter implements SupervisorNotificationPort {
    private static final Logger log = LoggerFactory.getLogger(LoggingSupervisorNotificationAdapter.class);

    @Override
    public void notifyVisitReadyForReview(Long supervisorUserId, Long visitId, String placeName, String promoterName, String fcmToken) {
        log.info("[FCM stub] supervisorUserId={} visitId={} place={} promoter={} tokenPresent={}",
                supervisorUserId, visitId, placeName, promoterName, fcmToken != null && !fcmToken.isBlank());
    }
}
