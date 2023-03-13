package com.zoodishop.linkchanger;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class LinkChangerClass extends TelegramLongPollingBot {
    @Value("#{bot['com.example.bot.m.UserBotClass.joinChannelMsg']}")
    private String joinChannelMsg;
    @Value("#{bot['com.example.bot.m.UserBotClass.username_channel']}")
    private String username_channel;
    @Value("#{bot['com.example.bot.m.UserBotClass.sendStartMsg']}")
    private String sendStartMsg;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userId = update.getMessage().getFrom().getId();
            var messageText = update.getMessage().getText();
            var userRole = checkChannelMember(userId);
            if (messageText.equals("/start")) {
                if (!userRole.equals("kicked") && !userRole.equals("left") && !userRole.equals("restricted")) {
                    sendMessageText("سلام برای تغییر کانفیگ ، کانفیگت رو بفرست تا تغییر بدم و کانفیگ جدید برات بفرستم \n", userId);
                    onClosing();
                } else {
                    sendMessageText(joinChannelMsg + " \n ⚡️" + username_channel + "⚡️ \n" + sendStartMsg, userId);
                    onClosing();
                }
            }
            if (userRole.equals("member") || userRole.equals("administrator") || userRole.equals("creator")) {
                if (messageText.startsWith("vless")) {
                    messageText = messageText.replace("mamadbyavar.ir", "zoodishop.com");
                    sendMessageText(messageText, userId);
                }
                if (messageText.startsWith("vmess")) {
                    var str = messageText.substring(messageText.lastIndexOf("://") + 3);
                    byte[] decoded = Base64.getDecoder().decode(str);
                    String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                    decodedStr = decodedStr.replace("mamadbyavar.ir", "zoodishop.com");
                    byte[] encoded = Base64.getEncoder().encode(decodedStr.getBytes());
                    sendMessageText("vmess://" + new String(encoded, StandardCharsets.UTF_8), userId);
                }
            }
        }
    }

    @SneakyThrows
    public void sendMessageText(String text, Long userId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(userId));
        execute(sendMessage);
    }

    public String checkChannelMember(Long userId) throws TelegramApiException {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setUserId(userId);
        getChatMember.setChatId(username_channel);
        return execute(getChatMember).getStatus();
    }

    @Override
    public String getBotUsername() {
        return "ThunderVpn_ConfigChangerBot";
    }

    @Override
    public String getBotToken() {
        return "6024382997:AAHX45NzQ_8ZaU-iD5BeybDn7Kj7-uSr7H8";
    }
}
