package main.chat.classes;

/**
 * Created by vlad on 02.06.2016.
 */
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private long id;

    private String text;

    private String author;

    private Date timestamp;

    private Boolean isDeleted;

    public Message(long id, String text, String author) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.timestamp = new Date();
        this.isDeleted = false;
    }

    public Message(long id, String text, String author, String timestamp, Boolean isDeleted) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.timestamp = new Date(new Long(timestamp));
        this.isDeleted = isDeleted;
    }

    public Message(String msg, long timestamp) {
        String[] splitMsg = msg.split(" ");
        String id = String.valueOf((long) (new Date().getTime() * Math.random()));
        this.text = "";
        for (int i = 1; i < splitMsg.length; i++) {
            this.text += splitMsg[i] + " ";
        }
        this.author = splitMsg[0].substring(1, splitMsg[0].length() - 1);
        this.id = new Long(id);
        this.timestamp = new Date(timestamp);
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", id);
        jsonObject.put("author", author);
        jsonObject.put("text", text);
        jsonObject.put("timestamp", new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(timestamp));
        jsonObject.put("isDeleted", isDeleted);

        return jsonObject.toJSONString();
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getStingTimestamp() {
        return String.valueOf(timestamp.getTime());
    }

    public void setText(String text) {
        this.text = text;
    }
}
