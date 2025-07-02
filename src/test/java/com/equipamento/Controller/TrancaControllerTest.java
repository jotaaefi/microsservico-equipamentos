package com.equipamento.Controller;

import com.equipamento.Service.TrancaService;
import com.equipamento.mapper.TrancaMapper;
import com.equipamento.trabalhoES2.TrabalhoEs2Application;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource; // IMPORT NOVO E IMPORTANTE
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TrabalhoEs2Application.class)
// O 'exclude' FOI REMOVIDO DAQUI E SUBSTITUÍDO PELA LINHA ABAIXO
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
@AutoConfigureMockMvc
public class TrancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrancaService trancaService;

    @MockBean
    private TrancaMapper trancaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testeMinimo_deveCarregarContextoERodar() throws Exception {
        when(trancaService.listarTrancas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tranca")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}