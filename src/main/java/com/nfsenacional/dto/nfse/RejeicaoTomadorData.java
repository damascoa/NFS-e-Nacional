package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\RejeicaoTomadorData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RejeicaoTomadorData {

    private String descricao;

    private String codigoMotivo;

    private String motivo;

}
