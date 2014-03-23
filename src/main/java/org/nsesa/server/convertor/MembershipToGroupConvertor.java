package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.Membership;
import org.nsesa.server.domain.Person;
import org.nsesa.server.dto.GroupDTO;
import org.nsesa.server.repository.GroupRepository;
import org.nsesa.server.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("membershipToGroupConvertor")
@Lazy
public class MembershipToGroupConvertor implements ValueConverter {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    PersonRepository personRepository;

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        if (object instanceof Person) {
            Set<GroupDTO> groups = new HashSet<GroupDTO>();
            Person person = (Person) object;
            for (Membership membership : person.getMemberships()) {
                if (membership.isActive())
                    groups.add(new GroupDTO(membership.getGroup().getGroupID(), membership.getGroup().getName()));
            }
            return groups;
        }
        return null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;

        return null;
    }
}
