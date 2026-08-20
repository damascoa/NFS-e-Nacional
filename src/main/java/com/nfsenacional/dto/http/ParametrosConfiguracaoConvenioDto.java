package com.nfsenacional.dto.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Parâmetros de adesão/convênio de um município ao Sistema Nacional NFS-e.
 * Porte de {@code Nfse\Dto\Http\ParametrosConfiguracaoConvenioDto} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametrosConfiguracaoConvenioDto {
    private Integer aderenteAmbienteNacional;
    private Integer aderenteEmissorNacional;
    private Integer situacaoEmissaoPadraoContribuintesRFB;
    private Integer aderenteMAN;
    private Boolean permiteAproveitametoDeCreditos;
}
