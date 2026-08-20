package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno de {@code GET /dps/{idDps}}.
 * Porte de {@code Nfse\Dto\Http\ConsultaDpsResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaDpsResponse {
    private Integer tipoAmbiente;
    private String versaoAplicativo;
    private String dataHoraProcessamento;
    private String idDps;
    private String chaveAcesso;
}
