package com.bootcamp.cepfinder;

import com.bootcamp.cepfinder.client.IbgeClient;
import com.bootcamp.cepfinder.dto.CidadeInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração — API IBGE (2ª API Pública)")
class CidadeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IbgeClient ibgeClient;

    @Test
    @DisplayName("GET /api/cidade/{ibge} — deve retornar 200 com dados do município")
    void deveRetornarDadosDoCidade() throws Exception {
        var cidade = new CidadeInfoResponse(
                3550308, "São Paulo",
                "SP", "São Paulo",
                "Sudeste", "SE",
                "Metropolitana de São Paulo",
                "São Paulo"
        );
        when(ibgeClient.buscarMunicipio("3550308")).thenReturn(Optional.of(cidade));

        mockMvc.perform(get("/api/cidade/3550308").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.regiao").value("Sudeste"))
                .andExpect(jsonPath("$.mesorregiao").value("Metropolitana de São Paulo"));
    }

    @Test
    @DisplayName("GET /api/cidade/{ibge} — deve retornar 404 quando IBGE não encontra o município")
    void deveRetornar404QuandoMunicipioNaoEncontrado() throws Exception {
        when(ibgeClient.buscarMunicipio("9999999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cidade/9999999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/cidade/{ibge} — deve retornar 404 quando IBGE está indisponível")
    void deveRetornar404QuandoIbgeIndisponivel() throws Exception {
        when(ibgeClient.buscarMunicipio("3304557")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cidade/3304557").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
