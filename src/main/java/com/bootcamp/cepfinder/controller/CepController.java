package com.bootcamp.cepfinder.controller;

import com.bootcamp.cepfinder.dto.CepSearchResponse;
import com.bootcamp.cepfinder.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@Tag(name = "CEP Finder", description = "Consulta endereços via ViaCEP e mantém histórico no banco de dados")
public class CepController {

    private final CepService cepService;

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Busca um CEP", description = "Consulta a API ViaCEP e salva o resultado no histórico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CEP encontrado"),
        @ApiResponse(responseCode = "400", description = "CEP inválido (não numérico ou diferente de 8 dígitos)"),
        @ApiResponse(responseCode = "404", description = "CEP não encontrado na base dos Correios")
    })
    public ResponseEntity<CepSearchResponse> buscarCep(
            @PathVariable
            @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve estar no formato 00000000 ou 00000-000")
            String cep) {
        return ResponseEntity.ok(cepService.buscarERegistrar(cep));
    }

    @GetMapping("/historico")
    @Operation(summary = "Lista o histórico", description = "Retorna até 100 buscas realizadas, da mais recente à mais antiga")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CepSearchResponse>> listarHistorico() {
        return ResponseEntity.ok(cepService.listarHistorico());
    }

    @DeleteMapping("/historico/{id}")
    @Operation(summary = "Remove do histórico", description = "Remove uma entrada específica do histórico pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID não encontrado")
    })
    public ResponseEntity<Void> removerHistorico(@PathVariable Long id) {
        cepService.removerHistorico(id);
        return ResponseEntity.noContent().build();
    }
}
