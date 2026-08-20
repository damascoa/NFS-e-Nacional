package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.AmbienteGerador;
import com.nfsenacional.enums.CodigoStatus;
import com.nfsenacional.enums.ProcessoEmissao;

/**
 * Porte de {@code Nfse\Dto\Nfse\InfNfseData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfNfseData {

    /** Identificador da NFS-e. */
    private String id;

    /** Número da NFS-e. */
    private String numeroNfse;

    /** Número do DFe. */
    private String numeroDfse;

    /** Código de verificação. */
    private String codigoVerificacao;

    /** Data e hora de processamento. */
    private String dataProcessamento;

    /** Ambiente gerador. */
    private AmbienteGerador ambienteGerador;

    /** Versão do aplicativo. */
    private String versaoAplicativo;

    /** Processo de emissão. */
    private ProcessoEmissao processoEmissao;

    /** Local de emissão (Nome). */
    private String localEmissao;

    /** Local de prestação (Nome). */
    private String localPrestacao;

    /** Código do local de incidência. */
    private String codigoLocalIncidencia;

    /** Local de incidência (Nome). */
    private String nomeLocalIncidencia;

    /** Descrição da tributação nacional. */
    private String descricaoTributacaoNacional;

    /** Descrição da tributação municipal. */
    private String descricaoTributacaoMunicipal;

    /** Descrição da NBS. */
    private String descricaoNbs;

    /** Tipo de Emissão. */
    private Integer tipoEmissao;

    /** Código de status. */
    private CodigoStatus codigoStatus;

    /** Outras Informações. */
    private String outrasInformacoes;

    /** Dados da DPS. */
    private DpsData dps;

    /** Dados do emitente. */
    private EmitenteData emitente;

    /** Valores da NFS-e. */
    private ValoresNfseData valores;

    /** Grupo de informações geradas pelo sistema referentes ao IBS e à CBS. */
    private IbscbsNfseData ibscbs;

}
