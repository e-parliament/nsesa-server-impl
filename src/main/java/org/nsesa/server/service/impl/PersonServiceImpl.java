package org.nsesa.server.service.impl;

import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.inspiresoftware.lib.dto.geda.assembler.dsl.impl.DefaultDSLRegistry;
import org.apache.cxf.annotations.GZIP;
import org.nsesa.server.domain.Person;
import org.nsesa.server.dto.PersonDTO;
import org.nsesa.server.repository.PersonRepository;
import org.nsesa.server.service.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Date: 11/03/13 15:52
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@WebService(endpointInterface = "org.nsesa.server.service.api.PersonService", serviceName = "PersonService")
@GZIP
@Path("/person")
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepository personRepository;

    private final Assembler personAssembler = DTOAssembler.newAssembler(PersonDTO.class, Person.class);

    @GET
    @Path("/id/{personID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional
    @Override
    public PersonDTO getPerson(@PathParam("personID") String personID) {

        Person person = personRepository.findByPersonID(personID);
        if (person == null) {
            person = new Person(personID, "guest-" + UUID.randomUUID().toString(), "Guest", "GUEST");
            personRepository.save(person);
        }
        PersonDTO personDTO = new PersonDTO();
        personAssembler.assembleDto(personDTO, person, getConvertors(), new DefaultDSLRegistry());
        return personDTO;
    }

    @GET
    @Path("/username/{username}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional
    @Override
    public PersonDTO getPersonByUsername(@PathParam("username") String username) {
        Person person = personRepository.findByUsername(username);
        if (person == null) {
            person = new Person(UUID.randomUUID().toString(), username, username, "GUEST");
            personRepository.save(person);
        }
        PersonDTO personDTO = new PersonDTO();
        personAssembler.assembleDto(personDTO, person, getConvertors(), new DefaultDSLRegistry());
        return personDTO;
    }

    @POST
    @Path("/create")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional()
    @Override
    public void save(PersonDTO personDTO) {
        Person person = new Person();
        personAssembler.assembleEntity(personDTO, person, getConvertors(), new DefaultDSLRegistry());
        personRepository.save(person);
    }

    private Map<String, Object> getConvertors() {
        return new HashMap<String, Object>();
    }
}
