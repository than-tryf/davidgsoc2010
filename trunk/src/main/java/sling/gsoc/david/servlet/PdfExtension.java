package sling.gsoc.david.servlet;

import com.lowagie.text.Chunk;
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
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.jcr.api.SlingRepository;

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
    static private final String ENCODING = "UTF-8";
    static private final String DAVID_ROOT = "/content/david";
    @Reference
    private SlingRepository repository;
    private Session session;

    protected void activate(ComponentContext ctx) {
        System.out.println("activate");
        try {
            /* TODO
             * Change this behaviour
             * to connect the repository
             */
            session = repository.loginAdministrative(null);
        } catch (RepositoryException ex) {
            System.out.println(ex.toString());
        }
    }

    protected void deactivate(ComponentContext ctx) {
        System.out.println("deactivate");
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
     * @throws IOException in case the search will unexpectedly fail
     */
    private void createPdfResult(SlingHttpServletRequest req,
            SlingHttpServletResponse resp) {
        try {
            Resource resource = req.getResource();
            Node node = session.getRootNode().getNode(resource.getPath().substring(1));
            System.out.println(node.getPath());
            if (node.getPath().equals(DAVID_ROOT)) {
                resp.setContentType("text/plain");
                resp.setCharacterEncoding(ENCODING);
                resp.setHeader("Content-disposition",
                        "inline; filename=\"article.pdf\"");

                ServletOutputStream output = resp.getOutputStream();
                Document document = new Document();

                PdfWriter.getInstance(document, output);
                document.open();
                StringBuffer temp = new StringBuffer("Rose India");
                while (temp.length() != 160) {
                    temp.append(" ");
                }
                Chunk chunk1 = new Chunk(temp.toString()+Chunk.NEWLINE);
                chunk1.setTextRise(6.0f);
                chunk1.setBackground(new Color(0xFc, 0xDc, 0xAc));
                Chunk chunk2 = new Chunk(temp.toString()+Chunk.NEWLINE);
                chunk2.setTextRise(6.0f);
                chunk2.setBackground(new Color(0xDc, 0xAc, 0xFc));
                Chunk chunk3 = new Chunk(temp.toString()+Chunk.NEWLINE);
                chunk3.setTextRise(6.0f);
                chunk3.setBackground(new Color(0xFc, 0xDc, 0xAc));
                document.add(chunk1);
                document.add(chunk2);
                document.add(chunk3);
                document.close();
                output.flush();
                output.close();
            } else {
                String title = node.getProperty("title").getString();
                String text = node.getProperty("text").getString();

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

                ArrayList p = HTMLWorker.parseToList(
                        new InputStreamReader(new ByteArrayInputStream(text.getBytes())), st);

                for (int k = 0; k < p.size(); ++k) {
                    document.add((Element) p.get(k));
                }


                document.close();
                output.flush();
                output.close();
            }

        } catch (Exception e) {
            // TODO
            // Modify it with something better
            System.out.println(e.toString());
        }
    }
}
