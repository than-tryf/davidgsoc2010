package sling.gsoc.david.jcr;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.value.ValueFactoryImpl;
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
    private final String TAG_PATH="/content/tags";

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
        try{
            Value[] tagValues=addedNode.getProperty("tag").getValues();
        }
        catch(ValueFormatException vfe) {
            
        }
        String UUID=addedNode.getUUID();
        log.info("New content available with UUID: "+UUID+" and path: "+addedNode.getPath());
        
        Node tagsRootNode = session.getRootNode().getNode(TAG_PATH.substring(1));
        log.info(tagsRootNode.getProperty("sling:resourceType").getString());
        log.info(tagsRootNode.getName());
        for(int i=0;i<tagValues.length;i++) {
            //ADD +1 TO COUNT PROPERTY
            if (tagsRootNode.hasNode(tagValues[i].getString())) {
                Node tagNode=tagsRootNode.getNode(tagValues[i].getString());
                int count=Integer.parseInt(tagNode.getProperty("count").getValue().getString())+1;
                Value[] UUIDs=tagNode.getProperty("UUID").getValues();
                ValueFactory valueFactory=session.getValueFactory();
                Value value=valueFactory.createValue(UUID);
                UUIDs=Arrays.copyOf(UUIDs,UUIDs.length+1);
                UUIDs[UUIDs.length-1]=value;

                tagNode.setProperty("count", count);
                tagNode.setProperty("UUIDs", UUIDs);
                tagsRootNode.save();
                log.info("New tag node created");
            }
            //CREATE NEW NODE WITH COUNT 1
            else {
                log.info("New tag value: "+tagValues[i].getString());
                Node newTagNode = tagsRootNode.addNode(tagValues[i].getString());
                newTagNode.setProperty("count", 1);
                ValueFactory valueFactory=session.getValueFactory();
                Value value=valueFactory.createValue(UUID);
                newTagNode.setProperty("UUIDs", new Value[]{value});
                tagsRootNode.save();
                log.info("New tag node created");
            }
        }

    }
}
