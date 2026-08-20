package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\ValoresNfseData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValoresNfseData {

    /** Valor calculado de Dedução/Redução. */
    private Double valorCalculadoDeducaoReducao;

    /** Tipo de Benefício Municipal. */
    private Integer tipoBeneficioMunicipal;

    /** Valor calculado de Benefício Municipal. */
    private Double valorCalculadoBeneficioMunicipal;

    /** Valor da Base de Cálculo. */
    private Double baseCalculo;

    /** Alíquota Aplicada. */
    private Double aliquotaAplicada;

    /** Valor do ISSQN. */
    private Double valorIssqn;

    /** Valor Total Retido. */
    private Double valorTotalRetido;

    /** Valor Líquido da NFS-e. */
    private Double valorLiquido;

}
