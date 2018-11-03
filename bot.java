import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.File;
import sun.security.krb5.Config;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Bot extends TelegramLongPollingBot{

    String pathToSaveFolder = "D://TempFiles//";   //����� ���� ����������� ����� �� ������

    String botToken = "731541418:AAFHv_7fzbV05vlFy4BBwwzyE2VNhB0okQc";  //���������� ����� ���� ��� �������������
    String botName = "ScannerPrinter_bot";

    String noticePrint = "���������� �� ������";
    String noticeHelp = "��������� ����� ��� �������� �����  ��� �������� �� �������"
                      + " ��� ������� /����������� ��� ��������� ��������������� ������";
    String noticeScan = "�������������";

    static int idTxtFile = 1;  //�����-����� ����������� txt ������ (���������������� � ������ �����)

    public static void main(String[] args){

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try{
            telegramBotsApi.registerBot(new Bot());
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {   //������������ ���������� ���������
                                                    //��� ���������
        Message message = update.getMessage();

        if(message != null && message.hasText()){  //���� ��������� � ��������� ���� �����

             switch (message.getText()){

                 case "/������":      sendMsg(message,noticeHelp); break;
                 case "/�����������": scan(message);               break;
                 default:             print(message);              return;
             }
        }

        if(message != null && message.hasDocument()){  //���� � ��������� �� ���� ������, �� ���� �����
            print(message);
        }
    }

    private void print(Message message){   //���������

        if(message != null && message.hasDocument()){  //���� � ��������� ���� �����
            saveFile(message);
        }

        if(message != null && message.hasText()){  //���� � ��������� ���� �����
             createTxtFile(message);
        }

        if(message != null && (message.hasDocument() || message.hasText())){
            sendMsg(message,noticePrint);
        }
    }

    private void scan(Message message){  //������������

    }

    public void saveFile(Message message){  //��������� ����

        System.out.print(message.getDocument().getMimeType());

        GetFile getFileRequest = new GetFile();
        getFileRequest.setFileId(message.getDocument().getFileId());

        try{

            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileRequest);
            java.io.File upFile = downloadFile(telegramFile);
            java.io.File newFile = new java.io.File(pathToSaveFolder+message.getDocument().getFileName());
            upFile.renameTo(newFile);

        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private  void createTxtFile(Message message) {  //��������� .txt ���� �� ������

        while((new java.io.File(pathToSaveFolder+"//"+idTxtFile+".txt")).exists()){
            ++idTxtFile;
        }

        java.io.File file = new java.io.File(pathToSaveFolder+"//"+idTxtFile+".txt");

        try{

            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(message.getText());
            writer.flush();
            writer.close();
            ++idTxtFile;

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendMsg(Message message, String text){  //��������������� �����
                                                        //��� �������� ��������� ������������
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try{
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public  void setButtons(SendMessage sendMessage){ //������������� ����������

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);  //�������� �� ���������� ����� �������������

        //������� ������
        List<KeyboardRow> keyboars = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();   //������ ��� ������

        keyboardFirstRow.add(new KeyboardButton("/������"));
        keyboardFirstRow.add(new KeyboardButton("/�����������"));

        keyboars.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboars);
    }

    @Override
    public String getBotUsername() {  return botName; }

    @Override
    public String getBotToken() {
        return botToken;
    }
}