package paypal;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;  

class Mailer {  
    public static void send(String from,String password,String to,String sub,String msg, List<String> filenames) {  
          //Get properties object    
          Properties props = new Properties();    
          props.put("mail.smtp.host", "smtp.gmail.com");    
          props.put("mail.smtp.socketFactory.port", "465");    
          props.put("mail.smtp.socketFactory.class",    
                    "javax.net.ssl.SSLSocketFactory");    
          props.put("mail.smtp.auth", "true");    
          props.put("mail.smtp.port", "465");    
          
          Session session = Session.getDefaultInstance(props,  
        		  new javax.mail.Authenticator() {  
        		  	protected PasswordAuthentication getPasswordAuthentication() {  
        		  	return new PasswordAuthentication(from,password);  
        		  	}  
        		  });  
        		     
          			//2) compose message     
          			try{  
        		    MimeMessage message = new MimeMessage(session);  
        		    message.setFrom(new InternetAddress(from));  
        		    message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
        		    message.setSubject(sub);  
        		      
        		    //3) create MimeBodyPart object and set your message text     
        		    BodyPart messageBodyPart1 = new MimeBodyPart();  
        		    messageBodyPart1.setText(msg);  
        		    Multipart multipart = new MimeMultipart();  
        		    multipart.addBodyPart(messageBodyPart1); 
        		    
        		    //4) create new MimeBodyPart object and set DataHandler object to this object      
        		    for (String s : filenames) {
        		    	MimeBodyPart messageBodyPart2 = new MimeBodyPart();   
        		    	DataSource source = new FileDataSource(s);  
        		    	messageBodyPart2.setDataHandler(new DataHandler(source));  
        		    	messageBodyPart2.setFileName(s);  
        		     
        		    	//5) create Multipart object and add MimeBodyPart objects to this object      
        		     
        		    	multipart.addBodyPart(messageBodyPart2);
        		    }
        		  
        		    //6) set the multiplart object to the message object  
        		    message.setContent(multipart);  
        		     
        		    //7) send message  
        		    Transport.send(message);  
        		   
        		   System.out.println("message sent....");  
        		   } catch (MessagingException ex) {ex.printStackTrace();}  
    }  
}

public class SendEmails{    
 public static void main(String[] args) throws FileNotFoundException {    
	 // For Unit testing
	 /*
     //from,password,to,subject,message
	 String from = "from@gmail.com";
	 String password = "password";
	 String to = "to@gmail.com";
	 String sub = "SendEmails Testing";
	 String msg = "This is a testing message";
	 List<String> filenames = new ArrayList<>();
	 String filePath = System.getProperty("user.dir")+"\\src\\sample";
	 
	 filenames.add(filePath + "\\screenshot1.jpg");
	 filenames.add(filePath + "\\screenshot2.jpg");
	 filenames.add(filePath + "\\screenshot3.jpg");
	 
     Mailer.send(from, password, to, sub, msg, filenames);  
     */
 }    
}
