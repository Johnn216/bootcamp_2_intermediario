package com.bootcamp.cepfinder.dto;

public record CidadeInfoResponse(
        Integer id,
        String nome,
        String uf,
        String nomeUf,
        String regiao,
        String siglaRegiao,
        String mesorregiao,
        String microrregiao
) {
    public static CidadeInfoResponse from(IbgeMunicipioRaw raw) {
        var micro = raw.microrregiao();
        var meso  = micro != null ? micro.mesorregiao() : null;
        var uf    = meso  != null ? meso.uf()           : null;
        var reg   = uf    != null ? uf.regiao()          : null;

        return new CidadeInfoResponse(
                raw.id(),
                raw.nome(),
                uf   != null ? uf.sigla() : null,
                uf   != null ? uf.nome()  : null,
                reg  != null ? reg.nome()  : null,
                reg  != null ? reg.sigla() : null,
                meso != null ? meso.nome() : null,
                micro != null ? micro.nome() : null
        );
    }
}
