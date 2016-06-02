package main.chat.classes;

/**
 * Created by vlad on 02.06.2016.
 */
import main.chat.classes.DBWorker;
import main.chat.classes.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MessageExchange {

    private JSONParser jsonParser = new JSONParser();

    public String getToken(int index) {
        int number = index * 8 + 11;

        return "TN" + number + "EN";
    }

    public int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
    }

    public String getServerResponse(DBWorker dbWorker, int indexMsg, int nMessages,
                                    List<Long> modifiedMsgs, int indexMod) {
        List<Message> modChunk = new ArrayList<>();
        List<Message> chunk = new ArrayList<>();
        Stack<Message> stack = dbWorker.getMessagesChunk(nMessages - indexMsg);
        int n = stack.size();
        for (int i = 1; i <= n; i++) {
            chunk.add(stack.pop());
        }
        for (int i = indexMod; i < modifiedMsgs.size(); i++) {
            modChunk.add(dbWorker.getMessage(modifiedMsgs.get(i)));
        }
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("messages", chunk);
        jsonObject.put("modMsgs", modChunk);
        jsonObject.put("tokenMsg", getToken(nMessages));
        jsonObject.put("tokenMod", getToken(modifiedMsgs.size()));

        return jsonObject.toJSONString();
    }

    public Message getClientMessage(InputStream inputStream) throws Exception {
        JSONObject json = getJSONObject(inputStreamToString(inputStream));
        return new Message(new Long(json.get("id").toString()), json.get("txt").toString(), json.get("author").toString());
    }

    public JSONObject getJSONObject(String json) throws ParseException {
        return (JSONObject) jsonParser.parse(json.trim());
    }

    public String inputStreamToString(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = in.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        System.out.println("Input stream " + new String(baos.toByteArray()));
        return new String(baos.toByteArray());
    }

    public String getErrorMessage(String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", text);
        return jsonObject.toJSONString();
    }
}