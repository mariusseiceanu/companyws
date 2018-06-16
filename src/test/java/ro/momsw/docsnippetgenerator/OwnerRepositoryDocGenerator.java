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
import ro.momsw.companyws.service.OwnerRepository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CompanyWSApplication.class})
@AutoConfigureRestDocs(outputDir = "target/doc", uriPort = 80, uriHost = "company-bo-companybo.1d35.starter-us-east-1.openshiftapps.com")
@AutoConfigureMockMvc
public class OwnerRepositoryDocGenerator {

    public static final String ownersUrlTemplate = "/owners";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        ownerRepository.deleteAll();
    }

    @Test
    public void shouldCreateOwner() throws Exception {
        mockMvc.perform(post(ownersUrlTemplate).contentType("application/json").content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isCreated()).
                andExpect(header().string("Location", containsString("owners/"))).
                andDo(document("owner-create",
                requestFields(
                        fieldWithPath("email").description("Owner email address (mandatory, unique)"),
                        fieldWithPath("firstName").description("User first name (mandatory)"),
                        fieldWithPath("lastName").description("User last name (mandatory)")
                )));    }

    @TestConfiguration
    static class CustomizationConfiguration
            implements RestDocsMockMvcConfigurationCustomizer {

        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.snippets().withTemplateFormat(TemplateFormats.asciidoctor());
        }

    }
}