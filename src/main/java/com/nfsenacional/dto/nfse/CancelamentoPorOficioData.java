package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\CancelamentoPorOficioData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelamentoPorOficioData {

    /** DTO para evento e305101 - Cancelamento de NFS-e Por Ofício Baseado em TE305101 do schema tiposEventos_v1.01.xsd Cancelamento iniciado por determinação ou ordem oficial da administração tributária class CancelamentoPorOficioData extends Dto { Descrição do Evento: "Cancelamento de NFS-e por Ofício" */
    private String descricao;

    /** CPF do agente da administração tributária municipal que efetuou o cancelamento por ofício de NFS-e */
    private String cpfAgenteTributario;

    /** Número do processo administrativo municipal vinculado ao cancelamento de NFS-e por ofício */
    private String numeroProcessoAdministrativo;

    /** Descrição para explicitar o motivo indicado neste evento */
    private String descricaoProcessoAdministrativo;

}
