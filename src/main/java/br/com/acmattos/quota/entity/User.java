package br.com.acmattos.quota.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {
    @Id
    private String id;
    @Column(name="first_name")
    @NotBlank
    private String firstName;
    @Column(name="last_name")
    @NotBlank
    private String lastName;
    @Column(name="last_login_time_utc")
    private LocalDateTime lastLoginTimeUtc;

    public User(){}

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDateTime getLastLoginTimeUtc() {
        return lastLoginTimeUtc;
    }

    public User setLastLoginTimeUtc(LocalDateTime lastLoginTimeUtc) {
        this.lastLoginTimeUtc = lastLoginTimeUtc;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id)
            && Objects.equals(firstName, user.firstName)
            && Objects.equals(lastName, user.lastName)
            && Objects.equals(lastLoginTimeUtc, user.lastLoginTimeUtc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, lastLoginTimeUtc);
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", lastLoginTimeUtc=" + lastLoginTimeUtc +
            '}';
    }
}
