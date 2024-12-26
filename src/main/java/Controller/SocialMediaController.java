package Controller;


import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;


import java.util.List;
import java.util.Map;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
        MessageService messageService;
        AccountService accountService;
        
    public SocialMediaController(){
        this.messageService = new MessageService();
        this.accountService = new AccountService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() { 
        Javalin app = Javalin.create();
        app.post("/messages", this::CreateMessageHandler);
        app.delete("/messages/{message_id}", this::DeleteMessageByMessageIdHandler);
        app.get("/accounts/{account_id}/messages", this::RetrieveAllMessagesForUserHandler);
        app.get("/messages", this::RetrieveAllMessagesHandler);
        app.get("/messages/{message_id}", this::RetrieveMessageByMessageIdHandler);
        app.patch("/messages/{message_id}", this::UpdateMessageTextHandler);
        app.post("/login", this::UserLoginHandler);
        app.post("/register", this::UserRegistrationHandler);
        
        
        
        return app;
    }
// Updated handler method
private void CreateMessageHandler(Context ctx) {
    try {
        // Parse the request body into a Message object
        Message message = ctx.bodyAsClass(Message.class);

        // Validate the message
        if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255 ||messageService.doesUserExist(message.getPosted_by()) == true) {
            ctx.status(400).result("");
            return;
        }

     
        // Persist the message
        Message createdMessage = messageService.CreateMessage(message);

        if (createdMessage == null) {
            ctx.status(400).result("");
        } else {
            ctx.json(createdMessage);
        }

    } catch (Exception e) {
        ctx.status(400).result("Invalid request body.");
    }
}
private void UserLoginHandler(Context ctx) throws JsonProcessingException {
    try {
        // Parse the request body into an Account object
        Account account = ctx.bodyAsClass(Account.class);

        // Validate the account: username and password must not be null
        if (account.getUsername() == null || account.getPassword() == null) {
            ctx.status(401).result("");
            return;
        }

        // Attempt to log in the user
        Account userLogin = AccountService.userLogin(account.getUsername(), account.getPassword());

        if (userLogin == null) {
            ctx.status(401).result("");
        } else {
            ctx.json(userLogin); // Return the account details, including account_id
        }
    } catch (Exception e) {
        ctx.status(400).result("Invalid request body.");
    }
}
private void UserRegistrationHandler(Context ctx) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    try {
        // Parse the request body into an Account object
        Account account = mapper.readValue(ctx.body(), Account.class);

        // Validate the account
        if (account.getUsername() == null || account.getUsername().isBlank() || 
            account.getPassword() == null || account.getPassword().length() < 4 || 
            accountService.doesUserExistByUsername(account.getUsername())) {
            ctx.status(400).result("");
            return;
        }

        // Persist the account
        Account registeredAccount = AccountService.UserRegistration(account);

        if (registeredAccount == null) {
            ctx.status(400).result("");
        } else {
            ctx.json(registeredAccount);
        }

    } catch (Exception e) {
        ctx.status(400).result("Invalid request body.");
    }
}


    private void RetrieveAllMessagesHandler(Context ctx) {
        List<Message> messages = messageService.RetrieveAllMessages();
        ctx.json(messages);
    }
    private void DeleteMessageByMessageIdHandler(Context ctx) {
        try {
            String messageIdStr = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(messageIdStr); // Extract the message ID from the context
    
            // Attempt to delete the message
            Message deletedMessage = messageService.DeleteMessageByMessageId(message_id);
    
            if (deletedMessage == null) {
                // Message did not exist, return empty body with status 200
                ctx.status(200).result("");
            } else {
                // Message existed and was deleted, return the deleted message
                ctx.json(deletedMessage);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message ID.");
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while deleting the message.");
        }
    }

    private void RetrieveMessageByMessageIdHandler(Context ctx) {
        try {
            String messageIdStr = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(messageIdStr); // Extract the message ID from the context
    
            Message message = messageService.RetrieveMessageByMessageId(message_id);
            if (message == null) {
                ctx.status(200).json(""); // Empty response body with status 200
            } else {
                ctx.json(message); // Return the message in JSON format
            }
    
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message_id: must be an integer.");
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
        }
    }
    private void RetrieveAllMessagesForUserHandler(Context ctx) {
        try {
            String accountIdStr = ctx.pathParam("account_id");
            int accountId = Integer.parseInt(accountIdStr); // Extract the account ID from the context
            List<Message> messages = messageService.RetrieveAllMessagesForUser(accountId);
            ctx.status(200).json(messages); // Always return 200 with a JSON response
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account_id: must be an integer.");
        } catch (Exception e) {
            ctx.status(500).result("An unexpected error occurred.");
        }
    }
    private void UpdateMessageTextHandler(Context ctx) {
        try {
            // Extract the message ID
            String messageIdStr = ctx.pathParam("message_id");
            int message_id = Integer.parseInt(messageIdStr);
    
            // Parse the JSON body to extract message_text
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, String> requestBody = mapper.readValue(ctx.body(), Map.class);
            String message_text = requestBody.get("message_text");
    
            // Validate the new message text
            if (message_text == null || message_text.isBlank() || message_text.length() > 255) {
                ctx.status(400).result("");
                return;
            }
    
            // Update the message
            Message updatedMessage = messageService.UpdateMessageText(message_id, message_text);
    
            if (updatedMessage == null) {
                // Update failed (e.g., message ID does not exist)
                ctx.status(400).result("");
            } else {
                // Return the updated message
                ctx.json(updatedMessage);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid message ID.");
        } catch (JsonProcessingException e) {
            ctx.status(400).result("Invalid request body format.");
        } catch (Exception e) {
            ctx.status(500).result("An error occurred while updating the message.");
        }
    }


}