package ch.pinet.appmonitoring.otelcollectorapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSaveAndGetConfig() throws Exception {
        String testConfig = "{\"config\": \"enabled\"}";
        mockMvc.perform(post("/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testConfig))
                .andExpect(status().isOk());
        mockMvc.perform(get("/config"))
                .andExpect(status().isOk())
                .andExpect(content().json(testConfig));
    }
}
