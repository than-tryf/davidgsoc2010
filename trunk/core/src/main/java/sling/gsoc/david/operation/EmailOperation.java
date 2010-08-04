package sling.gsoc.david.operation;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.servlets.post.SlingPostOperation;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false, immediate = true)
@Service
public class EmailOperation implements SlingPostOperation{

    @Property(value = "email")
    static final String OPERATION = "sling.post.operation";

    private static final Logger log = LoggerFactory.getLogger(EmailOperation.class);

    public void run(SlingHttpServletRequest request, HtmlResponse response, SlingPostProcessor[] spps) {
        log.info("This will send an email...");
    }

}
