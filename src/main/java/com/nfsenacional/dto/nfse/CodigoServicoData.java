package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\CodigoServicoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoServicoData {

    /** Código de tributação nacional (LC 116/03). */
    private String codigoTributacaoNacional;

    /** Código de tributação municipal. */
    private String codigoTributacaoMunicipal;

    /** Descrição do serviço. */
    private String descricaoServico;

    /** Código NBS (Nomenclatura Brasileira de Serviços). */
    private String codigoNbs;

    /** Código CNAE (Classificação Nacional de Atividades Econômicas). */
    private String codigoCnae;

    /** Código interno do serviço no sistema do contribuinte. */
    private String codigoInternoContribuinte;

}
