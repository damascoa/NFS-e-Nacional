package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\ValorServicoPrestadoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorServicoPrestadoData {

    /** Valor recebido pelo intermediário. Obrigatório se tpEmit = 3. */
    private Double valorRecebido;

    /** Valor do serviço prestado. */
    private Double valorServico;

}
