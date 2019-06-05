package com.esgi.prendsplace2.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Interest.
 */
@Entity
@Table(name = "interest")
public class Interest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "interests")
    @JsonIgnore
    private Set<Event> events = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "interest_users",
               joinColumns = @JoinColumn(name = "interest_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"))
    private Set<User> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Interest name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public Interest events(Set<Event> events) {
        this.events = events;
        return this;
    }

    public Interest addEvents(Event event) {
        this.events.add(event);
        event.getInterests().add(this);
        return this;
    }

    public Interest removeEvents(Event event) {
        this.events.remove(event);
        event.getInterests().remove(this);
        return this;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Interest users(Set<User> users) {
        this.users = users;
        return this;
    }

    public Interest addUsers(User user) {
        this.users.add(user);
        return this;
    }

    public Interest removeUsers(User user) {
        this.users.remove(user);
        return this;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interest)) {
            return false;
        }
        return id != null && id.equals(((Interest) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Interest{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
