package com.rodions.telenest;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import retrofit.RetrofitError;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rodion on 26.12.15.
 */
public class TelegramService {

    private static final Logger LOGGER = Logger.getLogger(TelegramService.class.getName());

    private int start;
    private TelegramBot bot;

    private NestRegistry registry;

    public TelegramService(Properties props, NestRegistry registry) {
        this.registry = registry;
        this.start = Integer.parseInt(props.getProperty("testuser.telegram.lastmessage.id"));
        this.bot = TelegramBotAdapter.build(props.getProperty("telegram.token"));
    }

    public void start() {
        int delta = 0;
        while (true) {
            try {
                GetUpdatesResponse updates = bot.getUpdates(start + delta, 10, 60);
                delta += updates.updates().size();
                for (Update update : updates.updates()) {
                    Message msg = update.message();
                    Chat chat = msg.chat();
                    String txt = msg.text();

                    LOGGER.log(Level.INFO, "update: " + update.updateId() + " chat: " + chat.id() +
                            " user: " + msg.from().id() + " text: " + txt);
                    processCommand(txt, msg.from().id());

                    // This is horrible but we need this because of polling telegram, better way is to setup webhook.
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARNING, "unexpected interrupt", e);
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (RetrofitError e) {
                LOGGER.log(Level.SEVERE, "unexpected exception", e);
            }
        }
    }

    public void processCommand(String command, Integer from) {
        if ("/help".equals(command) || "/start".equals(command)) {
            bot.sendMessage(from, "Available commands are:\n" +
                            "/status - to get thermostat status\n" +
                            "/adjust target - adjust thermostat to target degree"
            );

        } else if ("/status".equals(command)) {
            NestUserService userService = registry.getUserService(from);
            List<NestThermostat> thermostats = userService.getThermostats();
            String msg = getStatusMessage(thermostats);
            bot.sendMessage(from, msg);
        } else if (command.startsWith("/adjust")) {
            String[] parts = command.split("\\s+");
            if (parts.length >= 2) {
                Integer value = Integer.parseInt(parts[1]);
                NestUserService userService = registry.getUserService(from);
                String result = userService.adjustThermostat(value);
                bot.sendMessage(from, result);
            } else {
                bot.sendMessage(from, "/adjust command takes one number argument");
            }

        } else {
            bot.sendMessage(from, "Unknown command, please send '/help' to get a commands list");
        }
    }

    private String getStatusMessage(List<NestThermostat> thermostats) {
        if (thermostats == null) {
            return "No information available, please try again later, you can also contact our support team";
        } else {
            StringBuilder sb = new StringBuilder("Your thermostats:\n");
            for (NestThermostat thermostat : thermostats) {
                sb.append(thermostat.toString()).append('\n');
            }
            return sb.toString();
        }
    }

}
