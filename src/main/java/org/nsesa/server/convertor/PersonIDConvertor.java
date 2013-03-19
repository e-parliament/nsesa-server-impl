package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Document;
import org.nsesa.server.domain.Person;
import org.nsesa.server.repository.DocumentRepository;
import org.nsesa.server.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("personIDConvertor")
@Lazy
public class PersonIDConvertor implements ValueConverter {

    @Autowired
    PersonRepository personRepository;

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        final AmendmentContainer amendmentContainer = (AmendmentContainer) object;
        return amendmentContainer.getPerson() != null ? amendmentContainer.getPerson().getPersonID() : null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;

        final Person person = personRepository.findByPersonID((String) object);
        if (oldEntity instanceof AmendmentContainer) {
            ((AmendmentContainer)oldEntity).setPerson(person);
        }
        return person;
    }
}
