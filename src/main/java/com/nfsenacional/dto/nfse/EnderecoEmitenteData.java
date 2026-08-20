package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\EnderecoEmitenteData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoEmitenteData {

    /** Logradouro. */
    private String logradouro;

    /** Número. */
    private String numero;

    /** Complemento. */
    private String complemento;

    /** Bairro. */
    private String bairro;

    /** Código do município (IBGE). */
    private String codigoMunicipio;

    /** Sigla da UF. */
    private String uf;

    /** CEP. */
    private String cep;

}
