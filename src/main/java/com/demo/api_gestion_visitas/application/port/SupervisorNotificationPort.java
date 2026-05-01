package com.demo.api_gestion_visitas.application.port;

/**
 * Push notification to supervisor when visit is closed by both actors (FCM stub or real).
 */
public interface SupervisorNotificationPort {
    void notifyVisitReadyForReview(Long supervisorUserId, Long visitId, String placeName, String promoterName, String fcmToken);
}
