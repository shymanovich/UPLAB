package main.chat.servlets;

/**
 * Created by vlad on 02.06.2016.
 */
import main.chat.classes.DBWorker;
import main.chat.classes.Message;
import main.chat.classes.MessageExchange;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@WebServlet(value = "/chat")
public class ChatServlet extends HttpServlet {

    DBWorker dbWorker;

    private MessageExchange messageExchange;

    private int nMessages;

    private List<Long> modifiedMsgs;

    Logger logger;

    @Override
    public void init() throws ServletException {
        super.init();
        messageExchange = new MessageExchange();
        dbWorker = new DBWorker();
        nMessages = dbWorker.getMessagesSize();
        modifiedMsgs = new ArrayList<>();
        createLogger();
    }

    private void createLogger() {
        logger = Logger.getLogger(this.getServletName());
        FileHandler fh;
        try {
            fh = new FileHandler("LogFile.log", true);
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            fh.setFormatter(new SimpleFormatter());
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tokenMsg = req.getParameter("tokenMsg");
        String tokenMod = req.getParameter("tokenMod");
        int indexMsg = tokenMsg == null ? 0 : messageExchange.getIndex(tokenMsg);
        int indexMod = tokenMod == null ? 0 : messageExchange.getIndex(tokenMod);
        logger.log(Level.INFO, "IndexMsg " + indexMsg + "\nIndexMod " + indexMod);
        while(indexMsg == nMessages && indexMod == modifiedMsgs.size()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String response = messageExchange.getServerResponse(dbWorker, indexMsg, nMessages, modifiedMsgs, indexMod);
        resp.getOutputStream().println(response);
        logger.log(Level.INFO, "Response sent, size " + response.length());
        logger.log(Level.INFO, "End request " + req.getRequestedSessionId());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Message message = null;
        try {
            message = messageExchange.getClientMessage(req.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "New message: " + message.toString());
        dbWorker.addMessage(message);
        nMessages++;
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Message message = null;
        try {
            message = messageExchange.getClientMessage(req.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Update message: " + message.toString());

        message = dbWorker.updateMessage(message.getId(), message.getText());
        if (message != null) {
            modifiedMsgs.add(message.getId());
        } else {
            logger.log(Level.WARNING, "Invalid message id");
            resp.getWriter().println(messageExchange.getErrorMessage("Invalid message id"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Message message = null;
        try {
            message = messageExchange.getClientMessage(req.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Delete message: " + message.toString());

        message = dbWorker.deleteMessage(message.getId());
        if(message != null) {
            modifiedMsgs.add(message.getId());
        } else {
            logger.log(Level.WARNING, "Invalid message id");
            resp.getWriter().println(messageExchange.getErrorMessage("Invalid message id "));
        }
    }
}