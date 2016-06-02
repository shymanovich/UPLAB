package main.chat.classes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Formatter;
import java.util.Stack;

public class DBWorker {

    private final String HOST = "jdbc:mysql://localhost:3306/chat";
    private final String USERNAME = "root";
    private final String PASSWORD = "root";

    private Connection connection;

    public DBWorker() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean validateUser(String username, String password) {
        try {
            password = encryptPassword(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String query = "SELECT * FROM users WHERE username=?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username.toLowerCase());

            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()) {
                createUser(username, password);
                return true;
            } else {
                return password.equals(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createUser(String username, String password) {
        String query = "INSERT INTO users(username, password) values(?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(Message msg) {
        String query = "INSERT INTO messages(text, author, timestamp, isDeleted) values(?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, msg.getText());
            statement.setString(2, msg.getAuthor());
            statement.setString(3, msg.getStingTimestamp());
            statement.setInt(4, 0);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Message getMessage(long id) {
        Message msg = null;
        String query = "SELECT * FROM messages WHERE id=?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                msg = new Message(resultSet.getInt("id"), resultSet.getString("text"),
                        resultSet.getString("author"), resultSet.getString("timestamp"),
                        resultSet.getInt("isDeleted") == 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  msg;
    }

    public Stack<Message> getMessagesChunk(int n) {
        Stack<Message> chunk = new Stack<>();
        String query = "SELECT * FROM messages ORDER BY id DESC LIMIT " + n;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                chunk.push(new Message(resultSet.getInt("id"), resultSet.getString("text"),
                        resultSet.getString("author"), resultSet.getString("timestamp"),
                        resultSet.getInt("isDeleted") == 1));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chunk;
    }

    public Message deleteMessage(long id) {
        Message msg = getMessage(id);
        String query = "UPDATE messages SET isDeleted = 1, text = 'Deleted!' WHERE id=" + id;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message updateMessage(long id, String text) {
        Message msg = getMessage(id);
        String query = "UPDATE messages SET text='" + text + "' WHERE id=" + id;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public int getMessagesSize() {
        String query = "SELECT COUNT(1) FROM messages";
        return getSize(query);
    }

    public int getUsersSize() {
        String query = "SELECT COUNT(1) FROM users";
        return getSize(query);
    }

    private int getSize(String query) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next() ? resultSet.getInt("COUNT(1)") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String sha1;
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(password.getBytes("UTF-8"));
        Formatter formatter = new Formatter();
        for (byte b : crypt.digest()) {
            formatter.format("%02x", b);
        }
        sha1 = formatter.toString();
        formatter.close();
        return sha1;
    }

    public int getUserId(String username) {
        String query = "SELECT * FROM users WHERE username=?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getUserById(int id) {
        String query = "SELECT * FROM users WHERE id=" + id;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
