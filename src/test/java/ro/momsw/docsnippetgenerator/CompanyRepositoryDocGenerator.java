package ro.momsw.docsnippetgenerator;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ro.momsw.companyws.CompanyWSApplication;
import ro.momsw.companyws.service.CompanyRepository;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CompanyWSApplication.class})
@AutoConfigureRestDocs(outputDir = "target/doc", uriPort = 80, uriHost = "company-bo-companybo.1d35.starter-us-east-1.openshiftapps.com")
@AutoConfigureMockMvc
public class CompanyRepositoryDocGenerator {

    @Autowired
    private MockMvc mockMvc;

    public static final String companiesUrlTemplate = "/companies";
    public static final String ownersUrlTemplate = "/owners";

    @Autowired
    private CompanyRepository companyRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        companyRepository.deleteAll();
    }

    @Test
    public void shouldReturnDocForEntryPoints() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andDo(document("home"));
        this.mockMvc.perform(get("/companies")).andDo(print()).andExpect(status().isOk()).andDo(document("companies"));
        this.mockMvc.perform(get("/owners")).andDo(print()).andExpect(status().isOk()).andDo(document("owners"));
    }

    @Test
    public void shouldReturnDocForCreateCompany() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated()).andExpect(header().string("Location", CoreMatchers.containsString("companies/"))).
                andDo(document("company-create",
                        requestFields(
                                fieldWithPath("name").description("Company name (mandatory)"),
                                fieldWithPath("address").description("Address of the company (mandatory)"),
                                fieldWithPath("city").description("City where company is located (mandatory)"),
                                fieldWithPath("country").description("Country where company is located (mandatory)"),
                                fieldWithPath("email").description("Email where company can be contacted"),
                                fieldWithPath("phoneNumber").description("Phone number where company can be contacted")
                        )));
    }

    @Test
    public void shouldReturnDocForGetDetailsOfCompany() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andDo(document("company-get"));
    }

    @Test
    public void shouldReturnDocForGetOwnersOfCompany() throws Exception {
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

        //create another owner
        ownerMVCResult = mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.oancea@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Oancea\"}")).andExpect(
                status().isCreated()).andReturn();
        ownerRESTLocation = ownerMVCResult.getResponse().getHeader("Location");

        //add beneficial owner to the company
        mockMvc.perform(post(companyBeneficialOwnersRESTLocation).contentType("text/uri-list").content(ownerRESTLocation));

        mockMvc.perform(get(companyBeneficialOwnersRESTLocation)).andExpect(status().isOk()).
                andDo(document("company-get-beneficialOwners"));
    }

    @Test
    public void shouldReturnDocForAddOwnerToCompany() throws Exception {
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
        mockMvc.perform(post(companyBeneficialOwnersRESTLocation).contentType("text/uri-list").content(ownerRESTLocation)).
                andDo(document("company-add-beneficialOwner"));

        mockMvc.perform(get(companyBeneficialOwnersRESTLocation)).andExpect(status().isOk()).
                andDo(document("company-add-beneficialOwner-check"));
    }

    @Test
    public void shouldReturnDocForUpdateCompany() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(put(location).content(
                "{  \"name\" : \"MOMENTUM SOFTWARE 2\",  \"address\" : \"Valea Frumoasei 10\", \"city\" : \"Sibiu 550310\", \"country\" : \"RO\", \"email\" : \"office2@momentum-software.ro\", \"phoneNumber\" : \"+401\"}")).
                andExpect(status().isNoContent()).
                andDo(document("company-update",
                        requestFields(
                                fieldWithPath("name").description("Company name"),
                                fieldWithPath("address").description("Address of the company"),
                                fieldWithPath("city").description("City where company is located"),
                                fieldWithPath("country").description("Country where company is located"),
                                fieldWithPath("email").description("Email where company can be contacted"),
                                fieldWithPath("phoneNumber").description("Phone number where company can be contacted")
                        )));
        }

    @Test
    public void sholdReturnDocForListOfCompanies() throws Exception {
        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM SOFTWARE\",  \"address\" : \"Valea Frumoasei\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-software.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated());

        mockMvc.perform(post(companiesUrlTemplate).content(
                "{\"name\" : \"MOMENTUM CONSULTING\",  \"address\" : \"Other Street\", \"city\" : \"Sibiu\", \"country\" : \"Romania\", \"email\" : \"office@momentum-consulting.ro\", \"phoneNumber\" : \"+40\"}")).
                andExpect(status().isCreated());

        mockMvc.perform(
                get(companiesUrlTemplate)).
                andExpect(status().isOk()).
                andDo(document("company-list"));

    }

    @TestConfiguration
    static class CustomizationConfiguration
            implements RestDocsMockMvcConfigurationCustomizer {

        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.snippets().withTemplateFormat(TemplateFormats.asciidoctor());
        }

    }
}