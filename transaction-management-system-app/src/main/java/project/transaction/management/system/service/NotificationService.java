package project.transaction.management.system.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import project.transaction.management.system.api.resource.transaction.TransactionResponseResource;

public class NotificationService {
    private static final String SNS_TOPIC_ARN = "arn:aws:sns:eu-north-1:866886651121:Transactions";
    private static final AmazonSNS snsClient = AmazonSNSClientBuilder.defaultClient();

    private NotificationService() {
    }

    public static void completeTransaction(TransactionResponseResource resource) {
        if(resource.getTransactionType().equalsIgnoreCase("DEPOSIT")) {
            sendNotification("Deposit to " + resource.getSourceAccountNumber() + " account has been successfull.");
        }
    }

    private static void sendNotification(String message) {
        snsClient.publish(new PublishRequest(SNS_TOPIC_ARN, message));
    }
}
