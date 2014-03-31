package org.nsesa.server.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 12/03/13 11:53
 *
 * @author <a href="mailto:philip.luppens@gmail.com">Philip Luppens</a>
 * @version $Id$
 */
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ID;

    @Column(nullable = false, length = 64)
    private String personID;

    /**
     * The username of this person.
     */
    @Column(length = 64)
    private String username;

    /**
     * The first name of this person.
     */
    @Column(nullable = false, length = 64)
    private String name;

    /**
     * The family name of this person.
     */
    @Column(nullable = false, length = 64)
    private String lastName;

    @OneToMany(mappedBy = "person", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Membership> memberships = new HashSet<Membership>();

    public Person() {
    }

    public Person(String personID, String username, String name, String lastName) {
        this.personID = personID;
        this.username = username;
        this.name = name;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person personDTO = (Person) o;

        if (ID != null ? !ID.equals(personDTO.ID) : personDTO.ID != null) return false;
        if (lastName != null ? !lastName.equals(personDTO.lastName) : personDTO.lastName != null) return false;
        if (name != null ? !name.equals(personDTO.name) : personDTO.name != null) return false;
        if (username != null ? !username.equals(personDTO.username) : personDTO.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }

    /**
     * Returns the display name of this person (name + space + family name to upper case)
     *
     * @return the display name
     */
    @Transient
    public String getDisplayName() {
        return name + " " + lastName.toUpperCase();
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long id) {
        this.ID = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
