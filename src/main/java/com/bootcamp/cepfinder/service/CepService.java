package com.bootcamp.cepfinder.service;

import com.bootcamp.cepfinder.client.ViaCepClient;
import com.bootcamp.cepfinder.dto.CepApiResponse;
import com.bootcamp.cepfinder.dto.CepSearchResponse;
import com.bootcamp.cepfinder.model.CepSearch;
import com.bootcamp.cepfinder.repository.CepSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CepService {

    private static final int MAX_HISTORICO = 100;

    private final ViaCepClient viaCepClient;
    private final CepSearchRepository repository;

    @Transactional
    public CepSearchResponse buscarERegistrar(String cep) {
        CepApiResponse apiResponse = viaCepClient.buscarCep(cep);

        CepSearch entity = CepSearch.builder()
                .cep(apiResponse.cep())
                .logradouro(apiResponse.logradouro())
                .complemento(apiResponse.complemento())
                .bairro(apiResponse.bairro())
                .localidade(apiResponse.localidade())
                .uf(apiResponse.uf())
                .ibge(apiResponse.ibge())
                .ddd(apiResponse.ddd())
                .build();

        CepSearch saved = repository.save(entity);
        log.info("CEP {} salvo no histórico com id={}", cep, saved.getId());

        return CepSearchResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CepSearchResponse> listarHistorico() {
        return repository.findAllByOrderByBuscadoEmDesc(PageRequest.of(0, MAX_HISTORICO))
                .stream()
                .map(CepSearchResponse::from)
                .toList();
    }

    @Transactional
    public void removerHistorico(Long id) {
        // Single atomic delete — avoids existsById + deleteById race condition
        int deleted = repository.deleteByIdReturningCount(id);
        if (deleted == 0) {
            throw new IllegalArgumentException("Registro não encontrado: " + id);
        }
        log.info("Registro id={} removido do histórico", id);
    }
}
