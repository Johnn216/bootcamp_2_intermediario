package com.bootcamp.cepfinder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IbgeMunicipioRaw(
        Integer id,
        String nome,
        @JsonProperty("microrregiao") Microrregiao microrregiao
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Microrregiao(
            String nome,
            @JsonProperty("mesorregiao") Mesorregiao mesorregiao
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Mesorregiao(
            String nome,
            @JsonProperty("UF") Uf uf
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Uf(
            String sigla,
            String nome,
            @JsonProperty("regiao") Regiao regiao
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Regiao(
            String sigla,
            String nome
    ) {}
}
