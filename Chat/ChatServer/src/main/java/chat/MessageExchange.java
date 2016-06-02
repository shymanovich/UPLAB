package Chat.ChatServer.src.main.java.chat;

/**
 * Created by vlad on 03.06.2016.
 */
import Chat.ChatServer.src.main.java.chat.Message;
import Chat.ChatServer.src.main.java.chat.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MessageExchange {

    private JSONParser jsonParser = new JSONParser();

    public String getToken(int index) {
        int number = index * 8 + 11;

        return "TN" + number + "EN";
    }

    public int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public String getServerResponse(List<Message> messages, int index, String type) {
        List<Message> chunk = messages.subList(index, messages.size());
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("messages", chunk);
        jsonObject.put("token", getToken(messages.size()));
        jsonObject.put("type", type);

        return jsonObject.toJSONString();
    }

    public Message getClientMessage(InputStream inputStream) throws Exception {
        JSONObject json = getJSONObject(inputStreamToString(inputStream));
        return new Message((Long)json.get("id"), (String)json.get("text"),
                (String)json.get("author"), (String)json.get("timestamp"));
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while((length = in.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        System.out.println("Input stream " + new String(baos.toByteArray()));
        return new String(baos.toByteArray());
    }

    public String getErrorMessage(String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", text);
        return  jsonObject.toJSONString();
    }
}