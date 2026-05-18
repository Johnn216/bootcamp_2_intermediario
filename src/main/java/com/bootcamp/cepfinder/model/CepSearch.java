package com.bootcamp.cepfinder.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "cep_searches",
    indexes = @Index(name = "idx_cep_searches_buscado_em", columnList = "buscado_em DESC")
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CepSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(length = 200)
    private String logradouro;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String localidade;

    @Column(length = 2)
    private String uf;

    @Column(length = 10)
    private String ibge;

    @Column(length = 3)
    private String ddd;

    @Column(name = "buscado_em", nullable = false)
    private LocalDateTime buscadoEm;

    @PrePersist
    void prePersist() {
        this.buscadoEm = LocalDateTime.now();
    }
}
