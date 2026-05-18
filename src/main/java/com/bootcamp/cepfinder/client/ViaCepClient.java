package com.bootcamp.cepfinder.client;

import com.bootcamp.cepfinder.dto.CepApiResponse;
import com.bootcamp.cepfinder.exception.CepNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ViaCepClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ViaCepClient(RestTemplate restTemplate,
                        @Value("${viacep.base-url:https://viacep.com.br}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public CepApiResponse buscarCep(String cep) {
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve conter exatamente 8 dígitos. Recebido: " + cep);
        }

        String url = baseUrl + "/ws/" + cepLimpo + "/json/";
        log.info("Consultando ViaCEP: {}", url);

        try {
            CepApiResponse response = restTemplate.getForObject(url, CepApiResponse.class);
            if (response == null || response.isErro()) {
                throw new CepNotFoundException(cep);
            }
            return response;
        } catch (HttpClientErrorException ex) {
            log.error("Erro ao consultar ViaCEP para CEP {}: {}", cep, ex.getMessage());
            throw new CepNotFoundException(cep);
        }
    }
}
