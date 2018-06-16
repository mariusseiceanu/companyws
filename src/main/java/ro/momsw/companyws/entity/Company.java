package ro.momsw.companyws.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "Company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min=1)
    @Column(nullable = false)
    private String name;

    @Size(min=1)
    @Column(nullable = false)
    private String address;

    @Size(min=1)
    @Column(nullable = false)
    private String city;

    @Size(min=1)
    @Column(nullable = false)
    private String country;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "company_beneficialowner",
            joinColumns = @JoinColumn(name = "beneficialowner_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "company_id", referencedColumnName = "id"))
    private Set<Owner> beneficialOwners;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Owner> getBeneficialOwners() {
        return beneficialOwners;
    }

    public void setBeneficialOwners(Set<Owner> beneficialOwners) {
        this.beneficialOwners = beneficialOwners;
    }
}
