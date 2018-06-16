package ro.momsw.companyws;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ro.momsw.companyws.service.CompanyRepository;


import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CompanyRepositoryTest {

    public static final String companiesUrlTemplate = "/companies";
    public static final String ownersUrlTemplate = "/owners";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        companyRepository.deleteAll();
    }

    @Test
    public void shouldReturnRepositoryIndex() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$._links.companies").exists());
    }

    @Test
    public void shouldCreateCompany() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated()).
                andExpect(header().string("Location", containsString("companies/")));
    }

    @Test
    public void shouldCreateCompanyWithoutOptionalAttribtues() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\"}")).
                andExpect(status().isCreated()).
                andExpect(header().string("Location", containsString("companies/")));
    }

    @Test
    public void shouldNotCreateCompanyWithNullName() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateCompanyWithEmptyName() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"\", \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateCompanyWithoutAddress() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateCompanyWithoutCity() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\", \"address\" : \"Valea Frumoasei\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateCompanyWithoutCountry() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\", \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldRetrieveCompany() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andExpect(jsonPath("$.name").value("MOMENTUM SOFTWARE")).
                andExpect(jsonPath("$.address").value("Valea Frumoasei")).
                andExpect(jsonPath("$.city").value("Sibiu")).
                andExpect(jsonPath("$.country").value("Romania")).
                andExpect(jsonPath("$.email").value("office@momentum-software.ro")).
                andExpect(jsonPath("$.phoneNumber").value("+40"));
    }

    @Test
    public void shouldQueryCompanyByNameWithOneResult() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated());

        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM CONSULTING\",  \"address\" : \"Other Street\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-consulting.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated());

        mockMvc.perform(
                get(companiesUrlTemplate + "/search/findByName?name={name}", "MOMENTUM SOFTWARE")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$._embedded.companies[0].name").value("MOMENTUM SOFTWARE")).
                andExpect(jsonPath("$._embedded.companies[0].country").value("Romania")).
                andExpect(jsonPath("$._embadded.companies[1]").doesNotHaveJsonPath());

    }

    @Test
    public void shouldUpdateAllFieldsOfCompany() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(put(location).content(
                "{  \"name\" : \"MOMENTUM SOFTWARE 2\",  \"address\" : \"Valea Frumoasei 10\", \"city\" : \"Sibiu 550310\", \"country\" : \"RO\", \"email\" : \"office2@momentum-software.ro\", \"phoneNumber\" : \"+401\"}")).
                andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andExpect(jsonPath("$.name").value("MOMENTUM SOFTWARE 2")).
                andExpect(jsonPath("$.address").value("Valea Frumoasei 10")).
                andExpect(jsonPath("$.city").value("Sibiu 550310")).
                andExpect(jsonPath("$.country").value("RO")).
                andExpect(jsonPath("$.email").value("office2@momentum-software.ro")).
                andExpect(jsonPath("$.phoneNumber").value("+401"));
    }

    @Test
    public void shouldPartiallyUpdateCompany() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(
                patch(location).content("{\"city\": \"Sibiu 550310\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andExpect(jsonPath("$.name").value("MOMENTUM SOFTWARE")).
                andExpect(jsonPath("$.address").value("Valea Frumoasei")).
                andExpect(jsonPath("$.city").value("Sibiu 550310")).
                andExpect(jsonPath("$.country").value("Romania")).
                andExpect(jsonPath("$.email").value("office@momentum-software.ro")).
                andExpect(jsonPath("$.phoneNumber").value("+40"));
    }

    @Test
    public void shouldAddOwnerToCompany() throws Exception {

        MvcResult companyMVCResult = mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).andExpect(
                status().isCreated()).andReturn();

        String companyRESTLocation = companyMVCResult.getResponse().getHeader("Location");
        String companyBeneficialOwnersRESTLocation = companyRESTLocation + "/beneficialOwners";

        //create one owner
        MvcResult ownerMVCResult = mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).andExpect(
                status().isCreated()).andReturn();
        String ownerRESTLocation = ownerMVCResult.getResponse().getHeader("Location");

        //add beneficial owner to the company
        mockMvc.perform(post(companyBeneficialOwnersRESTLocation).contentType("text/uri-list").content(ownerRESTLocation));

        mockMvc.perform(get(companyBeneficialOwnersRESTLocation)).andExpect(status().isOk()).
                andExpect(jsonPath("$._embedded.owners[0].firstName").value("Marius")).
                andExpect(jsonPath("$._embedded.owners[0].lastName").value("Seiceanu")).
                andExpect(jsonPath("$._embedded.owners[1]").doesNotExist());
    }

//    @Test
//    public void shouldDeleteCompany() throws Exception {
//
//        MvcResult mvcResult = mockMvc.perform(post(companiesUrlTemplate).content(
//                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).andExpect(
//                status().isCreated()).andReturn();
//
//        String location = mvcResult.getResponse().getHeader("Location");
//        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
//
//        mockMvc.perform(get(location)).andExpect(status().isNotFound());
//    }

}
