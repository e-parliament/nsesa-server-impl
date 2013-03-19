package org.nsesa.server.repository;

import org.nsesa.server.domain.Document;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Date: 12/03/13 13:53
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Repository
public interface DocumentRepository extends PagingAndSortingRepository<Document, Long> {

    Document findByDocumentID(String documentID);
}
