package Chat.ChatServer.src.main.java.chat.Searcher;

import Chat.ChatServer.src.main.java.chat.Message;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by vlad on 03.06.2016.
 */
public class Searcher {

    List<Message> history;

    public Searcher(List<Message> history) {
        this.history = history;
    }

    public int searchByAuthor(String author) throws IOException {
        int n = 0;
        System.out.println("All " + author + "'s messages:");
        for(Message msg : history) {
            if(msg.getAuthor().compareTo(author) == 0) {
                System.out.println(msg);
                n++;
            }
        }
        return n;
    }

    public int searchByKeyword (String keyword) throws IOException {
        int n = 0;
        System.out.println("All messages with \"" + keyword + "\":");
        for (Message msg : history) {
            if(msg.getText().contains(keyword)) {
                System.out.println(msg);
                n++;
            }
        }
        return n;
    }

    public int searchByRegEx(String regex) throws IOException {
        int n = 0;
        System.out.println("All messages that match ^" + regex + "?:");
        for (Message msg : history) {
            if(msg.getText().substring(2, msg.getText().length() - 2).matches(regex)) {
                System.out.println(msg);
                n++;
            }
        }
        return n;
    }

    public int searchByTimePeriod(String period) throws IOException {
        int n = 0;
        String[] splitPeriod = period.split(" |\\.|:|-", -1);
        Date startDate = new Date(new Integer(splitPeriod[6]) - 1900, new Integer(splitPeriod[5]) - 1, new Integer(splitPeriod[4]),
                new Integer(splitPeriod[1]), new Integer(splitPeriod[2]), new Integer(splitPeriod[3]));
        Date endDate = new Date(new Integer(splitPeriod[14]) - 1900, new Integer(splitPeriod[13]) - 1, new Integer(splitPeriod[12]),
                new Integer(splitPeriod[9]), new Integer(splitPeriod[10]), new Integer(splitPeriod[11]));

        for(Message msg : history) {
            if(msg.getTimestamp().compareTo(startDate) >= 0 && msg.getTimestamp().compareTo(endDate) <= 0) {
                System.out.println(msg);
                n++;
            }
        }
        return n;
    }
}
