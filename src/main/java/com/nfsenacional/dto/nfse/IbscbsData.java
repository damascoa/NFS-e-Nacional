package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import com.nfsenacional.enums.IndicadorDestinatario;
import com.nfsenacional.enums.TipoEnteGovernamental;
import com.nfsenacional.enums.TipoOperacaoRtc;

/**
 * Porte de {@code Nfse\Dto\Nfse\IbscbsData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IbscbsData {

    /** Indicador da finalidade da emissão de NFS-e. 0 - NFS-e regular */
    private String finalidadeNfse;

    /** Indica operação de uso ou consumo pessoal (art. 57). 0 - Não 1 - Sim */
    private Integer indicadorUsoConsumoPessoal;

    /** Código indicador da operação de fornecimento, conforme tabela "código indicador de operação" (Anexo VII). */
    private String codigoIndicadorOperacao;

    /** Tipo de Operação com Entes Governamentais ou outros serviços sobre bens imóveis. */
    private TipoOperacaoRtc tipoOperacao;

    /** Chaves de acesso das NFS-e referenciadas. */
    private List<String> chavesNfseReferenciadas;

    /** Tipo de ente governamental. Informado apenas no caso de compras governamentais. */
    private TipoEnteGovernamental tipoEnteGovernamental;

    /** A respeito do destinatário dos serviços. */
    private IndicadorDestinatario indicadorDestinatario;

    /** Dados do destinatário do serviço. Só deve ser identificado quando indDest = 1. */
    private TomadorData destinatario;

    /** Informações de operações relacionadas a bens imóveis, exceto obras. */
    private ImovelData imovel;

    /** Documentos referenciados de reembolso, repasse ou ressarcimento. */
    private List<ReembolsoDocumentoData> documentosReembolso;

    /** Código de Situação Tributária do IBS e da CBS. */
    private String cst;

    /** Código de Classificação Tributária do IBS e da CBS. */
    private String codigoClassificacaoTributaria;

    /** Código e classificação do crédito presumido do IBS e da CBS. */
    private String codigoCreditoPresumido;

    /** Código de Situação Tributária do IBS e da CBS de tributação regular. */
    private String cstTributacaoRegular;

    /** Código da Classificação Tributária do IBS e da CBS de tributação regular. */
    private String codigoClassificacaoTributariaRegular;

    /** Percentual de diferimento para o IBS estadual. */
    private Double percentualDiferimentoUf;

    /** Percentual de diferimento para o IBS municipal. */
    private Double percentualDiferimentoMunicipal;

    /** Percentual de diferimento para a CBS. */
    private Double percentualDiferimentoCbs;

}
