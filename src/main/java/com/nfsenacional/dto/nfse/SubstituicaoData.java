package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.MotivoSubstituicao;

/**
 * Porte de {@code Nfse\Dto\Nfse\SubstituicaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubstituicaoData {

    /** Chave de acesso da NFS-e a ser substituída. */
    private String chaveNfseSubstituida;

    /** Código do motivo da substituição. 01 - Desenquadramento de NFS-e do Simples Nacional 02 - Enquadramento de NFS-e no Simples Nacional 99 - Outros */
    private MotivoSubstituicao codigoMotivo;

    /** Descrição do motivo da substituição. Obrigatório se cMotivo = 99. */
    private String descricaoMotivo;

}
