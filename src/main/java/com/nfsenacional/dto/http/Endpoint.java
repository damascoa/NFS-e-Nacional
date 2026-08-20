package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Par de URLs base (produção/homologação) — permite customizar o endpoint da Sefin,
 * por exemplo para municípios com infraestrutura própria (ver {@code SefinEndpointResolver}).
 * Porte de {@code Nfse\Dto\Http\Endpoint} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Endpoint {
    private String production;
    private String homologation;
}
