package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\AnulacaoRejeicaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnulacaoRejeicaoData {

    private String descricao;

    private String cpfAgenteTributario;

    private String idEventoManifestacaoRejeicao;

    private String motivo;

}
