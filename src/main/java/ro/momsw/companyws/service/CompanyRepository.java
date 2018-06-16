package ro.momsw.companyws.service;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import ro.momsw.companyws.entity.Company;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "companies", path = "companies")
@Controller
public interface CompanyRepository extends PagingAndSortingRepository<Company, Long> {

    //disabled delete Company by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    //disabled delete Company by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void delete(Company company);

    //disabled delete Company by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends Company> company);

    //disabled delete Company by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteAll();

    List<Company> findByName(@Param("name") String name);

    List<Company> findByCountry(@Param("country") String country);
}
