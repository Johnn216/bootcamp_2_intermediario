package com.bootcamp.cepfinder;

import com.bootcamp.cepfinder.client.IbgeClient;
import com.bootcamp.cepfinder.client.ViaCepClient;
import com.bootcamp.cepfinder.dto.CepApiResponse;
import com.bootcamp.cepfinder.model.CepSearch;
import com.bootcamp.cepfinder.repository.CepSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração — CEP Finder")
class CepIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CepSearchRepository repository;

    @MockBean
    private ViaCepClient viaCepClient;

    @MockBean
    @SuppressWarnings("unused")
    private IbgeClient ibgeClient;

    @BeforeEach
    void limparBase() {
        repository.deleteAll();
    }

    // ============================================================
    // Busca de CEP
    // ============================================================

    @Test
    @DisplayName("GET /api/cep/{cep} — deve retornar 200 e salvar no banco")
    void deveBuscarCepESalvarNoHistorico() throws Exception {
        when(viaCepClient.buscarCep("01310100")).thenReturn(mockAvenidaPaulista());

        mockMvc.perform(get("/api/cep/01310100").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01310-100"))
                .andExpect(jsonPath("$.logradouro").value("Avenida Paulista"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.buscadoEm").isNotEmpty());

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("GET /api/cep/{cep} — deve retornar 404 para CEP inexistente")
    void deveRetornar404ParaCepInexistente() throws Exception {
        when(viaCepClient.buscarCep("00000000"))
                .thenThrow(new com.bootcamp.cepfinder.exception.CepNotFoundException("00000000"));

        mockMvc.perform(get("/api/cep/00000000").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        assertThat(repository.count()).isZero();
    }

    // ============================================================
    // Histórico — sem Thread.sleep: insere direto no repositório
    // com timestamps controlados para garantir ordem determinística
    // ============================================================

    @Test
    @DisplayName("GET /api/historico — deve retornar lista ordenada por data decrescente")
    void deveListarHistoricoOrdenadoPorDataDecrescente() throws Exception {
        // Inserção direta no repositório com timestamps explícitos para evitar flakiness
        repository.save(CepSearch.builder()
                .cep("01310-100").logradouro("Avenida Paulista").bairro("Bela Vista")
                .localidade("São Paulo").uf("SP").buscadoEm(LocalDateTime.now().minusSeconds(5))
                .build());
        repository.save(CepSearch.builder()
                .cep("22070-011").logradouro("Avenida Atlântica").bairro("Copacabana")
                .localidade("Rio de Janeiro").uf("RJ").buscadoEm(LocalDateTime.now())
                .build());

        mockMvc.perform(get("/api/historico").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].localidade").value("Rio de Janeiro"))
                .andExpect(jsonPath("$[1].localidade").value("São Paulo"));
    }

    @Test
    @DisplayName("DELETE /api/historico/{id} — deve remover registro e retornar 204")
    void deveRemoverRegistroDoHistorico() throws Exception {
        when(viaCepClient.buscarCep("01310100")).thenReturn(mockAvenidaPaulista());

        String body = mockMvc.perform(get("/api/cep/01310100").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        long id = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/historico/" + id))
                .andExpect(status().isNoContent());

        assertThat(repository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("DELETE /api/historico/{id} — deve retornar 400 para ID inexistente")
    void deveRetornar400AoRemoverIdInexistente() throws Exception {
        mockMvc.perform(delete("/api/historico/999999"))
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // Fixtures
    // ============================================================

    private CepApiResponse mockAvenidaPaulista() {
        return new CepApiResponse(
                "01310-100", "Avenida Paulista", "de 1 a 610 - lado par",
                "Bela Vista", "São Paulo", "SP", "3550308", "11", null
        );
    }
}
