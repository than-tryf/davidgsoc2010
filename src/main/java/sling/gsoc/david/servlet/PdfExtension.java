package sling.gsoc.david.servlet;


import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Paragraph;

/** Sling Servlet registered with PDF extensions**/

@Component(metatype=false,immediate=true)
@Service(value=javax.servlet.Servlet.class)
public class PdfExtension extends SlingAllMethodsServlet {

    @Property(value="PDF Extension Servlet")
    static final String DESCRIPTION = "service.description";
    @Property(value="David Mini CMS")
    static final String VENDOR = "service.vendor";
    @Property(value="sling/servlet/default")
    static final String RESOURCE_TYPES = "sling.servlet.resourceTypes";
    @Property(value="pdf")
    static final String EXTENSIONS = "sling.servlet.extensions";
    

    private final Logger log = LoggerFactory.getLogger(PdfExtension.class);
    private final String CONTENT_TYPE = "application/pdf";
    private final String ENCODING = "UTF-8";

    protected void activate(ComponentContext ctx) {
        log.info("activate");
    }

    protected void deactivate(ComponentContext ctx) {
        log.info("deactivate");
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
            resp.setContentType("text/plain");
            resp.setCharacterEncoding(ENCODING);
            resp.setHeader("Content-disposition",
                    "inline; filename=\"article.pdf\"");

            ServletOutputStream output = resp.getOutputStream();
            Document document = new Document();

            PdfWriter.getInstance(document,output);
            document.open();
            document.add(new Paragraph("Hello World"));
            document.close();
            output.flush();
            output.close();
            
            //PrintWriter writer=resp.getWriter();
            //writer.println("Trying to create PDF");
            //writer.flush();
            //writer.close();
        } catch (Exception e) {
            // TODO: Modify it with something better
            log.error(e.toString());
        }
    }
}
