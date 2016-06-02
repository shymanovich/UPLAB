package Chat.ChatServer.src.main.java.chat;

/**
 * Created by vlad on 03.06.2016.
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private long id;

    private String text;

    private String author;

    private Date timestamp;

    public Message(long id, String text, String author, String timestamp) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.timestamp = new Date(new Long(timestamp));
    }

    public Message(String msg, long timestamp) {
        String[] splitMsg = msg.split(" ");
        String id = String.valueOf((long)(new Date ().getTime() * Math.random()));
        this.text = "";
        for(int i = 1; i < splitMsg.length; i++) {
            this.text += splitMsg[i] + " ";
        }
        this.author = splitMsg[0].substring(1, splitMsg[0].length() - 1);
        this.id = new Long(id);
        this.timestamp = new Date(timestamp);
    }

    @Override
    public String toString() {
        return id + " [" + author + "]: " + text + new SimpleDateFormat("hh:mm:ss dd.MM.yyyy").format(timestamp);
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

    public Date getTimestamp() { return timestamp; }

    public String getStingTimestamp() {
        return String.valueOf(timestamp.getTime());
    }

    public void setText(String text) {
        this.text = text;
    }
}
