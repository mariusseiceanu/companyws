package ro.momsw.companyws.service;

import org.springframework.data.repository.query.Param;
import ro.momsw.companyws.entity.Owner;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "owners", path = "owners")
public interface OwnerRepository extends PagingAndSortingRepository<Owner, Long> {

    //disabled delete Owner by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    //disabled delete Owner by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void delete(Owner owner);

    //disabled delete Owner by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends Owner> owner);

    //disabled delete Owner by intent as this is not a requested functionality
    @Override
    @RestResource(exported = false)
    void deleteAll();

    List<Owner> findByEmail(@Param("email") String email);

}
