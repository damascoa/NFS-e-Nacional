package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Um item retornado pela distribuição de documentos fiscais (ADN) via NSU.
 * Porte de {@code Nfse\Dto\Http\DistribuicaoNsuDto} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribuicaoNsuDto {
    private Integer nsu;
    private String chaveAcesso;
    private String dfeXmlGZipB64;
}
