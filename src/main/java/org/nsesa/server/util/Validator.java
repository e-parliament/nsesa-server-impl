package org.nsesa.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO check if performant enough
 *
 * Date: 02/05/13 13:44
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
public class Validator {

    private static final Logger LOG = LoggerFactory.getLogger(Validator.class);

    public static void validate(final String content, String... schemaNames) {
        LOG.debug("====================== AN VALIDATION ======================");
        System.setProperty("javax.xml.validation.SchemaFactory:" + XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "org.apache.xerces.jaxp.validation.XMLSchemaFactory");

        List<Source> schemas = new ArrayList<Source>();
        for (final String schemaName : schemaNames) {
            schemas.add(new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaName)));
        }
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            //            factory.setValidating(true);
            factory.setNamespaceAware(true);

            final SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

            factory.setSchema(schemaFactory.newSchema(schemas.toArray(new Source[schemas.size()])));

            final SAXParser parser = factory.newSAXParser();

            final XMLReader reader = parser.getXMLReader();

            final LoggingErrorHandler loggingErrorHandler = new LoggingErrorHandler();
            reader.setErrorHandler(loggingErrorHandler);
            reader.parse(new InputSource(new ByteArrayInputStream(content.trim().getBytes("UTF-8"))));

        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Problem parsing XML - configuration error: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Problem parsing XML - I/O error: " + e.getMessage(), e);
        } catch (SAXException e) {
            throw new RuntimeException("Problem parsing XML - SAX error: " + e.getMessage(), e);
        } finally {
            LOG.debug("==================== END OF VALIDATION ====================");
        }

    }

    public static class LoggingErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException e) throws SAXException {
            LOG.warn("SAX warning: " + e);
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            LOG.warn("SAX error: " + e);
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            LOG.warn("SAX fatal: " + e);
        }
    }
}
