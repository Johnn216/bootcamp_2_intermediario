package com.bootcamp.cepfinder.dto;

import com.bootcamp.cepfinder.model.CepSearch;
import java.time.LocalDateTime;

public record CepSearchResponse(
        Long id,
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge,
        String ddd,
        LocalDateTime buscadoEm
) {
    public static CepSearchResponse from(CepSearch entity) {
        return new CepSearchResponse(
                entity.getId(),
                entity.getCep(),
                entity.getLogradouro(),
                entity.getComplemento(),
                entity.getBairro(),
                entity.getLocalidade(),
                entity.getUf(),
                entity.getIbge(),
                entity.getDdd(),
                entity.getBuscadoEm()
        );
    }
}
