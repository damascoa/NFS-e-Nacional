package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\BeneficioMunicipalData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioMunicipalData {

    /** Código de identificação do Benefício Municipal. */
    private String codigoBeneficio;

    /** Percentual de redução da base de cálculo referente ao benefício municipal. */
    private Double percentualReducaoBcBm;

    /** Valor monetário de redução da base de cálculo referente ao benefício municipal. */
    private Double valorReducaoBcBm;

}
