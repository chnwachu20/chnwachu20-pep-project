package DAO;
import Util.ConnectionUtil;
import Model.Message;
import Model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class MessageDAO {
  
    public Message insertMessage(Message message) {
        String sql = "INSERT INTO Message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3,  message.getTime_posted_epoch());
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                ResultSet keys = preparedStatement.getGeneratedKeys();
                if (keys.next()) {
                    message.setMessage_id(keys.getInt(1));
                }
            } else {
                throw new SQLException("No rows affected during message insertion.");
            }
    
            return message;
    
        } catch (SQLException e) {
            System.err.println("Error inserting message: " + e.getMessage());
            return null;
        }
    }
    


    /**
     * TODO: retrieve all messages from the Message table.
     * You only need to change the sql String.
     * @return all Message.
     */

    public List<Message> RetrieveAllMessages(){
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            //Write SQL logic here
            String sql = "SELECT * FROM Message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Message message = new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                        messages.add(message);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return messages;
    }
      /**
     
     */
  // Updated DAO Method
public Message RetrieveMessageByMessageId(int message_id) {
    String sql = "SELECT * FROM Message WHERE message_id = ?";
    try (Connection connection = ConnectionUtil.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, message_id);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            return new Message(
                rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch")
            );
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving message: " + e.getMessage());
    }
    return null; // Return null if no message found or error occurs
}

public List<Message> RetrieveMessagesByAccountId(int accountId) {
    String sql = "SELECT * FROM Message WHERE posted_by = ?";
    List<Message> messages = new ArrayList<>();

    try (
        Connection connection = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)
    ) {
        preparedStatement.setInt(1, accountId);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            messages.add(new Message(
                rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch")
            ));
        }
    } catch (SQLException e) {
        System.err.println("Error retrieving messages: " + e.getMessage());
    }
    return messages; // Return an empty list if no messages are found
}
 //Delete Message by message id
 public Message DeleteMessageByMessageId(int message_id) {
    String selectSql = "SELECT * FROM Message WHERE message_id = ?";
    String deleteSql = "DELETE FROM Message WHERE message_id = ?";
    try (Connection connection = ConnectionUtil.getConnection();
         PreparedStatement selectStmt = connection.prepareStatement(selectSql);
         PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

        // Check if the message exists
        selectStmt.setInt(1, message_id);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            // If message exists, construct the message object
            Message message = new Message(
                rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch")
            );

            // Proceed to delete the message
            deleteStmt.setInt(1, message_id);
            deleteStmt.executeUpdate();

            return message; // Return the deleted message
        } else {
            // If message does not exist, return null
            return null;
        }
    } catch (SQLException e) {
        System.err.println("Error deleting message: " + e.getMessage());
        return null;
    }
}
public Message UpdateMessageText(int message_id, String message_text) {
    String selectSql = "SELECT * FROM Message WHERE message_id = ?";
    String updateSql = "UPDATE Message SET message_text = ? WHERE message_id = ?";

    try (Connection connection = ConnectionUtil.getConnection();
         PreparedStatement selectStmt = connection.prepareStatement(selectSql);
         PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {

        // Check if the message exists
        selectStmt.setInt(1, message_id);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            // Retrieve the existing message details
            Message message = new Message(
                rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch")
            );

            // Update the message text
            updateStmt.setString(1, message_text);
            updateStmt.setInt(2, message_id);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update the message object with the new text
                message.setMessage_text(message_text);
                return message;
            }
        }

        // Message ID does not exist
        return null;

    } catch (SQLException e) {
        System.err.println("Error updating message: " + e.getMessage());
        return null;
    }
}

  
 /**
     * TODO: insert a message into the Message table.
     * Unlike some of the other insert problems, the primary key here will be provided by the client as part of the
     * Message object. Given the specific nature of an ISBN as both a numerical organization of books outside of this
     * database, and as a primary key, it would make sense for the client to submit an ISBN when submitting a book.
     * You only need to change the sql String and leverage PreparedStatement's setString and setInt methods.
     */
    //update message text
 

   
// Updated doesUserExist Method in DAO
public boolean doesUserExist(int posted_by) {
    String sql = "SELECT 1 FROM Users WHERE user_id = ?"; // Assuming `Users` table with `user_id`
    try (Connection connection = ConnectionUtil.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        preparedStatement.setInt(1, posted_by);
        ResultSet rs = preparedStatement.executeQuery();

        return rs.next(); // Returns true if a record is found, false otherwise

    } catch (SQLException e) {
        System.err.println("Error checking user existence: " + e.getMessage());
        return false;
    }
}

}
