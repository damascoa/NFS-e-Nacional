package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno da consulta de parâmetros de convênio de um município.
 * Porte de {@code Nfse\Dto\Http\ResultadoConsultaConfiguracoesConvenioResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoConsultaConfiguracoesConvenioResponse {
    private String mensagem;
    private ParametrosConfiguracaoConvenioDto parametrosConvenio;
}
