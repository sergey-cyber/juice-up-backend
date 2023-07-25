package apps.juice_up.bot.components;

import apps.juice_up.bot.ApplicationContextProvider;
import apps.juice_up.bot.TelegramNotificationBot;
import apps.juice_up.model.TlgNotificationDTO;
import apps.juice_up.repos.UserRepository;
import apps.juice_up.service.TlgNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Component
public class BotSkills {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TlgNotificationService tlgNotificationService;

    public void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(Buttons.inlineMarkup());

        sendMessage(message);
    }

    public void sendHelpText(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotCommands.HELP_TEXT);

        sendMessage(message);
    }

    public void enableNotificationInfo(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Input your user_name from 'juice up' application. " +
                "The name must begin with the @ symbol");

        sendMessage(message);
    }

    public void activate(long chatId, String forUser) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        var user = userRepository.findByName(forUser.substring(1));
        if (user == null) {
            message.setText("Error: user with name " + forUser + " not found");
            sendMessage(message);
            return;
        }
        user.setTelegramId(chatId);
        userRepository.save(user);
        message.setText("Notification enabled for user with name " + forUser);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            TelegramNotificationBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(TelegramNotificationBot.class);
            telegramBot.execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    public void sendMessage(String text, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            TelegramNotificationBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(TelegramNotificationBot.class);
            telegramBot.execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private static class SendMessageTask extends TimerTask {
        private final TlgNotificationDTO tlgNotificationDTO;
        private final long chatId;
        private final TlgNotificationService tlgNotificationService;
        public SendMessageTask(TlgNotificationDTO tlgNotificationDTO, long chatId, TlgNotificationService tlgNotificationService) {
            this.tlgNotificationDTO = tlgNotificationDTO;
            this.chatId = chatId;
            this.tlgNotificationService = tlgNotificationService;
        }

        @Override
        public void run() {
            try {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(tlgNotificationDTO.getMessage());
                TelegramNotificationBot telegramBot = ApplicationContextProvider.getApplicationContext().getBean(TelegramNotificationBot.class);
                telegramBot.execute(message);
                tlgNotificationService.delete(tlgNotificationDTO.getId());
            } catch (TelegramApiException e){
                log.error(e.getMessage());
            }
        }
    }

    public void sendMessageToSpecificTime(TlgNotificationDTO tlgNotificationDTO, long chatId) {
        //Now create the time and schedule it
        Timer timer = new Timer();

        //Use this if you want to execute it once
        timer.schedule(new SendMessageTask(tlgNotificationDTO, chatId, tlgNotificationService), tlgNotificationDTO.getExecuteTimestamp());
        System.out.println("Timer installed in " + tlgNotificationDTO.getExecuteTimestamp());
        //Use this if you want to execute it repeatedly
        //int period = 10000;
        //timer.schedule(new MyTimeTask(), date, period );
    }
}
