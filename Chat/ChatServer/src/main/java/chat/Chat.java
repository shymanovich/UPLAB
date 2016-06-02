package Chat.ChatServer.src.main.java.chat;

/**
 * Created by vlad on 03.06.2016.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Chat.ChatServer.src.main.java.chat.exceptions.BadFormatException;
import Chat.ChatServer.src.main.java.chat.fileworker.FileWorker;
import Chat.ChatServer.src.main.java.chat.Searcher.Searcher;

public class Chat {
    List<Message> history;
    FileWorker fileWorker;
    Searcher searcher;
    Logger logger;

    Chat() {
        history = new ArrayList<>();
        fileWorker = new FileWorker(history);
        loadHistory();
        createLogger();
        searcher = new Searcher(history);
    }

    private void createLogger() {
        logger = Logger.getLogger(this.getClass().getName());
        FileHandler fh;

        try {
            fh = new FileHandler("LogFile.log", true);
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String msg = scanner.nextLine();
            execute(msg);
        }
    }

    private void loadHistory() {
        fileWorker.loadHistory();
    }

    private void saveHistory() {
        fileWorker.saveHistory();
    }

    private void execute(String msg) {
        String[] commands = msg.split(" ");
        String badFormat = "Unknown message format.";
        try {
            switch (commands[0]) {
                case "/?":
                    if (commands.length == 1) {
                        logger.log(Level.INFO, "/? Display help");
                        displayHelp();
                    } else {
                        throw new BadFormatException(badFormat);
                    }
                    return;

                case "/h":
                    if (commands.length == 1) {
                        displayHistory();
                    } else {
                        throw new BadFormatException(badFormat);
                    }
                    return;

                case "/s":
                    if (commands.length == 1) {
                        saveHistory();
                        System.exit(0);
                    } else {
                        throw new BadFormatException(badFormat);
                    }
                    return;

                case "/d":
                    if (commands.length == 2) {
                        deleteMessage(new Long(commands[1]));
                    } else {
                        throw new BadFormatException(badFormat);
                    }
                    return;

                case "/sa":
                    if (commands.length == 2) {
                        searchByAuthor(commands[1]);
                    } else {
                        throw new BadFormatException(badFormat);
                    }
                    return;

                case "/sk":
                    if (commands.length == 2) {
                        searchByKeyword(commands[1]);
                    } else {
                        throw new BadFormatException(badFormat);
                    }
                    return;

                case "/sr":
                    searchByRegEx(msg.substring(4));
                    return;

                case "/hp":
                    if(msg.matches("/hp \\d{2}:\\d{2}:\\d{2} \\d{2}\\.\\d{2}\\.\\d{4} -" +
                            " \\d{2}:\\d{2}:\\d{2} \\d{2}\\.\\d{2}\\.\\d{4}")) {
                        searchByTimePeriod(msg);
                    }
                    return;

                default:
                    if (msg.matches("\\[.+\\] : .+")) {
                        history.add(new Message(msg, new Date().getTime()));
                        logger.log(Level.INFO, "Message added to the story");
                    } else {
                        logger.log(Level.WARNING, badFormat);
                        throw new BadFormatException(badFormat);
                    }
            }
        } catch (BadFormatException e) {
            logger.log(Level.WARNING, badFormat);
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println(badFormat);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private void displayHistory() throws IOException {
        logger.log(Level.INFO, "/h View history");
        System.out.println("History format: id [author] : message date");
        history.forEach(System.out::println);
    }

    private void deleteMessage(long id) throws IOException {
        logger.log(Level.INFO, "/d Deleting message id = " + id);
        for(Message msg : history) {
            if(msg.getId() == id) {
                history.remove(msg);
                System.out.println("Successful deletion.");
                logger.log(Level.INFO, "Successful deletion");
                return;
            }
        }
        System.err.println("Messages with this id not found.");
        logger.log(Level.INFO, "Message with this id not found");
    }

    private void searchByAuthor(String author) throws IOException {
        logger.log(Level.INFO, "/sa Search in the history of messages by author " + author);
        logger.log(Level.INFO, "Found " + searcher.searchByAuthor(author) + " messages");
    }

    private void searchByKeyword (String keyword) throws IOException {
        logger.log(Level.INFO, "/sk Search in the history of messages by keyword " + keyword);
        logger.log(Level.INFO, "Found " + searcher.searchByKeyword(keyword) + " messages");
    }

    private void searchByRegEx(String regex) throws IOException {
        logger.log(Level.INFO, "/sr Search in the history of messages by a regular expression ^" + regex);
        logger.log(Level.INFO, "Found " + searcher.searchByRegEx(regex) + " messages");
    }

    private void searchByTimePeriod(String period) throws IOException {
        logger.log(Level.INFO, "/sr View history of messages for a certain period " + period);
        logger.log(Level.INFO, "Found " + searcher.searchByTimePeriod(period) + " messages");
    }

    private void displayHelp () {
        System.out.println("/? - Display help.");
        System.out.println("/s - Save history and exit.");
        System.out.println("/h - View history of messages.");
        System.out.println("/d *id* - Delete the message by id.");
        System.out.println("/sa *author* - Search in the history of messages by author.");
        System.out.println("/sk *keyword* - Search in the history of messages by keyword.");
        System.out.println("/sr *regular expression* - Search in the history of messages by a regular expression.");
        System.out.println("/hp *hh:mm:ss dd.mm.yyyy - hh:mm:ss dd.mm.yyyy* - View history of messages for a certain period.");
        System.out.println("Messages format: [Author] : message");
    }
}
