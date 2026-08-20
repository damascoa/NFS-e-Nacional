package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\AtividadeEventoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtividadeEventoData {

    /** Nome do evento ou atividade. */
    private String nome;

    /** Data de início do evento. Formato: AAAA-MM-DD */
    private String dataInicio;

    /** Data de fim do evento. Formato: AAAA-MM-DD */
    private String dataFim;

    /** Identificador da atividade ou evento. */
    private String idAtividadeEvento;

    /** Endereço do evento. */
    private EnderecoData endereco;

}
