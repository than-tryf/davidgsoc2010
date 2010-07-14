package sling.gsoc.david.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletOutputStream;
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
public class CloudExtension extends SlingAllMethodsServlet{
    @Property(value = "PDF Extension Servlet")
    static final String DESCRIPTION = "service.description";
    @Property(value = "David Mini CMS")
    static final String VENDOR = "service.vendor";
    @Property(value = "sling/servlet/default")
    static final String RESOURCE_TYPES = "sling.servlet.resourceTypes";
    @Property(value = "cloud")
    static final String EXTENSIONS = "sling.servlet.extensions";
    private final String ENCODING = "UTF-8";
    private final String DAVID_ROOT = "/content/david";
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
        } catch (IOException ex) {
            log.error(ex.toString());
        }
    }

    private void createTagCloud(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException {
        String xml="<tags><a href='http://www.roytanck.com' style='22' color='0xff0000' hicolor='0x00cc00'>WordPress</a><a href='http://www.roytanck.com' style='12'>Flash</a><a href='http://www.roytanck.com' style='16'>Plugin</a><a href='http://www.roytanck.com' style='14'>WP-Cumulus</a><a href='http://www.roytanck.com' style='12'>3D</a><a href='http://www.roytanck.com' style='12'>Tag cloud</a><a href='http://www.roytanck.com' style='9'>Roy Tanck</a><a href='http://www.roytanck.com' style='10'>SWFObject</a><a href='http://www.roytanck.com' style='10'>Example</a><a href='http://www.roytanck.com' style='12'>Click</a><a href='http://www.roytanck.com' style='12'>Animation</a></tags>";
        resp.setContentType("text/plain");
        resp.setCharacterEncoding(ENCODING);
        PrintWriter writer = resp.getWriter();
        writer.print(xml);
        writer.close();
    }

}
