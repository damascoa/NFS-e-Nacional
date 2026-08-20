package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\InfEventoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfEventoData {

    private String id;

    private String versaoAplicativo;

    private Integer ambiente;

    private Integer numeroSequencialEvento;

    private String dataHoraProcessamento;

    private String numeroDfe;

    private PedRegEventoData pedRegEvento;

}
