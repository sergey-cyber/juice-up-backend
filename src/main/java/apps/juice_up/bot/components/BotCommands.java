package apps.juice_up.bot.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info"),
            new BotCommand("/enable", "notification activation")
    );

    String HELP_TEXT = """
            This bot will send notifications from 'juice up' application. The following commands are available to you:

            /start - start the bot
            /help - help menu
            /enable - notification activation""";
}