package sling.gsoc.david.job;

import java.util.Properties;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendTask implements Job {

    private static final Logger log = LoggerFactory.getLogger(SendTask.class);
    private String from = "david@gsoc";
    private String to = "admin@david";
    private String subject = "David Mini CMS - Report";
    private javax.jcr.Session session;
    private final String TAG_PATH = "/content/tags";
    private final String DAVID_ROOT = "/content/david";

    public SendTask(javax.jcr.Session session) {
        this.session=session;
    }

    public void execute(JobContext jc) {
        log.info("execute");
        String info=retrieveRepositoryInformation();
        sendMail(info);
    }

    public void sendMail(String info) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        Session mailSession = Session.getDefaultInstance(props);
        Message simpleMessage = new MimeMessage(mailSession);

        InternetAddress fromAddress = null;
        InternetAddress toAddress = null;
        ClassLoader bakCL = Thread.currentThread().getContextClassLoader();
        try {
            fromAddress = new InternetAddress(from);
            toAddress = new InternetAddress(to);
        } catch (AddressException e) {
            log.error(e.toString());
        }

        try {
            simpleMessage.setFrom(fromAddress);
            simpleMessage.setRecipient(RecipientType.TO, toAddress);
            simpleMessage.setSubject(subject);
            simpleMessage.setText(info);
            //Workaround for using javax.mail bundle
            //see http://www.mail-archive.com/user@geronimo.apache.org/msg14511.html
            Thread.currentThread().setContextClassLoader(null);
            Transport.send(simpleMessage);
        } catch (MessagingException e) {
            log.error(e.toString());
        }
        finally {
                Thread.currentThread().setContextClassLoader(bakCL);
        }
    }

    private String retrieveRepositoryInformation() {
        StringBuilder info=new StringBuilder();
        try {
            //INFORMATIONS ABOUT ARTICLES
            Node rootNode = session.getRootNode().getNode(DAVID_ROOT.substring(1));
            if (rootNode.hasNodes()) {
                long size=rootNode.getNodes().getSize();
                info.append("Articles available on David Mini CMS: ");
                info.append(size);
                info.append("\n");
            }
            //INFORMATIONS ABOUT TAGS
            Node tagNode = session.getRootNode().getNode(TAG_PATH.substring(1));
            if (tagNode.hasNodes()) {
                long size=tagNode.getNodes().getSize();
                info.append("Tags available on David Mini CMS: ");
                info.append(size);
                info.append("\n");
            }
            log.info("Info retrieved: \n"+info.toString());
        } catch (PathNotFoundException ex) {
            log.error(ex.toString());
            info.append(ex.toString());
        } catch (RepositoryException ex) {
            log.error(ex.toString());
            info.append(ex.toString());
        } finally {
            return info.toString();
        }
    }
}
