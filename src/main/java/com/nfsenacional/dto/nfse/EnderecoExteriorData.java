package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\EnderecoExteriorData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoExteriorData {

    /** Código do país (ISO2). */
    private String codigoPais;

    /** Código de endereçamento postal. */
    private String codigoEnderecamentoPostal;

    /** Nome da cidade. */
    private String cidade;

    /** Estado, província ou região. */
    private String estadoProvinciaRegiao;

}
