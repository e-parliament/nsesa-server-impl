package org.nsesa.server.repository;

import org.nsesa.server.domain.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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
public interface PersonRepository extends PagingAndSortingRepository<Person, Long> {

    Person findByUsername(String username);

    @Query("SELECT p FROM Person p WHERE LOWER(p.lastName) LIKE ?1")
    List<Person> findByLastNameLikeOrderByLastNameDesc(String lastNameQuery, Pageable pageable);

    Person findByPersonID(String personID);
}
