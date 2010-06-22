package sling.gsoc.david.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Sling Servlet registered with PDF extensions
 *
 * @scr.component immediate="true" metatype="no"
 * @scr.service interface="javax.servlet.Servlet"
 *
 * @scr.property name="service.description" value="PDF Extension Servlet"
 * @scr.property name="service.vendor" value="David Mini CMS"
 *
 * @scr.property name="sling.servlet.resourceTypes"
 *               value="sling/servlet/default"
 *
 * @scr.property name="sling.servlet.methods"
 *               values.0="GET"
 *               values.1="POST"
 * 
 * @scr.property name="sling.servlet.extensions"
 *               value = "pdf"
 */
/**
 *
 * @author Federico Paparoni
 */
public class PdfExtension extends SlingAllMethodsServlet {

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
            /*resp.setHeader("Content-disposition",
                    "inline; filename=\"article.pdf\"");

            ServletOutputStream output = resp.getOutputStream();
            Document document = new Document();

            PdfWriter.getInstance(document,output);
            document.open();
            document.add(new Paragraph("Hello World"));
            document.close();*/
            PrintWriter writer=resp.getWriter();
            writer.println("Trying to create PDF");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            // TODO: Modify it with something better
            log.error(e.toString());
        }
    }
}
