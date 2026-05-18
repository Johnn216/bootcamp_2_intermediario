package com.bootcamp.cepfinder.controller;

import com.bootcamp.cepfinder.client.IbgeClient;
import com.bootcamp.cepfinder.dto.CidadeInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Cidade", description = "Dados municipais via IBGE (API pública oficial)")
public class CidadeController {

    private final IbgeClient ibgeClient;

    @GetMapping("/cidade/{ibge}")
    @Operation(
        summary = "Dados do município pelo código IBGE",
        description = "Consulta a API oficial do IBGE e retorna dados geográficos e administrativos do município"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados encontrados"),
        @ApiResponse(responseCode = "404", description = "Município não encontrado no IBGE")
    })
    public ResponseEntity<CidadeInfoResponse> buscarCidade(@PathVariable String ibge) {
        return ibgeClient.buscarMunicipio(ibge)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
