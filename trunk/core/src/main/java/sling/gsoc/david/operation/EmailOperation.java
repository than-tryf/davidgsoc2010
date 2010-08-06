package sling.gsoc.david.operation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.servlets.post.SlingPostOperation;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sling.gsoc.david.job.SendTask;

@Component(metatype = false, immediate = true)
@Service
public class EmailOperation implements SlingPostOperation{

    @Property(value = "email")
    static final String OPERATION = "sling.post.operation";

    @Property(value = "from")
    static final String FROM = "notify.email.from";

    @Property(value = "to")
    static final String TO = "notify.email.to";

    @Property(value = "Seen on David Mini CMS")
    static final String SUBJECT = "notify.email.subject";

    @Property(value = "text")
    static final String TEXT = "notify.email.text";

    @Reference
    private Scheduler scheduler;

    private static final Logger log = LoggerFactory.getLogger(EmailOperation.class);

    public void run(SlingHttpServletRequest request, HtmlResponse response, SlingPostProcessor[] spps) {
        try {
            log.info("This will send an email...");

            log.info(request.getParameter(FROM));
            log.info(request.getParameter(TO));
            log.info(request.getParameter(SUBJECT));
            log.info(request.getParameter(TEXT));

            Map<String, Serializable> config = new HashMap<String, Serializable>();
            SendTask task = new SendTask();
            task.setFrom(request.getParameter(FROM));
            task.setTo(request.getParameter(TO));
            task.setSubject(request.getParameter(SUBJECT));
            task.setText(request.getParameter(TEXT));
            String jobName = "EmailOperation";

            final long delay = 10 * 1000;
            final Date fireDate = new Date();
            fireDate.setTime(System.currentTimeMillis() + delay);

            this.scheduler.fireJobAt(jobName, task, config, fireDate);
        } catch (Exception ex) {
            log.error(ex.toString());
        }

    }

}
