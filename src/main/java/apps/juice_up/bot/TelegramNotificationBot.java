package apps.juice_up.bot;

import apps.juice_up.bot.components.BotCommands;
import apps.juice_up.bot.components.Buttons;
import apps.juice_up.repos.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static apps.juice_up.bot.components.BotCommands.LIST_OF_COMMANDS;

@Slf4j
@Component
public class TelegramNotificationBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    public TelegramNotificationBot(@Value("${bot.token}") String botToken) {
        super(botToken);
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "JuiceUpNotificationsBot";
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId = 0;
        String userName = null;
        String receivedMessage;

        //если получено сообщение текстом
        if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userName = update.getMessage().getFrom().getFirstName();

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, userName);
            }

            //если нажата одна из кнопок бота
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName);
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        System.out.println(receivedMessage);
        if(receivedMessage.startsWith("@")) {
            activate(chatId, receivedMessage);
            return;
        }
        switch (receivedMessage) {
            case "/start" -> startBot(chatId, userName);
            case "/help" -> sendHelpText(chatId);
            //TODO: add enable/disable actions. See: https://stackoverflow.com/questions/67774182/how-to-allow-java-telegram-bot-wait-user-reply
            case "/enable" -> enableNotificationInfo(chatId);
            default -> {
            }
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(Buttons.inlineMarkup());

        sendMessage(message);
    }

    private void sendHelpText(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotCommands.HELP_TEXT);

        sendMessage(message);
    }

    private void enableNotificationInfo(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Input your user_name from 'juice up' application. " +
                "The name must begin with the @ symbol");

        sendMessage(message);
    }

    private void activate(long chatId, String forUser) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        var user = userRepository.findByName(forUser.substring(1));
        if(user == null) {
            message.setText("Error: user with name " + forUser +  " not found");
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
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }
}
