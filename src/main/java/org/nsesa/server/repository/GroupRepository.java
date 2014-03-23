package org.nsesa.server.repository;

import org.nsesa.server.domain.Group;
import org.nsesa.server.domain.Person;
import org.springframework.data.domain.Pageable;
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
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {

    @Query("SELECT m.group FROM Membership m WHERE m.person.personID = ?1")
    List<Group> findByMembership(String personID);

    Group findByGroupID(String groupID);
}
