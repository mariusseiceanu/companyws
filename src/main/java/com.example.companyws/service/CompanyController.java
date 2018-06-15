package com.example.companyws.service;

import com.example.companyws.entity.Company;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "company", path = "company")
public interface CompanyController extends PagingAndSortingRepository<Company, Long> {

    //disabled delete Company as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    //disabled delete Company as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void delete(Company company);

    //disabled delete Company as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends Company> company);

    //disabled delete Company as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteAll();

}
