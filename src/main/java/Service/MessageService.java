package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

public class MessageService {
    public MessageDAO messageDAO;

    /**
     * No-args constructor for messageService which creates a MessageDAO.
     * There is no need to change this constructor.
     */
    public MessageService(){
        messageDAO = new MessageDAO();
    }
    /**
     * Constructor for a MessageService when a MessageDAO is provided.
     * This is used for when a mock MessageDAO that exhibits mock behavior is used in the test cases.
     * This would allow the testing of MessageService independently of MessageDAO.
     * There is no need to modify this constructor.
     * @param messageDAO
     */
    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }
    /**
     * TODO: Use the messageDAO to retrieve all messages.
     * @return all messages.
     */
    public List<Message> RetrieveAllMessages() {
        return messageDAO.RetrieveAllMessages();
    }
    public Message CreateMessage(Message message) {
        try {
            return messageDAO.insertMessage(message);
        } catch (Exception e) {
            System.err.println("Error creating message: " + e.getMessage());
            return null;
        }
    }
    /**
     * TODO: Use the messageDAO to persist a message to the database.
     * An message_id will be provided in Message. Method should check if the message_id already exists before it attempts to
     * persist it.
     * @param message a message object.
     * @return message if it was successfully persisted, null if it was not successfully persisted (eg if the message primary
     * key was already in use.)
     */
    public Message RetrieveMessageByMessageId(int message_id) {
        return messageDAO.RetrieveMessageByMessageId(message_id);
    }
    public List<Message> RetrieveAllMessagesForUser(int accountId) {
        return messageDAO.RetrieveMessagesByAccountId(accountId);
    }
    public Message UpdateMessageText(int message_id, String message_text) {
        try {
            // Call the DAO method to update the message
            return messageDAO.UpdateMessageText(message_id, message_text);
        } catch (Exception e) {
            System.err.println("Error updating message text: " + e.getMessage());
            return null;
        }
    }
    
    public Message DeleteMessageByMessageId(int message_id) {
        try {
            // Attempt to delete the message in DAO
            return messageDAO.DeleteMessageByMessageId(message_id);
        } catch (Exception e) {
            System.err.println("Error deleting message: " + e.getMessage());
            return null;
        }
    }
    public boolean doesUserExist(int posted_by) {
        return messageDAO.doesUserExist(posted_by);
    }
    
}
