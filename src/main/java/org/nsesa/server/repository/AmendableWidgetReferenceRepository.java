package org.nsesa.server.repository;

import org.nsesa.server.domain.AmendableWidgetReference;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Date: 12/03/13 13:53
 *
 * @author <a href="philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Repository
public interface AmendableWidgetReferenceRepository extends PagingAndSortingRepository<AmendableWidgetReference, Long> {

    AmendableWidgetReference findByReferenceID(String referenceID);
}
