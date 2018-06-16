package ro.momsw.docsnippetgenerator;

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
import ro.momsw.companyws.CompanyWSApplication;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CompanyWSApplication.class})
@AutoConfigureRestDocs(outputDir = "target/doc", uriPort = 80, uriHost = "company-bo-companybo.1d35.starter-us-east-1.openshiftapps.com")
@AutoConfigureMockMvc
public class EntryPointDocGenerator {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void shouldReturnDocForEntryPoints() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andDo(document("home"));
        this.mockMvc.perform(get("/companies")).andDo(print()).andExpect(status().isOk()).andDo(document("companies"));
        this.mockMvc.perform(get("/owners")).andDo(print()).andExpect(status().isOk()).andDo(document("owners"));
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