package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\ValoresData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValoresData {

    /** Valor do serviço prestado. */
    private ValorServicoPrestadoData valorServicoPrestado;

    /** Descontos condicionados e incondicionados. */
    private DescontoData desconto;

    /** Deduções e reduções da base de cálculo. */
    private DeducaoReducaoData deducaoReducao;

    /** Informações sobre a tributação do serviço. */
    private TributacaoData tributacao;

}
