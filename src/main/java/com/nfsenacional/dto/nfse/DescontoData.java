package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\DescontoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescontoData {

    /** Valor do desconto incondicionado. */
    private Double valorDescontoIncondicionado;

    /** Valor do desconto condicionado. */
    private Double valorDescontoCondicionado;

}
