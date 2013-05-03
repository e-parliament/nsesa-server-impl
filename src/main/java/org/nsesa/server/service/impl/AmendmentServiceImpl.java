package org.nsesa.server.service.impl;

import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.inspiresoftware.lib.dto.geda.assembler.dsl.impl.DefaultDSLRegistry;
import org.apache.cxf.annotations.GZIP;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Document;
import org.nsesa.server.domain.Person;
import org.nsesa.server.dto.AmendableWidgetReference;
import org.nsesa.server.dto.AmendmentAction;
import org.nsesa.server.dto.AmendmentContainerDTO;
import org.nsesa.server.repository.AmendmentContainerRepository;
import org.nsesa.server.repository.DocumentRepository;
import org.nsesa.server.repository.PersonRepository;
import org.nsesa.server.service.api.AmendmentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
    @Path("/id/{amendmentContainerID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Override
    public AmendmentContainerDTO getAmendmentContainer(@PathParam("amendmentContainerID") String amendmentContainerID) {
        final AmendmentContainerDTO amendmentContainerDTO = new AmendmentContainerDTO();
        amendmentContainerDTO.setAmendmentContainerID(amendmentContainerID);
        amendmentContainerDTO.setAmendmentAction(AmendmentAction.MODIFICATION);
        amendmentContainerDTO.setSourceReference(new AmendableWidgetReference("//akomaNtoso[0]/foo[1]"));
        return amendmentContainerDTO;
    }

    @GET
    @Path("/document/{documentID}/{personID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
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
    @Path("/document/{documentID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
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

    @POST
    @Path("/save")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Override
    public AmendmentContainerDTO save(final AmendmentContainerDTO amendmentContainerDTO) {

        // find existing
        AmendmentContainer amendmentContainer = amendmentContainerRepository.findByAmendmentContainerID(amendmentContainerDTO.getAmendmentContainerID());
        if (amendmentContainer == null) amendmentContainer = new AmendmentContainer();
        amendmentContainerAssembler.assembleEntity(amendmentContainerDTO, amendmentContainer, getConvertors(), new DefaultDSLRegistry());
        amendmentContainer = amendmentContainerRepository.save(amendmentContainer);
        amendmentContainerAssembler.assembleDto(amendmentContainerDTO, amendmentContainer, getConvertors(), new DefaultDSLRegistry());
        return amendmentContainerDTO;
    }

    @POST
    @Path("/delete/{amendmentContainerID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Override
    public void delete(final @PathParam("amendmentContainerID") String amendmentContainerID) {
        final AmendmentContainer container = amendmentContainerRepository.findByAmendmentContainerID(amendmentContainerID);
        if (container != null) {
            amendmentContainerRepository.delete(container);
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
