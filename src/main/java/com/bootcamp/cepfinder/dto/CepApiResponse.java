package com.bootcamp.cepfinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CepApiResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge,
        String ddd,
        @JsonProperty("erro") Boolean erro
) {
    public boolean isErro() {
        return Boolean.TRUE.equals(erro);
    }
}
