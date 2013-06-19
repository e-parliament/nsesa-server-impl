package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.AmendableWidgetReference;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.dto.AmendmentAction;
import org.nsesa.server.repository.AmendableWidgetReferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("amendmentActionConvertor")
@Lazy
public class AmendmentActionConvertor implements ValueConverter {

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        final AmendmentContainer amendmentContainer = (AmendmentContainer) object;
        if (amendmentContainer.getAmendmentAction() != null) {
            return AmendmentAction.valueOf(amendmentContainer.getAmendmentAction().name());
        }
        return null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;
        AmendmentAction dto = (AmendmentAction) object;

        if (oldEntity instanceof AmendmentContainer) {
            final org.nsesa.server.domain.AmendmentAction amendmentAction = org.nsesa.server.domain.AmendmentAction.valueOf(dto.name());
            ((AmendmentContainer)oldEntity).setAmendmentAction(amendmentAction);
            return amendmentAction;
        }
        return null;
    }
}
