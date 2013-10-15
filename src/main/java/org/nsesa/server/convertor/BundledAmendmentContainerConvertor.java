package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.AmendmentContainer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("bundledAmendmentContainerConvertor")
@Lazy
public class BundledAmendmentContainerConvertor implements ValueConverter {

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        final AmendmentContainer amendmentContainer = (AmendmentContainer) object;

        String[] amendmentContainerIDs = new String[amendmentContainer.getBundledAmendmentContainers().size()];
        int index = 0;
        for (String amendmentContainerID : amendmentContainer.getBundledAmendmentContainers()) {
            amendmentContainerIDs[index] = amendmentContainerID;
            index++;
        }
        return amendmentContainerIDs;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;

        final String[] amendmentContainerIDs = (String[]) object;

        if (oldEntity instanceof AmendmentContainer) {
            final AmendmentContainer parent = (AmendmentContainer) oldEntity;
            for (final String amendmentContainerID : amendmentContainerIDs) {
                parent.getBundledAmendmentContainers().add(amendmentContainerID);
            }
        }
        return amendmentContainerIDs;
    }
}
