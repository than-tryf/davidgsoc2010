package sling.gsoc.david.servlet;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import javax.jcr.RepositoryException;
import javax.servlet.ServletOutputStream;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.ComponentContext;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Sling Servlet registered with PDF extensions**/
@Component(metatype = false, immediate = true)
@Service(value = javax.servlet.Servlet.class)
public class PdfExtension extends SlingAllMethodsServlet {

    @Property(value = "PDF Extension Servlet")
    static final String DESCRIPTION = "service.description";
    @Property(value = "David Mini CMS")
    static final String VENDOR = "service.vendor";
    @Property(value = "sling/servlet/default")
    static final String RESOURCE_TYPES = "sling.servlet.resourceTypes";
    @Property(value = "pdf")
    static final String EXTENSIONS = "sling.servlet.extensions";
    private final String ENCODING = "UTF-8";
    private final String DAVID_ROOT = "/content/david";
    private final String SERVER_URL = "http://localhost:8080";
    @Reference
    private SlingRepository repository;
    private Session session;
    private Color COLOR_LINE_1 = new Color(0xDc, 0xAc, 0xFc);
    private Color COLOR_LINE_2 = new Color(0xFc, 0xDc, 0xAc);

    private static final Logger log = LoggerFactory.getLogger(PdfExtension.class);

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
        createPdfResult(req, resp);
    }

    /**
     * Create the PDF rappresentation of the resource.
     *
     * @param req request
     * @param resp response
     * @throws
     */
    private void createPdfResult(SlingHttpServletRequest req,
            SlingHttpServletResponse resp) {
        try {
            Resource resource = req.getResource();
            Node node = session.getRootNode().getNode(resource.getPath().substring(1));
            System.out.println(node.getPath());
            if (node.getPath().equals(DAVID_ROOT)) {
                createPdfList(req, resp);
            } else {
                createPdfResource(req, resp);
            }

        } catch (Exception e) {
            // TODO
            // Modify it with something better
            log.error(e.toString());
        }
    }

    /**
     * Create the PDF rappresentation of all resources in the repository.
     *
     * @param req request
     * @param resp response
     * @throws
     */
    private void createPdfList(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException, DocumentException, RepositoryException {
        log.info("createPdfList()");
        
        Node rootNode = session.getRootNode().getNode(DAVID_ROOT.substring(1));
        if (!rootNode.hasNodes()) {
            log.error("No entry in the repository");
            returnErrorPDF("No entry in the repository");
        }

        resp.setContentType("text/plain");
        resp.setCharacterEncoding(ENCODING);
        resp.setHeader("Content-disposition",
                "inline; filename=\"article.pdf\"");

        ServletOutputStream output = resp.getOutputStream();
        Document document = new Document();

        PdfWriter.getInstance(document, output);
        document.open();
        Paragraph intro=new Paragraph("List of articles in David Mini CMS\n\n");
        document.add(intro);
        QueryManager queryManager = rootNode.getSession().getWorkspace().getQueryManager();  
        Query query = queryManager.createQuery("/jcr:root/content/david/*/*/*/element(*, nt:unstructured) order by @created descending", "xpath");
        NodeIterator iterator=query.execute().getNodes();
        Node tempNode=null;
        String title=null;
        String path=null;
        StringBuffer tempBuffer=null;
        Chunk[] chunk = new Chunk[(int)iterator.getSize()];
        int i=0;
        while(iterator.hasNext()) {
            tempNode=iterator.nextNode();
            title = tempNode.getProperty("title").getString();
            path = tempNode.getPath();
            tempBuffer = new StringBuffer("");
            tempBuffer.append(i).append(" - ");
            tempBuffer.append(title);
            tempBuffer.append(" - ");
            tempBuffer.append(SERVER_URL).append(path);
            while (tempBuffer.length() < 80) {
                tempBuffer.append(" ");
            }
            log.info("Linea: "+tempBuffer.toString()+"|");
            chunk[i] = new Chunk(tempBuffer.toString()+Chunk.NEWLINE);
            if (i % 2 == 0)
                chunk[i].setBackground(COLOR_LINE_1);
            else
                chunk[i].setBackground(COLOR_LINE_2);

            document.add(chunk[i]);
            i++;
        }
        
        document.close();
        output.flush();
        output.close();
    }

    private void createPdfResource(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws RepositoryException, IOException, DocumentException {
        log.info("createPdfResource()");
        Resource resource = req.getResource();
        Node node = session.getRootNode().getNode(resource.getPath().substring(1));
        String title = node.getProperty("title").getString();
        String text = node.getProperty("text").getString();
        Value[] tagValues=null;
        try{
            tagValues=node.getProperty("tag").getValues();
        }
        catch(PathNotFoundException pnfe) {
            log.info("No tag found for this entry");
        }
        catch(ValueFormatException vfe) {
            //Single property found
            Value tagValue=node.getProperty("tag").getValue();
            tagValues=new Value[1];
            tagValues[0]=tagValue;
        }

        resp.setContentType("text/plain");
        resp.setCharacterEncoding(ENCODING);
        resp.setHeader("Content-disposition",
                "inline; filename=\"article.pdf\"");

        ServletOutputStream output = resp.getOutputStream();
        Document document = new Document();

        PdfWriter.getInstance(document, output);
        document.open();

        StyleSheet st = new StyleSheet();
        st.loadTagStyle("body", "leading", "16,0");
        document.add(new Paragraph("Title:" + title + "\n\n"));
        if (tagValues==null)
            document.add(new Paragraph("Tags: No tags for this entry\n\n"));
        else {
            StringBuilder tags=new StringBuilder();
            for(int i=0;i<tagValues.length;i++)
                tags.append(" ").append(tagValues[i].getString()).append(" ");

            document.add(new Paragraph("Tags: "+tags.toString()+"\n\n"));
        }


        ArrayList p = HTMLWorker.parseToList(
                new InputStreamReader(new ByteArrayInputStream(text.getBytes())), st);

        for (int k = 0; k < p.size(); ++k) {
            document.add((Element) p.get(k));
        }


        document.close();
        output.flush();
        output.close();
    }

    private void returnErrorPDF(String string) {

    }
}
