package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno de {@code GET /nfse/{chaveAcesso}}.
 * Porte de {@code Nfse\Dto\Http\ConsultaNfseResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaNfseResponse {
    private Integer tipoAmbiente;
    private String versaoAplicativo;
    private String dataHoraProcessamento;
    private String chaveAcesso;
    private String nfseXmlGZipB64;
}
