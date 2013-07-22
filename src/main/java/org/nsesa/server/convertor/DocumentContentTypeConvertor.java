package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.DocumentContent;
import org.nsesa.server.domain.DocumentContentType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("documentContentTypeConvertor")
@Lazy
public class DocumentContentTypeConvertor implements ValueConverter {

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        final DocumentContent documentContent = (DocumentContent) object;
        return documentContent.getDocumentContentType() != null ? documentContent.getDocumentContentType().toString() : null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;

        final DocumentContentType documentContentType = DocumentContentType.valueOf((String) object);
        if (oldEntity instanceof DocumentContent) {
            ((DocumentContent) oldEntity).setDocumentContentType(documentContentType);
        }
        return documentContentType;
    }
}
