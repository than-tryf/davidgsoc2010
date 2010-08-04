package sling.gsoc.david.job;

import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendTask implements Job{
    private static final Logger log = LoggerFactory.getLogger(SendTask.class);

    public void execute(JobContext jc) {
        log.info("execute YESSAAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

}
