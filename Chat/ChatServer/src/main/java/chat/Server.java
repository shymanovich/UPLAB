package Chat.ChatServer.src.main.java.chat;

/**
 * Created by vlad on 03.06.2016.
 */

import Chat.ChatServer.src.main.java.chat.fileworker.FileWorker;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements HttpHandler {

    private static List<Message> messageList = new ArrayList<>();

    private MessageExchange messageExchange = new MessageExchange();

    private static List<HttpExchange> waitingClients = new ArrayList<>();

    public static void main(String[] args) {
        FileWorker fw = new FileWorker(messageList);
        fw.loadHistory();

        if(args.length != 1) {
            System.out.println("Usage: java Server port");
            //return;
        }
        try {
            System.out.println("Server is starting...");
            Integer port = Integer.parseInt("3001");
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("Server started.");
            String serverHost = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Get list of messages: GET http://" + serverHost + ":" + port + "/chat?token={token}");
            System.out.println("Add new message: POST http://" + serverHost + ":" + port + "/chat");
            System.out.println("Edit message: PUT http://" + serverHost + ":" + port + "/chat");
            System.out.println("Delete message: DELETE http://" + serverHost + ":" + port + "/chat");

            server.createContext("/chat", new Server());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            System.out.println("Error creating http server");
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        String response = "";

        try {
            System.out.println("Begin request " + httpExchange.getRequestMethod());
            String query = httpExchange.getRequestURI().getQuery();
            System.out.println("Query " + query);

            switch(httpExchange.getRequestMethod()) {
                case "GET":
                    response = doGet(httpExchange);
                    break;
                case "POST":
                    doPost(httpExchange);
                    break;
                case "PUT":
                    doPut(httpExchange);
                    break;
                case "DELETE":
                    doDelete(httpExchange);
                    break;
                case "OPTIONS":
                    response = "";
                    break;
                default:
                    throw new Exception("Unsupported http method: " + httpExchange.getRequestMethod());
            }

            if(!response.equals("WAIT")) {
                sendResponse(httpExchange, response);
                System.out.println("Response sent, size " + response.length());
                System.out.println("End request " + httpExchange.getRequestMethod());
            } else {
                waitingClients.add(httpExchange);
            }
            return;
        } catch (Exception e) {
            response = messageExchange.getErrorMessage(e.getMessage());
            e.printStackTrace();
        }

        try {
            sendResponse(httpExchange, response);
        } catch (Exception e) {
            System.out.println("Unable to send response!");
        }
    }

    private String doGet(HttpExchange httpExchange) throws Exception {
        String query = httpExchange.getRequestURI().getQuery();

        if(query != null) {
            Map<String, String> map = queryToMap(query);
            String token = map.get("token");
            System.out.println("Token " + token);

            if (token != null && !token.equals("")) {
                int index = messageExchange.getIndex(token);
                if(index == messageList.size()) {
                    return "WAIT";
                } else {
                    System.out.println("Index " + index);
                    return messageExchange.getServerResponse(messageList, index, "POST");
                }
            }
            throw new Exception("Token query parameter is absent in url: " + query);
        }
        throw new Exception("Absent query in url");
    }

    private void doPost(HttpExchange httpExchange) throws Exception {
        Message message = messageExchange.getClientMessage(httpExchange.getRequestBody());
        System.out.println("New message: " + message.toString());

        messageList.add(message);
        notifyPost();
    }

    private void doPut(HttpExchange httpExchange) throws Exception {
        Message message = messageExchange.getClientMessage(httpExchange.getRequestBody());
        System.out.println("Update message: " + message.toString());

        for(Message item : messageList) {
            if(message.getId() == item.getId()) {
                item.setText(message.getText());
                notifyEdit(item, "PUT");
                return;
            }
        }

        throw new Exception("Invalid message id " + message.getId());
    }

    private void doDelete(HttpExchange httpExchange) throws Exception {
        Message message = messageExchange.getClientMessage(httpExchange.getRequestBody());
        System.out.println("Delete message: " + message.toString());

        for(Message item : messageList) {
            if(message.getId() == item.getId()) {
                messageList.remove(item);
                notifyEdit(item, "DELETE");
                return;
            }
        }

        throw new Exception("Invalid message id " + message.getId());
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        byte[] bytes = response.getBytes();
        Headers headers = httpExchange.getResponseHeaders();

        headers.add("Access-Control-Allow-Origin", "*");
        if(httpExchange.getRequestMethod().equals("OPTIONS")) {
            headers.add("Access-Control-Allow-Methods", "PUT, DELETE, POST, GET, OPTIONS");
        }
        httpExchange.sendResponseHeaders(200, bytes.length);
        writeBody(httpExchange, bytes);
    }

    private void writeBody(HttpExchange httpExchange, byte[] bytes) throws IOException {
        OutputStream os = httpExchange.getResponseBody();
        os.write(bytes);
        os.flush();
        os.close();
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }

        return result;
    }

    private void notifyPost()
    {
        for(HttpExchange httpExchange : waitingClients) {
            handle(httpExchange);
            waitingClients.remove(httpExchange);
        }
    }

    private void notifyEdit(Message message, String type) throws Exception {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        String response = messageExchange.getServerResponse(messages, 0, type);

        for(HttpExchange httpExchange : waitingClients) {
            sendResponse(httpExchange, response);
            System.out.println("Response sent, size " + response.length());
            System.out.println("End request " + httpExchange.getRequestMethod());
            waitingClients.remove(httpExchange);
        }
    }
}