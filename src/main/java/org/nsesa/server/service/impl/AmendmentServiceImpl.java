package org.nsesa.server.service.impl;

import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.inspiresoftware.lib.dto.geda.assembler.dsl.impl.DefaultDSLRegistry;
import org.apache.cxf.annotations.GZIP;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Document;
import org.nsesa.server.domain.Person;
import org.nsesa.server.dto.AmendmentContainerDTO;
import org.nsesa.server.exception.ResourceNotFoundException;
import org.nsesa.server.exception.StaleResourceException;
import org.nsesa.server.exception.ValidationException;
import org.nsesa.server.repository.AmendmentContainerRepository;
import org.nsesa.server.repository.DocumentRepository;
import org.nsesa.server.repository.PersonRepository;
import org.nsesa.server.service.api.AmendmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

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
@WebService(endpointInterface = "org.nsesa.server.service.api.AmendmentService", serviceName = "AmendmentService")
@GZIP
@Path("/amendment")
public class AmendmentServiceImpl implements AmendmentService {

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AmendmentContainerRepository amendmentContainerRepository;

    @Autowired
    ValueConverter documentIDConvertor;

    @Autowired
    ValueConverter personIDConvertor;

    @Autowired
    ValueConverter amendableWidgetReferenceConvertor;

    @Autowired
    ValueConverter amendmentActionConvertor;

    private final Assembler amendmentContainerAssembler = DTOAssembler.newAssembler(AmendmentContainerDTO.class, AmendmentContainer.class);

    @GET
    @Path("/document/{documentID}/{personID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<AmendmentContainerDTO> getAmendmentContainersByDocumentAndPerson(@PathParam("documentID") String documentID, @PathParam("personID") String personID) {
        final Person person = personRepository.findByPersonID(personID);
        if (person != null) {
            final Document document = documentRepository.findByDocumentID(documentID);
            if (document != null) {
                final List<AmendmentContainer> amendmentContainers = amendmentContainerRepository.findByDocumentAndPerson(document, person);
                final List<AmendmentContainerDTO> amendmentContainerDTOs = new ArrayList<AmendmentContainerDTO>();
                amendmentContainerAssembler.assembleDtos(amendmentContainerDTOs, amendmentContainers, getConvertors(), new DefaultDSLRegistry());
                return amendmentContainerDTOs;
            }
        }
        return null;
    }

    @GET
    @Path("/id/{amendmentContainerID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public AmendmentContainerDTO getAmendmentContainer(@PathParam("amendmentContainerID") String amendmentContainerID) {
        final AmendmentContainerDTO amendmentContainerDTO = new AmendmentContainerDTO();
        final List<AmendmentContainer> amendmentContainers = amendmentContainerRepository.findByAmendmentContainerIDOrderByCreationDateDesc(amendmentContainerID, new PageRequest(0, 1));
        if (amendmentContainers != null && !amendmentContainers.isEmpty()) {
            amendmentContainerAssembler.assembleDto(amendmentContainerDTO, amendmentContainers.get(0), getConvertors(), new DefaultDSLRegistry());
            return amendmentContainerDTO;
        }
        return null;
    }

    @GET
    @Path("/document/{documentID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<AmendmentContainerDTO> getAmendmentContainersByDocument(@PathParam("documentID") String documentID) {
        final Document document = documentRepository.findByDocumentID(documentID);
        if (document != null) {
            final List<AmendmentContainer> amendmentContainers = amendmentContainerRepository.findByDocument(document);
            final List<AmendmentContainerDTO> amendmentContainerDTOs = new ArrayList<AmendmentContainerDTO>();
            amendmentContainerAssembler.assembleDtos(amendmentContainerDTOs, amendmentContainers, getConvertors(), new DefaultDSLRegistry());
            return amendmentContainerDTOs;
        }
        return null;
    }

    @GET
    @Path("/revision/{revisionID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public AmendmentContainerDTO getAmendmentContainerVersion(@PathParam("revisionID") String revisionID) {
        AmendmentContainerDTO amendmentContainerDTO = new AmendmentContainerDTO();
        AmendmentContainer amendmentContainer = amendmentContainerRepository.findByRevisionID(revisionID);
        if (amendmentContainer != null) {
            amendmentContainerAssembler.assembleDto(amendmentContainerDTO, amendmentContainer, getConvertors(), new DefaultDSLRegistry());
            return amendmentContainerDTO;
        }
        return null;
    }

    @GET
    @Path("/revisions/{amendmentContainerID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<String> getAmendmentContainerVersions(@PathParam("amendmentContainerID") String amendmentContainerID) {
        final List<AmendmentContainer> amendmentContainers = amendmentContainerRepository.findByAmendmentContainerID(amendmentContainerID);
        final List<String> revisionIDs = new ArrayList<String>();
        for (final AmendmentContainer amendmentContainer : amendmentContainers) {
            revisionIDs.add(amendmentContainer.getRevisionID());
        }
        return revisionIDs;
    }

    @POST
    @Path("/save")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional()
    @Override
    public AmendmentContainerDTO save(final AmendmentContainerDTO amendmentContainerDTO) throws StaleResourceException, ResourceNotFoundException, ValidationException {
        // see if we already have an existing amendment under this revision
        final List<AmendmentContainer> existing = amendmentContainerRepository.findByAmendmentContainerIDOrderByCreationDateDesc(amendmentContainerDTO.getAmendmentContainerID(), new PageRequest(0, 1));
        if (existing != null && !existing.isEmpty()) {
            // check to verify we have the latest
            if (amendmentContainerDTO.getRevisionID().equals(existing.get(0).getRevisionID())) {
                // set new revision
                amendmentContainerDTO.setRevisionID(UUID.randomUUID().toString());
            } else {
                // todo stale resource
                throw new StaleResourceException("Stale resource detected; given revisionID is " + amendmentContainerDTO.getRevisionID() + ", latest revision in database is " + existing.get(0).getRevisionID());
            }
        }

        AmendmentContainer amendmentContainer = new AmendmentContainer();
        amendmentContainerAssembler.assembleEntity(amendmentContainerDTO, amendmentContainer, getConvertors(), new DefaultDSLRegistry());

        // make sure to set the creation date if the amendment is new (as well as the modification date)
        // TODO use @BeforePersist callbacks
        final Calendar calendar = GregorianCalendar.getInstance();
        if (amendmentContainer.getCreationDate() == null) amendmentContainer.setCreationDate(calendar);
        amendmentContainer.setModificationDate(calendar);

        amendmentContainer = amendmentContainerRepository.save(amendmentContainer);
        amendmentContainerAssembler.assembleDto(amendmentContainerDTO, amendmentContainer, getConvertors(), new DefaultDSLRegistry());
        return amendmentContainerDTO;
    }

    @POST
    @Path("/delete/{amendmentContainerID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional()
    @Override
    public void delete(final @PathParam("amendmentContainerID") String amendmentContainerID) {
        final List<AmendmentContainer> amendmentContainers = amendmentContainerRepository.findByAmendmentContainerID(amendmentContainerID);
        for (final AmendmentContainer container : amendmentContainers) {
            if (container != null) {
                amendmentContainerRepository.delete(container);
            }
        }
    }

    private Map<String, Object> getConvertors() {
        return new HashMap<String, Object>() {
            {
                put("documentIDConvertor", documentIDConvertor);
                put("personIDConvertor", personIDConvertor);
                put("amendableWidgetReferenceConvertor", amendableWidgetReferenceConvertor);
                put("amendmentActionConvertor", amendmentActionConvertor);
            }
        };
    }
}
