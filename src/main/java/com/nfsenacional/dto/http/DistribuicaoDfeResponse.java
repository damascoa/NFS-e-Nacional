package com.nfsenacional.dto.http;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno da distribuição de documentos fiscais (ADN) por NSU.
 * Porte de {@code Nfse\Dto\Http\DistribuicaoDfeResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribuicaoDfeResponse {
    private String tipoAmbiente;
    private String versaoAplicativo;
    private String dataHoraProcessamento;
    private Integer ultimoNsu;
    private Integer maiorNsu;
    private List<MensagemProcessamentoDto> alertas;
    private List<MensagemProcessamentoDto> erros;
    private List<DistribuicaoNsuDto> listaNsu;
}
