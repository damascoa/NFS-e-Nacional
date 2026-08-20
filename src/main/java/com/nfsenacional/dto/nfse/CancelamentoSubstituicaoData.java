package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\CancelamentoSubstituicaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelamentoSubstituicaoData {

    /** DTO para evento e105102 - Cancelamento de NFS-e por Substituição Baseado em TE105102 do schema tiposEventos_v1.01.xsd class CancelamentoSubstituicaoData extends Dto { Descrição do Evento: "Cancelamento de NFS-e por Substituicao" */
    private String descricao;

    /** Código de justificativa de cancelamento substituição */
    private String codigoMotivo;

    /** Descrição para explicitar o motivo indicado neste evento (opcional) */
    private String descricaoMotivo;

    /** Chave de Acesso da NFS-e substituta */
    private String chaveNfseSubstituta;

}
