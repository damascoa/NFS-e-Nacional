package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Alíquota vigente (ou histórica) de um serviço num município, num período.
 * Porte de {@code Nfse\Dto\Http\AliquotaDto} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AliquotaDto {
    private String incidencia;
    private Double aliquota;
    private String dataInicio;
    private String dataFim;
}
