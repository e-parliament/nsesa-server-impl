package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.AmendableWidgetReference;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.repository.AmendableWidgetReferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("amendableWidgetReferenceConvertor")
@Lazy
public class AmendableWidgetReferenceConvertor implements ValueConverter {

    @Autowired
    AmendableWidgetReferenceRepository amendableWidgetReferenceRepository;

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        final AmendmentContainer amendmentContainer = (AmendmentContainer) object;
        if (amendmentContainer.getSourceReference() != null) {
            // create DTO
            org.nsesa.server.dto.AmendableWidgetReference dto = new org.nsesa.server.dto.AmendableWidgetReference();
            dto.setPath(amendmentContainer.getSourceReference().getPath());
            dto.setCreation(amendmentContainer.getSourceReference().isCreation());
            dto.setNamespaceURI(amendmentContainer.getSourceReference().getNamespaceURI());
            dto.setOffset(amendmentContainer.getSourceReference().getOffset());
            dto.setSibling(amendmentContainer.getSourceReference().isSibling());
            dto.setType(amendmentContainer.getSourceReference().getType());
            dto.setReferenceID(amendmentContainer.getSourceReference().getReferenceID());
            return dto;
        }
        return null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;
        org.nsesa.server.dto.AmendableWidgetReference dto = (org.nsesa.server.dto.AmendableWidgetReference) object;
        AmendableWidgetReference amendableWidgetReference = amendableWidgetReferenceRepository.findByReferenceID(dto.getReferenceID());
        if (amendableWidgetReference == null) amendableWidgetReference = new AmendableWidgetReference();

        amendableWidgetReference.setPath(dto.getPath());
        amendableWidgetReference.setCreation(dto.isCreation());
        amendableWidgetReference.setNamespaceURI(dto.getNamespaceURI());
        amendableWidgetReference.setOffset(dto.getOffset());
        amendableWidgetReference.setSibling(dto.isSibling());
        amendableWidgetReference.setType(dto.getType());
        amendableWidgetReference.setReferenceID(dto.getReferenceID());

        if (oldEntity instanceof AmendmentContainer) {
            ((AmendmentContainer)oldEntity).setSourceReference(amendableWidgetReference);
        }
        return amendableWidgetReference;
    }
}
