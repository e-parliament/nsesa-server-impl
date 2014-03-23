package org.nsesa.server.service.impl;

import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import com.inspiresoftware.lib.dto.geda.assembler.dsl.impl.DefaultDSLRegistry;
import org.apache.cxf.annotations.GZIP;
import org.nsesa.server.domain.Group;
import org.nsesa.server.domain.Membership;
import org.nsesa.server.domain.Person;
import org.nsesa.server.dto.GroupDTO;
import org.nsesa.server.dto.PersonDTO;
import org.nsesa.server.repository.GroupRepository;
import org.nsesa.server.repository.PersonRepository;
import org.nsesa.server.service.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * Date: 11/03/13 15:52
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@WebService(endpointInterface = "org.nsesa.server.service.api.PersonService", serviceName = "PersonService")
@GZIP
@Path("/person")
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ValueConverter membershipToGroupConvertor;

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;

    private final Assembler personAssembler = DTOAssembler.newAssembler(PersonDTO.class, Person.class);
    private final Assembler groupAssembler = DTOAssembler.newAssembler(GroupDTO.class, Group.class);

    @PostConstruct
    void init() {
        // You cannot have a transaction here with the @Transactional annotation, so we set it up ourselves
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final Group groupA = new Group();
                groupA.setName("Group A");
                groupA.setDescription("Group A");
                groupA.setGroupID("groupA");
                groupRepository.save(groupA);

                final Group groupB = new Group();
                groupB.setName("Group B");
                groupB.setDescription("Group B");
                groupB.setGroupID("groupB");
                groupRepository.save(groupB);

                final Random r = new Random(102381931414l);

                for (int i = 1; i < 11; i++) {
                    Person byUsername = personRepository.findByUsername("mp" + i);
                    if (byUsername == null) {
                        byUsername = new Person("personID" + i, "mp" + i, "MP " + i, "MP");

                        if (r.nextBoolean()) {
                            Membership membership = new Membership(groupA, byUsername);
                            byUsername.getMemberships().add(membership);
                        }
                        if (r.nextBoolean()) {
                            Membership membership = new Membership(groupB, byUsername);
                            byUsername.getMemberships().add(membership);
                        }
                        personRepository.save(byUsername);
                    }
                }
            }

        });
    }

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

    @GET
    @Path("/query")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<PersonDTO> getPersons(@QueryParam("q") String personQuery,
                                      @QueryParam("start") @DefaultValue("0") int start,
                                      @QueryParam("limit") @DefaultValue("20") int limit) {
        if (personQuery == null || "".equalsIgnoreCase(personQuery.trim())) return null;

        List<PersonDTO> personDTOs = new ArrayList<PersonDTO>();
        final List<Person> persons = personRepository.findByLastNameLikeOrderByLastNameDesc(personQuery.toLowerCase(), new PageRequest(start, limit));
        personAssembler.assembleDtos(personDTOs, persons, getConvertors(), new DefaultDSLRegistry());
        return personDTOs;
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

    @GET
    @Path("/groups/{personID}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML})
    @Transactional(readOnly = true)
    @Override
    public List<GroupDTO> getGroups(@PathParam("personID") String personID) {
        List<Group> byMembership = groupRepository.findByMembership(personID);
        List<GroupDTO> groupDTOs = new ArrayList<GroupDTO>();
        groupAssembler.assembleDtos(groupDTOs, byMembership, getConvertors(), new DefaultDSLRegistry());
        return groupDTOs;
    }

    private Map<String, Object> getConvertors() {
        return new HashMap<String, Object>() {
            {
                put("membershipToGroupConvertor", membershipToGroupConvertor);
            }
        };
    }
}
