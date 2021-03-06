package org.nsesa.server.repository;

import org.nsesa.server.domain.AmendmentContainer;
import org.nsesa.server.domain.Document;
import org.nsesa.server.domain.Group;
import org.nsesa.server.domain.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Date: 12/03/13 13:53
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Repository
public interface AmendmentContainerRepository extends PagingAndSortingRepository<AmendmentContainer, Long> {

    List<AmendmentContainer> findByDocumentAndPersonAndLatestRevision(Document document, Person person, Boolean latestRevision);

    @Query("select a from AmendmentContainer a where a.document = ?1 and ?2 member of a.groups and a.latestRevision = ?3")
    List<AmendmentContainer> findByDocumentAndPersonAndLatestRevision(Document document, Group group, Boolean latestRevision);

    List<AmendmentContainer> findByAmendmentContainerIDOrderByCreationDateDesc(String amendmentContainerID);

    List<AmendmentContainer> findByDocument(Document document);

    AmendmentContainer findByRevisionID(String revisionID);

    AmendmentContainer findByAmendmentContainerIDAndLatestRevision(String amendmentContainerID, Boolean latestRevision);
}
