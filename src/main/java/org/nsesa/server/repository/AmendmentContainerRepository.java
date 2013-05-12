package org.nsesa.server.repository;

import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Document;
import org.nsesa.server.domain.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Date: 12/03/13 13:53
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Repository
public interface AmendmentContainerRepository extends PagingAndSortingRepository<AmendmentContainer, Long> {

    List<AmendmentContainer> findByDocumentAndPerson(Document document, Person person);

    List<AmendmentContainer> findByAmendmentContainerID(String amendmentContainerID);

    List<AmendmentContainer> findByDocument(Document document);

    List<AmendmentContainer> findLatestByDocument(Document document);

    AmendmentContainer findByRevisionID(String revisionID);

    List<AmendmentContainer> findByAmendmentContainerIDOrderByCreationDateDesc(String amendmentContainerID, Pageable pageable);
}
