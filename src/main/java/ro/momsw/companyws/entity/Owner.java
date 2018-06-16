package ro.momsw.companyws.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table (name = "Owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min = 1)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 1)
    @Column(nullable = false)
    private String firstName;

    @Size(min = 1)
    @Column(nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "beneficialOwners")
    private Set<Company> companies;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {

        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public Set<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(Set<Company> companies) {
        this.companies = companies;
    }
}
