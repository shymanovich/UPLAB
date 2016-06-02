package Chat.ChatServer.src.main.java.chat.fileworker;

/**
 * Created by vlad on 03.06.2016.
 */

import Chat.ChatServer.src.main.java.chat.Message;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;




public class FileWorker {

    private List<Message> history;

    public FileWorker(List<Message> history) {
        this.history = history;
    }

    public void loadHistory() {
        Scanner scanner;
        JSONParser parser = new JSONParser();
        try {
            scanner = new Scanner(new File("history.txt").getAbsoluteFile());
            while (scanner.hasNext()) {
                JSONObject jsonObject = (JSONObject) parser.parse(scanner.nextLine());
                history.add(new Message((Long)jsonObject.get("id"), (String)jsonObject.get("text"),
                        (String)jsonObject.get("author"), (String)jsonObject.get("timestamp")));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveHistory() {
        File outFile = new File("history.txt");
        try {
            outFile.createNewFile();
            PrintWriter out = new PrintWriter(outFile.getAbsoluteFile());
            for(Message msg : history) {
                out.println(createJsonMessageObject(msg));
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonObject createJsonMessageObject(Message message) {
        return Json.createObjectBuilder()
                .add("id", message.getId())
                .add("text", message.getText())
                .add("author", message.getAuthor())
                .add("timestamp", message.getStingTimestamp())
                .build();
    }
}
