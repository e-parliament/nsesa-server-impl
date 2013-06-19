package org.nsesa.server.convertor;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Document;
import org.nsesa.server.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Date: 13/03/13 14:35
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Component("documentIDConvertor")
@Lazy
public class DocumentIDConvertor implements ValueConverter {

    @Autowired
    DocumentRepository documentRepository;

    @Override
    public Object convertToDto(Object object, BeanFactory beanFactory) {

        if (object == null) return null;

        final AmendmentContainer amendmentContainer = (AmendmentContainer) object;
        return amendmentContainer.getDocument() != null ? amendmentContainer.getDocument().getDocumentID() : null;
    }

    @Override
    public Object convertToEntity(Object object, Object oldEntity, BeanFactory beanFactory) {

        if (object == null) return null;

        final Document document = documentRepository.findByDocumentID((String) object);
        if (oldEntity instanceof AmendmentContainer) {
            ((AmendmentContainer)oldEntity).setDocument(document);
        }
        return document;
    }
}
