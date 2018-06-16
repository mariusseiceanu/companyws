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
import ro.momsw.companyws.service.OwnerRepository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OwnerRepositoryTest {

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
    public void shouldReturnRepositoryIndex() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.owners").exists());
    }

    @Test
    public void shouldCreateOwner() throws Exception {
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isCreated()).
                andExpect(header().string("Location", containsString("owners/")));
    }

    @Test
    public void shouldNotCreateOwnerWithoutEmail() throws Exception {
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateOwnerWithoutFirstName() throws Exception {
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateOwnerWithoutLastName() throws Exception {
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\", \"firstName\" : \"Marius\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldNotCreateOwnerWithExistingEmail() throws Exception {
        //first user is created
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isCreated()).
                andExpect(header().string("Location", containsString("owners/")));

        //second try with same email fails
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius2\", \"lastName\" : \"Seiceanu2\"}")).
                andExpect(status().isConflict());
    }

    @Test
    public void shouldRetrieveOwner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andExpect(jsonPath("$.email").value("marius.seiceanu@gmail.com")).
                andExpect(jsonPath("$.firstName").value("Marius")).
                andExpect(jsonPath("$.lastName").value("Seiceanu"));
    }

    @Test
    public void shouldQueryOwnerByEmail() throws Exception {
        mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).
                andExpect(status().isCreated());

        mockMvc.perform(
                get(ownersUrlTemplate + "/search/findByEmail?email={email}", "marius.seiceanu@gmail.com")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$._embedded.owners[0].email").value("marius.seiceanu@gmail.com")).
                andExpect(jsonPath("$._embedded.owners[0].firstName").value("Marius")).
                andExpect(jsonPath("$._embedded.owners[0].lastName").value("Seiceanu"));
    }

    @Test
    public void shouldCompletelyUpdateOwner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(put(location).content(
                "{\"email\" : \"marius.seiceanu@gmail.com_0\",  \"firstName\" : \"Marius_1\", \"lastName\" : \"Seiceanu_2\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andExpect(jsonPath("$.email").value("marius.seiceanu@gmail.com_0")).
                andExpect(jsonPath("$.firstName").value("Marius_1")).
                andExpect(jsonPath("$.lastName").value("Seiceanu_2"));
    }

    @Test
    public void shouldPartiallyUpdateOwner() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post(ownersUrlTemplate).content(
                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(
                patch(location).content("{\"firstName\": \"Marius_2\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).
                andExpect(jsonPath("$.email").value("marius.seiceanu@gmail.com")).
                andExpect(jsonPath("$.firstName").value("Marius_2")).
                andExpect(jsonPath("$.lastName").value("Seiceanu"));
    }

//    @Test
//    public void shouldDeleteOwner() throws Exception {
//
//        MvcResult mvcResult = mockMvc.perform(post(ownersUrlTemplate).content(
//                "{\"email\" : \"marius.seiceanu@gmail.com\",  \"firstName\" : \"Marius\", \"lastName\" : \"Seiceanu\"}")).andExpect(
//                status().isCreated()).andReturn();
//
//        String location = mvcResult.getResponse().getHeader("Location");
//        mockMvc.perform(delete(location)).andExpect(status().isNoContent());
//
//        mockMvc.perform(get(location)).andExpect(status().isNotFound());
//    }

}
