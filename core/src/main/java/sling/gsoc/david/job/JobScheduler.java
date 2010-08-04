package sling.gsoc.david.job;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.commons.scheduler.Job;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false, immediate = true)
public class JobScheduler {
    @Reference
    private Scheduler scheduler;

    private static final Logger log = LoggerFactory.getLogger(JobScheduler.class);

    protected void activate(ComponentContext componentContext) throws Exception {
        log.info("activate");
        Map<String,Serializable> config
            = new HashMap<String, Serializable>();
        //set any configuration options in the config map here
        Job job = new SendTask();
        scheduler.addPeriodicJob("myperiodicjob", job, config, 60, false);
    }
}
