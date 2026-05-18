package com.bootcamp.cepfinder.client;

import com.bootcamp.cepfinder.dto.CidadeInfoResponse;
import com.bootcamp.cepfinder.dto.IbgeMunicipioRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IbgeClient {

    private static final String BASE = "https://servicodados.ibge.gov.br/api/v1";

    private final RestTemplate restTemplate;

    public Optional<CidadeInfoResponse> buscarMunicipio(String ibgeCodigo) {
        String url = BASE + "/localidades/municipios/" + ibgeCodigo;
        log.info("Consultando IBGE: {}", url);
        try {
            IbgeMunicipioRaw raw = restTemplate.getForObject(url, IbgeMunicipioRaw.class);
            if (raw == null) return Optional.empty();
            return Optional.of(CidadeInfoResponse.from(raw));
        } catch (RestClientException ex) {
            log.warn("IBGE indisponível para código {}: {}", ibgeCodigo, ex.getMessage());
            return Optional.empty();
        }
    }
}
