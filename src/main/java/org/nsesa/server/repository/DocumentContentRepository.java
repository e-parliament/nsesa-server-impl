package org.nsesa.server.repository;

import org.nsesa.server.domain.DocumentContent;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Date: 12/03/13 13:53
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Repository
public interface DocumentContentRepository extends PagingAndSortingRepository<DocumentContent, Long> {

    DocumentContent findByDocumentID(String documentID);
}
