package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\IbscbsNfseData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IbscbsNfseData {

    /** Código IBGE da localidade de incidência do IBS/CBS (local da operação). */
    private String codigoLocalidadeIncidencia;

    /** Nome da localidade de incidência do IBS/CBS. */
    private String nomeLocalidadeIncidencia;

    /** Percentual de redução de alíquota em compra governamental. */
    private Double percentualRedutor;

    /** Valor da base de cálculo do IBS/CBS antes das reduções. */
    private Double baseCalculo;

    /** Valor total relativo a reembolso, repasse ou ressarcimento que não integra a base de cálculo do ISSQN, do IBS e da CBS. */
    private Double valorCalculadoReembolso;

    /** Alíquota da UF para IBS da localidade de incidência. */
    private Double aliquotaIbsUf;

    /** Percentual de redução de alíquota estadual. */
    private Double percentualReducaoAliquotaUf;

    /** Alíquota efetiva da UF para IBS. */
    private Double aliquotaEfetivaUf;

    /** Alíquota do Município para IBS da localidade de incidência. */
    private Double aliquotaIbsMunicipal;

    /** Percentual de redução de alíquota municipal. */
    private Double percentualReducaoAliquotaMunicipal;

    /** Alíquota efetiva do Município para IBS. */
    private Double aliquotaEfetivaMunicipal;

    /** Alíquota da União para CBS. */
    private Double aliquotaCbs;

    /** Percentual de redução de alíquota da CBS. */
    private Double percentualReducaoAliquotaCbs;

    /** Alíquota efetiva da União para CBS. */
    private Double aliquotaEfetivaCbs;

    /** Valor total da NFS-e considerando os impostos por fora (IBS e CBS). */
    private Double valorTotalNota;

    /** Valor total do IBS. */
    private Double valorTotalIbs;

    /** Alíquota do crédito presumido para o IBS. */
    private Double aliquotaCreditoPresumidoIbs;

    /** Valor do crédito presumido para o IBS. */
    private Double valorCreditoPresumidoIbs;

    /** Total do diferimento do IBS estadual. */
    private Double valorDiferimentoUf;

    /** Total do valor do IBS estadual. */
    private Double valorIbsUf;

    /** Total do diferimento do IBS municipal. */
    private Double valorDiferimentoMunicipal;

    /** Total do valor do IBS municipal. */
    private Double valorIbsMunicipal;

    /** Alíquota do crédito presumido para a CBS. */
    private Double aliquotaCreditoPresumidoCbs;

    /** Valor do crédito presumido da CBS. */
    private Double valorCreditoPresumidoCbs;

    /** Total do diferimento da CBS. */
    private Double valorDiferimentoCbs;

    /** Total do valor da CBS da União. */
    private Double valorCbs;

    /** Alíquota efetiva de tributação regular do IBS estadual. */
    private Double aliquotaEfetivaRegularIbsUf;

    /** Valor da tributação regular do IBS estadual. */
    private Double valorTributacaoRegularIbsUf;

    /** Alíquota efetiva de tributação regular do IBS municipal. */
    private Double aliquotaEfetivaRegularIbsMunicipal;

    /** Valor da tributação regular do IBS municipal. */
    private Double valorTributacaoRegularIbsMunicipal;

    /** Alíquota efetiva de tributação regular da CBS. */
    private Double aliquotaEfetivaRegularCbs;

    /** Valor da tributação regular da CBS. */
    private Double valorTributacaoRegularCbs;

    /** Alíquota do IBS de competência do Estado em compras governamentais. */
    private Double aliquotaCompraGovIbsUf;

    /** Valor do tributo do IBS da UF calculado em compras governamentais. */
    private Double valorCompraGovIbsUf;

    /** Alíquota do IBS de competência do Município em compras governamentais. */
    private Double aliquotaCompraGovIbsMunicipal;

    /** Valor do tributo do IBS do Município calculado em compras governamentais. */
    private Double valorCompraGovIbsMunicipal;

    /** Alíquota da CBS em compras governamentais. */
    private Double aliquotaCompraGovCbs;

    /** Valor do tributo da CBS calculado em compras governamentais. */
    private Double valorCompraGovCbs;

}
