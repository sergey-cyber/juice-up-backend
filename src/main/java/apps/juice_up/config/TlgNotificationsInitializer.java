package apps.juice_up.config;

import apps.juice_up.bot.components.BotSkills;
import apps.juice_up.model.TlgNotificationDTO;
import apps.juice_up.service.TlgNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
     * Check all telegram notifications in database
     * If notifications in overdue, send message right now
     * Else, tune the send timer
     * When a message sent, a notification will be removed from db
 * */

@Slf4j
@Component
public class TlgNotificationsInitializer {

    @Autowired
    private BotSkills botSkills;
    @Autowired
    private TlgNotificationService tlgNotificationService;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            var notifications = tlgNotificationService.findAll();
            for(TlgNotificationDTO notice : notifications) {
                if(isOverdue(notice)) {
                    botSkills.sendMessage(notice.getMessage(), notice.getRecipientId());
                    tlgNotificationService.delete(notice.getId());
                } else {
                    botSkills.sendMessageToSpecificTime(notice, notice.getRecipientId());
                }
            }
            log.info("Telegram notifications installed");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean isOverdue(TlgNotificationDTO notice) {
        var currentDate = new Date();
        return notice.getExecuteTimestamp().before(currentDate);
    }
}
