package sling.gsoc.david.jcr;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Listen for JCR event and create a tag
 * structure for David tagging system **/

@Component(metatype = false, immediate = true)
public class TagGenerator implements EventListener {

    @Property(value = "Tag Generator Service")
    static final String DESCRIPTION = "service.description";
    @Property(value = "David Mini CMS")
    static final String VENDOR = "service.vendor";
    private Session session;
    private ObservationManager observationManager;
    private final String CONTENT_PATH = "/content/david";
    @Reference
    private SlingRepository repository;

    private static final Logger log = LoggerFactory.getLogger(TagGenerator.class);

    protected void activate(ComponentContext context) throws Exception {

        String contentPath = CONTENT_PATH;

        session = repository.loginAdministrative(null);
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED).equals("true")) {
            observationManager = session.getWorkspace().getObservationManager();
            observationManager.addEventListener(this, Event.NODE_ADDED, contentPath, true, null, null, false);
        }
    }

    protected void deactivate(ComponentContext componentContext) throws RepositoryException {
        if (observationManager != null) {
            observationManager.removeEventListener(this);
        }
        if (session != null) {
            session.logout();
            session = null;
        }
    }

    public void onEvent(EventIterator events) {
        while (events.hasNext()) {
            Event event = events.nextEvent();
            try {
                if (event.getType() == Event.NODE_ADDED) {
                    log.info("new upload: {}", event.getPath());
                    Node addedNode = session.getRootNode().getNode(event.getPath().substring(1));
                    processNode(addedNode);
                    log.info("finished processing of {}", event.getPath());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void processNode(Node addedNode) throws RepositoryException {
        Value[] tagValues=addedNode.getProperty("tag").getValues();
        String UUID=addedNode.getUUID();
        log.info("New content available with UUID: "+UUID);
        for(int i=0;i<tagValues.length;i++) {
            log.info(tagValues[i].getString());
        }
    }
}
