package sling.gsoc.david.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = false, immediate = true)
@Service(value = javax.servlet.Servlet.class)
public class CloudExtension extends SlingAllMethodsServlet {

    @Property(value = "PDF Extension Servlet")
    static final String DESCRIPTION = "service.description";
    @Property(value = "David Mini CMS")
    static final String VENDOR = "service.vendor";
    @Property(value = "sling/servlet/default")
    static final String RESOURCE_TYPES = "sling.servlet.resourceTypes";
    @Property(value = "cloud")
    static final String EXTENSIONS = "sling.servlet.extensions";
    private final String ENCODING = "UTF-8";
    private final String TAG_PATH = "/content/tags";
    private final String SERVER_URL = "http://localhost:8080";
    @Reference
    private SlingRepository repository;
    private Session session;
    private static final Logger log = LoggerFactory.getLogger(CloudExtension.class);

    protected void activate(ComponentContext ctx) {
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
    }

    protected void deactivate(ComponentContext ctx) {
        log.info("deactivate");
        if (session != null) {
            session.logout();
            session = null;
        }
    }

    @Override
    protected void doGet(SlingHttpServletRequest req,
            SlingHttpServletResponse resp) {
        try {
            createTagCloud(req, resp);
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    /**
     * Create the XML rappresentation of all tags in the repository.
     *
     * @param req request
     * @param resp response
     * @throws
     */
    private void createTagCloud(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException, RepositoryException {
        log.info("createTagCloud()");

        resp.setContentType("text/plain");
        resp.setCharacterEncoding(ENCODING);
        PrintWriter writer = resp.getWriter();
        StringBuffer buffer=new StringBuffer();
        buffer.append("<tags>");
        Node rootNode = session.getRootNode().getNode(TAG_PATH.substring(1));
        if (rootNode.hasNodes()) {
            NodeIterator iterator=rootNode.getNodes();
            while(iterator.hasNext()) {
                Node node=iterator.nextNode();
                String name=node.getName();
                String count=node.getProperty("count").getString();
                buffer.append("<a href='/content/david.taglist?tag=").append(name).append("' style='").append(Integer.parseInt(count)*5).append("'>").append(name).append("</a>");
            }
        }
        buffer.append("</tags>");
        writer.print(buffer.toString());
        writer.close();
    }
}
