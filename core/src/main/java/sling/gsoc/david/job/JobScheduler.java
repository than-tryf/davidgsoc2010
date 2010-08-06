package sling.gsoc.david.job;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false, immediate = true)
public class JobScheduler {
    @Reference
    private Scheduler scheduler;

    @Reference
    private SlingRepository repository;
    private Session session;

    @Property(value="MYSENDJOB")
    public static final String JOB_NAME="job.name";

    @Property(value="david@gsoc")
    public static final String EMAIL_FROM = "email.from";

    @Property(value="admin@david")
    public static final String EMAIL_TO = "email.to";

    @Property(value="David Mini CMS - Report")
    public static final String EMAIL_SUBJECT = "email.subject";

    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    protected void activate(ComponentContext componentContext) throws Exception {
        log.info("activate");

        try {
            /* TODO
             * Change this behaviour
             * to connect the repository
             */
            session = repository.loginAdministrative(null);
        } catch (RepositoryException ex) {
            log.error(ex.toString());
        }
        Map<String,Serializable> config
            = new HashMap<String, Serializable>();
        SendTask task = new SendTask(session);
        task.setFrom(EMAIL_FROM);
        task.setTo(EMAIL_TO);
        task.setSubject(EMAIL_SUBJECT);
        
        scheduler.addPeriodicJob(JOB_NAME, task, config, 60, false);
    }

    protected void deactivate(ComponentContext componentContext) throws Exception {
        log.info("activate");
        scheduler.removeJob(JOB_NAME);
        scheduler=null;
    }
}
