package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\InfPedRegData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfPedRegData {

    private String id;

    private Integer tipoAmbiente;

    private String versaoAplicativo;

    private String dataHoraEvento;

    private String chaveNfse;

    private String cnpjAutor;

    private String cpfAutor;

    private Integer nPedRegEvento;

    private String tipoEvento;

    /** === CANCELAMENTOS === */
    private CancelamentoData e101101;

    private CancelamentoSubstituicaoData e105102;

    private AnaliseFiscalSolicitacaoData e101103;

    private AnaliseFiscalData e105104;

    private AnaliseFiscalData e105105;

    private CancelamentoPorOficioData e305101;

    /** === CONFIRMAÇÕES === */
    private ConfirmacaoPrestadorData e202201;

    private ConfirmacaoTomadorData e203202;

    private ConfirmacaoIntermediarioData e204203;

    private ConfirmacaoTacitaData e205204;

    /** === REJEIÇÕES === */
    private RejeicaoPrestadorData e202205;

    private RejeicaoTomadorData e203206;

    private RejeicaoIntermediarioData e204207;

    private AnulacaoRejeicaoData e205208;

    /** === AÇÕES POR OFÍCIO === */
    private BloqueioPorOficioData e305102;

    private DesbloqueioPorOficioData e305103;

    /** === RESERVADOS PELO SCHEMA === */
    private Object e907202;

    private Object e967203;

}
