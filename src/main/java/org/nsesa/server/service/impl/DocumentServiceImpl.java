package org.nsesa.server.service.impl;

import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.inspiresoftware.lib.dto.geda.assembler.dsl.impl.DefaultDSLRegistry;
import org.apache.cxf.annotations.GZIP;
import org.nsesa.server.domain.Document;
import org.nsesa.server.dto.DocumentDTO;
import org.nsesa.server.repository.DocumentRepository;
import org.nsesa.server.service.api.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Date: 11/03/13 15:52
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@WebService(endpointInterface = "org.nsesa.server.service.api.DocumentService", serviceName = "DocumentService")
@GZIP
@Path("/document")
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;

    final Assembler documentAssembler = DTOAssembler.newAssembler(DocumentDTO.class, Document.class);

    @GET
    @Path("/id/{documentID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional()
    @Override
    public DocumentDTO getDocument(@PathParam("documentID") String documentID) {
        Document document = documentRepository.findByDocumentID(documentID);

        if (document == null) {
            // create new default document
            document = new Document();
            document.setCreationDate(GregorianCalendar.getInstance());
            final Calendar deadline = GregorianCalendar.getInstance();
            deadline.set(Calendar.HOUR_OF_DAY, 12);
            document.setDeadline(deadline);
            document.setAmendable(true);
            document.setLanguageIso("EN");
            document.setDocumentID(documentID);
            String documentName = "Document " + documentID;
            if (documentID.startsWith("http://")) {
                // loading a remote document
                documentName = "Document " + documentID.substring(documentID.lastIndexOf("/") + 1);
            }
            document.setName(documentName);
            documentRepository.save(document);
        }

        DocumentDTO documentDTO = new DocumentDTO();
        documentAssembler.assembleDto(documentDTO, document, getConvertors(), new DefaultDSLRegistry());
        return documentDTO;
    }

    @POST
    @Path("/save")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional()
    @Override
    public DocumentDTO saveDocument(DocumentDTO documentDTO) {
        Document document = documentRepository.findByDocumentID(documentDTO.getDocumentID());
        if (document == null) document = new Document();
        documentAssembler.assembleEntity(documentDTO, document, getConvertors(), new DefaultDSLRegistry());
        document = fromDocumentDTO(documentDTO);
        documentRepository.save(document);
        // since there might be changes (lock version, last modification date, etc ...)
        documentAssembler.assembleDto(documentDTO, document, getConvertors(), new DefaultDSLRegistry());
        return documentDTO;
    }

    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<DocumentDTO> list(@DefaultValue("0") @QueryParam("offset") int offset, @DefaultValue("5") @QueryParam("rows") int rows) {
        final Page<Document> page = documentRepository.findAll(new PageRequest(offset, rows));
        final List<DocumentDTO> documentDTOs = new ArrayList<DocumentDTO>();
        documentAssembler.assembleDtos(documentDTOs, page.getContent(), new HashMap<String, Object>(), new DefaultDSLRegistry());
        return documentDTOs;
    }

    @Override
    public List<DocumentDTO> getAvailableTranslations(@WebParam(name = "documentID") String documentID) {
        // TODO
        return new ArrayList<DocumentDTO>();
    }

    @Override
    public List<DocumentDTO> getRelatedDocuments(@WebParam(name = "documentID") String documentID) {
        // TODO
        return new ArrayList<DocumentDTO>();
    }

    private Document fromDocumentDTO(DocumentDTO documentDTO) {
        Document document = new Document();
        document.setDocumentID(documentDTO.getDocumentID());
        document.setName(documentDTO.getName());
        document.setLanguageIso(documentDTO.getLanguageIso());
        document.setAmendable(documentDTO.isAmendable());
        document.setDeadline(documentDTO.getDeadline());
        return document;
    }

    private Map<String, Object> getConvertors() {
        return new HashMap<String, Object>();
    }
}
