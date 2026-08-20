package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno de {@code POST /nfse/{chaveAcesso}/eventos} e de {@code GET .../eventos/{tipo}/{numSeq}}.
 * Porte de {@code Nfse\Dto\Http\RegistroEventoResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroEventoResponse {
    private Integer tipoAmbiente;
    private String versaoAplicativo;
    private String dataHoraProcessamento;
    private String eventoXmlGZipB64;
}
