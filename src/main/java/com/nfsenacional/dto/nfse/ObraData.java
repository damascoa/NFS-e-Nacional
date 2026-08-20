package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\ObraData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObraData {

    /** Inscrição imobiliária fiscal da obra. */
    private String inscricaoImobiliariaFiscal;

    /** Código da obra. */
    private String codigoObra;

    /** Endereço da obra. */
    private EnderecoData endereco;

}
