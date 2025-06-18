package mainpackage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
public class GmailReader {
    private static final String APPLICATION_NAME = "Gmail App";
    //"me"- авторизованный пользователь
    private static final String USER_ID = "me";
    public static void main(String[] args) {
        try {
            //scope — только чтение писем
            List<String> scopes = Collections.singletonList("https://www.googleapis.com/auth/gmail.readonly");
            //авторизация через класс Auth
            Credential credential = Auth.authorize(scopes, "gmail_reader");
            //API
            Gmail service = new Gmail.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            //получаем лист(список) писем из максимум 2
            ListMessagesResponse response = service.users().messages().list("me")
                    .setMaxResults(10L)
                    .execute();
            List<Message> messages = response.getMessages();
            if (messages == null || messages.isEmpty()) {
                System.out.println("Письмо не найдено.");
            } else {
                System.out.println("Последние письма:");
                for (Message message : messages) {
                    //получает письмо по id
                    Message fullMessage = service.users().messages().get(USER_ID, message.getId()).execute();
                    //получает тему письма
                    String subject = fullMessage.getPayload().getHeaders().stream()
                            .filter(h -> h.getName().equalsIgnoreCase("Subject"))
                            .map(MessagePartHeader::getValue)
                            .findFirst()
                            .orElse("(нет темы)");
                    System.out.println("- " + subject);
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при получении писем: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
