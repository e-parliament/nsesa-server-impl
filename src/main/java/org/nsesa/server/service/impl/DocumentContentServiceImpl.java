package org.nsesa.server.service.impl;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.inspiresoftware.lib.dto.geda.assembler.dsl.impl.DefaultDSLRegistry;
import org.apache.cxf.annotations.GZIP;
import org.nsesa.server.domain.DocumentContent;
import org.nsesa.server.dto.DocumentContentDTO;
import org.nsesa.server.repository.DocumentContentRepository;
import org.nsesa.server.service.api.DocumentContentService;
import org.nsesa.server.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.net.ssl.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: 11/03/13 15:52
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@WebService(endpointInterface = "org.nsesa.server.service.api.DocumentContentService", serviceName = "DocumentContentService")
@GZIP
@Path("/content")
public class DocumentContentServiceImpl implements DocumentContentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentContentServiceImpl.class);

    @Autowired
    DocumentContentRepository documentContentRepository;

    Map<String, Resource> documents;

    final Assembler documentContentAssembler = DTOAssembler.newAssembler(DocumentContentDTO.class, DocumentContent.class);

    private boolean installedTrustedCertificate;

    @Transactional()
    public void init() {
        // initialize data
        for (Map.Entry<String, Resource> entry : documents.entrySet()) {
            try {
                final String content = Files.toString(entry.getValue().getFile(), Charset.forName("UTF-8"));
                DocumentContent documentContent = documentContentRepository.findByDocumentID(entry.getKey());
                if (documentContent == null) documentContent = new DocumentContent();
                documentContent.setContent(content);
                documentContent.setDocumentID(entry.getKey());
                documentContentRepository.save(documentContent);
                LOG.info("Initialized document {} into content repository.", entry.getKey());
            } catch (IOException e) {
                LOG.error("Could not import document " + entry.getKey(), e);
            }
        }
    }

    @GET
    @Path("/documentID/{documentID:.+}") // we need a regex here or remote document IDs won't work since in JaxRS
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public DocumentContentDTO getDocumentContent(@PathParam("documentID") String documentID) {
        DocumentContent documentContent = documentContentRepository.findByDocumentID(documentID);

        if (documentContent == null) {
            if (documentID.startsWith("http://") || documentID.startsWith("https://")) {
                // assume we're retrieving a remote document
                String content = null;
                try {
                    if (documentID.startsWith("https://")) {
                        installTrustedCertificateIfNecessary();
                    }
                    URL url = new URL(documentID);
                    content = Resources.toString(url, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    LOG.error("Could not read document from URL {}", documentID, e);
                    throw new RuntimeException("Could not read document content.");
                }
                if (content != null) {
                    // create a new document content and store it
                    documentContent = new DocumentContent();
                    documentContent.setDocumentID(documentID);
                    documentContent.setContent(content);
                    documentContentRepository.save(documentContent);

                }
            }
        }
        if (documentContent != null) {
            DocumentContentDTO dto = new DocumentContentDTO();
            documentContentAssembler.assembleDto(dto, documentContent, getConvertors(), new DefaultDSLRegistry());
            return dto;
        }
        return null;
    }

    /**
     * Install a trusted manager to bypass all https certificates
     */
    private void installTrustedCertificateIfNecessary() {
        if (installedTrustedCertificate) {
            return;
        }
        try {
            //create a simple trust
            TrustManager trm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trm}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            LOG.error("No SSL algorithm", e);
            throw new RuntimeException("No SSL algorithm can be set up", e);
        } catch (KeyManagementException e) {
            LOG.error("No Key algorithm", e);
            throw new RuntimeException("No Key management algorithm can be set up", e);
        } finally {
            installedTrustedCertificate = true;
        }
    }

    @POST
    @Path("/save")
    @Transactional()
    @Override
    public void saveDocumentContent(@WebParam(name = "documentContentDTO") DocumentContentDTO documentContentDTO) {
        // validate first
        Validator.validate(documentContentDTO.getContent(), "xml.xsd", "akomantoso20.xsd");

        final DocumentContent documentContent = documentContentRepository.findByDocumentID(documentContentDTO.getDocumentID());
        if (documentContent != null) {
            documentContent.setContent(documentContentDTO.getContent());
            documentContentRepository.save(documentContent);
        }
    }

    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<DocumentContentDTO> list(@DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("5") @QueryParam("rows") int rows) {
        final Page<DocumentContent> page = documentContentRepository.findAll(new PageRequest(offset, rows));
        final List<DocumentContentDTO> documentDTOs = new ArrayList<DocumentContentDTO>();
        documentContentAssembler.assembleDtos(documentDTOs, page.getContent(), new HashMap<String, Object>(), new DefaultDSLRegistry());
        return documentDTOs;
    }

    @GET
    @Path("/fragment/{documentID}/{elementID:.+}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public String getDocumentFragment(@PathParam("documentID") final String documentID, @PathParam("xpathExpression") final String xpathExpression) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private Map<String, Object> getConvertors() {
        return new HashMap<String, Object>();
    }

    // Spring setter ---------------------

    public void setDocuments(Map<String, Resource> documents) {
        this.documents = documents;
    }
}
