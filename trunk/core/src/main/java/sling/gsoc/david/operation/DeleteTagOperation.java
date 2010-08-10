package sling.gsoc.david.operation;

import java.util.logging.Level;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.observation.ObservationManager;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HtmlResponse;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.servlets.post.SlingPostOperation;
import org.apache.sling.servlets.post.SlingPostProcessor;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false, immediate = true)
@Service
public class DeleteTagOperation implements SlingPostOperation{

    @Property(value = "deletetag")
    static final String OPERATION = "sling.post.operation";
    private Session session;
    private ObservationManager observationManager;
    @Reference
    private SlingRepository repository;

    private final String CONTENT_PATH = "/content/david";
    private final String TAG_PATH="/content/tags";
    
    private static final Logger log = LoggerFactory.getLogger(DeleteTagOperation.class);

    protected void activate(ComponentContext context) throws Exception {
        session = repository.loginAdministrative(null);
    }

    protected void deactivate(ComponentContext componentContext) throws RepositoryException {
        if (session != null) {
            session.logout();
            session = null;
        }
    }

    public void run(SlingHttpServletRequest shsr, HtmlResponse hr, SlingPostProcessor[] spps) {
        log.info("run");
        String path=shsr.getRequestURI();
        log.info("delete tag for entry in "+path);
        try {
            Node deletedNode = session.getRootNode().getNode(path.substring(1));
            processDeletedNode(deletedNode);
            session.save();
        } catch (RepositoryException ex) {
            log.error(ex.toString());
        }
    }

    private void processDeletedNode(Node deletedNode) throws RepositoryException {
        log.info("processDeletedNode");
        Value[] tagValues=null;
        try{
            tagValues=deletedNode.getProperty("tag").getValues();
        }
        catch(PathNotFoundException pnfe) {
            log.info("No tag found for this entry");
            return;
        }
        catch(ValueFormatException vfe) {
            //Single property found
            Value tagValue=deletedNode.getProperty("tag").getValue();
            tagValues=new Value[1];
            tagValues[0]=tagValue;
        }
        String UUID=deletedNode.getUUID();
        Node tagsRootNode = session.getRootNode().getNode(TAG_PATH.substring(1));

        for(int i=0;i<tagValues.length;i++) {
                log.info("Tag value: "+tagValues[i].getString());
                Node tagNode=tagsRootNode.getNode(tagValues[i].getString());
                int count=Integer.parseInt(tagNode.getProperty("count").getValue().getString());

                if (count==1) {
                    tagNode.remove();
                    log.info("Tag node removed");
                }
                else {
                    Value[] UUIDs=null;
                    UUIDs=tagNode.getProperty("UUIDs").getValues();

                    ValueFactory valueFactory=session.getValueFactory();

                    Value[] newUUIDs=new Value[UUIDs.length-1];
                    log.info("remove UUID value "+valueFactory.createValue(UUID).getString());
                    String toRemove=valueFactory.createValue(UUID).getString();
                    for (int j=0,k=0;j<UUIDs.length;j++) {
                        String inArray=UUIDs[j].getString();
                        if (!toRemove.equals(inArray)) {
                            newUUIDs[k]=valueFactory.createValue(UUIDs[j].getString());
                            k++;
                        }
                    }
                    log.info("Old "+UUIDs.length+" New "+newUUIDs.length);
                    tagNode.setProperty("count", count-1);
                    tagNode.setProperty("UUIDs", newUUIDs);
                    tagsRootNode.save();
                    log.info("Tag node updated");
                }
        }
    }

}
