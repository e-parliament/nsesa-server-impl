package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Group;
import org.nsesa.server.dto.GroupDTO;
import org.nsesa.server.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Philip Luppens on 23/03/14 22:45.
 * TODO Complete this class documentation.
 */
@Component("groupConvertor")
@Lazy
public class GroupConvertor implements ValueConverter {

    @Autowired
    GroupRepository groupRepository;

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {
        if (object == null) return null;
        if (object instanceof AmendmentContainer) {
            Set<GroupDTO> shared = new HashSet<GroupDTO>();
            AmendmentContainer amendmentContainer = (AmendmentContainer) object;
            for (Group fromServer : amendmentContainer.getGroups()) {
                shared.add(new GroupDTO(fromServer.getGroupID(), fromServer.getName()));
            }
            return shared;
        }
        return null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {
        Set<GroupDTO> groups = (Set)object;
        Set<Group> domain = new HashSet<Group>();

        AmendmentContainer amendmentContainer = (AmendmentContainer) oldEntity;
        for (GroupDTO groupDTO : groups) {
            domain.add(groupRepository.findByGroupID(groupDTO.getGroupID()));
        }
        return domain;
    }
}
