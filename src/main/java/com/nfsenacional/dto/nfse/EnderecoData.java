package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\EnderecoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoData {

    /** Código do município (IBGE). */
    private String codigoMunicipio;

    /** CEP. */
    private String cep;

    /** Logradouro. */
    private String logradouro;

    /** Número. */
    private String numero;

    /** Bairro. */
    private String bairro;

    /** Complemento. */
    private String complemento;

    /** Endereço no exterior. */
    private EnderecoExteriorData enderecoExterior;

}
