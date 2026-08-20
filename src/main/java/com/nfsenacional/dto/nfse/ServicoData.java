package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\ServicoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoData {

    /** Local da prestação do serviço. */
    private LocalPrestacaoData localPrestacao;

    /** Código do serviço prestado. */
    private CodigoServicoData codigoServico;

    /** Informações de comércio exterior. */
    private ComercioExteriorData comercioExterior;

    /** Informações da obra. */
    private ObraData obra;

    /** Informações de atividade/evento. */
    private AtividadeEventoData atividadeEvento;

    /** Informações complementares do serviço. */
    private InfoComplData informacaoComplemento;

}
