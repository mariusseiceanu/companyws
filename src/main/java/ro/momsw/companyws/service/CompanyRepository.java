package ro.momsw.companyws.service;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ro.momsw.companyws.entity.Company;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.validation.ConstraintViolationException;
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

    // Convert a predefined exception to an HTTP Status code
    @ResponseStatus(value= HttpStatus.CONFLICT,
            reason="Data integrity violation")  // 409
    @ExceptionHandler(ConstraintViolationException.class)
    default public void conflict() {

    }
}
