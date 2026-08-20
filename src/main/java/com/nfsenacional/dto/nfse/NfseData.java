package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\NfseData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NfseData {

    /** Versão do leiaute. */
    private String versao;

    /** Informações da NFS-e. */
    private InfNfseData infNfse;

    /** Informações do Evento. */
    private InfEventoData infEvento;

    /** XML original retornado pela API da SEFIN Nacional. */
    private String nfseXml;

}
