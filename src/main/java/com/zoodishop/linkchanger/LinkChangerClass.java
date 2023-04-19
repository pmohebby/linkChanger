package com.zoodishop.linkchanger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoodishop.linkchanger.dto.VmessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
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
                if (messageText.startsWith("vless") || messageText.startsWith("trojan")) {
                    var host = messageText.substring(messageText.lastIndexOf("@") + 1, messageText.indexOf(":"));
                    if (host.equals("server1.zoodishop.com")) {
                        messageText = messageText.replace("server1.zoodishop.com", "antispam.itresaneh.tk");
                    }
                    if (host.equals("server2.zoodishop.com")) {
                        messageText = messageText.replace("server2.zoodishop.com", "khabar.itresaneh.tk");
                    }
                    if (host.equals("server3.zoodishop.com")) {
                        messageText = messageText.replace("server3.zoodishop.com", "mostanad.itresaneh.tk");
                    }
                    if (host.equals("server4.zoodishop.com")) {
                        messageText = messageText.replace("server4.zoodishop.com", "qozaresh.itresaneh.tk");
                    } else {
                        messageText = ":(";
                    }
                    sendMessageText(messageText, userId);
                }
                if (messageText.startsWith("vmess")) {
                    var str = messageText.substring(messageText.lastIndexOf("://") + 3);
                    byte[] decoded = Base64.getDecoder().decode(str);
                    String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                    VmessDto vmessDto = ConvertJsonToModelVmess(decodedStr);
                    var subDomain = vmessDto.getAdd();
                    if (subDomain.equals("server1.zoodishop.com")) {
                        decodedStr = decodedStr.replace("server1.zoodishop.com", "antispam.itresaneh.tk");
                    }
                    if (subDomain.equals("server2.zoodishop.com")) {
                        decodedStr = decodedStr.replace("server2.zoodishop.com", "khabar.itresaneh.tk");
                    }
                    if (subDomain.equals("server3.zoodishop.com")) {
                        decodedStr = decodedStr.replace("server3.zoodishop.com", "mostanad.itresaneh.tk");
                    }
                    if (subDomain.equals("server4.zoodishop.com")) {
                        decodedStr = decodedStr.replace("server4.zoodishop.com", "qozaresh.itresaneh.tk");
                    }
                    byte[] encoded = Base64.getEncoder().encode(decodedStr.getBytes());
                    sendMessageText("vmess://" + new String(encoded, StandardCharsets.UTF_8), userId);
                }
            }
        }
    }

    private VmessDto ConvertJsonToModelVmess(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, new TypeReference<VmessDto>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        return System.getenv("bot_username");
    }

    @Override
    public String getBotToken() {
        return System.getenv("bot_token");
    }
}
