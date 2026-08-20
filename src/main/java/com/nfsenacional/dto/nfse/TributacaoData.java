package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.CstPisCofins;
import com.nfsenacional.enums.IndicadorTotalTributos;
import com.nfsenacional.enums.TipoImunidade;
import com.nfsenacional.enums.TipoRetencaoIssqn;
import com.nfsenacional.enums.TipoRetencaoPisCofins;
import com.nfsenacional.enums.TipoSuspensao;
import com.nfsenacional.enums.TributacaoIssqn;

/**
 * Porte de {@code Nfse\Dto\Nfse\TributacaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TributacaoData {

    /** Tributação do ISSQN. 1 - Operação tributável 2 - Imunidade 3 - Exportação de serviço 4 - Não Incidência */
    private TributacaoIssqn tributacaoIssqn;

    /** Tipo de imunidade. Obrigatório se tribISSQN = 2. 0 - Imunidade (tipo não informado na nota de origem) 1 - Patrimônio, renda ou serviços, uns dos outros (CF88, Art 150, VI, a) 2 - Templos de qualquer culto (CF88, Art 150, VI, b) 3 - Patrimônio, renda ou serviços dos partidos políticos, inclusive suas fundações, das entidades sindicais dos trabalhadores, das instituições de educação e de assistência social, sem fins lucrativos, atendidos os requisitos da lei (CF88, Art 150, VI, c) 4 - Livros, jornais, periódicos e o papel destinado a sua impressão (CF88, Art 150, VI, d) */
    private TipoImunidade tipoImunidade;

    /** Tipo de retencao do ISSQN. 1 - Não Retido 2 - Retido pelo Tomador 3 - Retido pelo Intermediario */
    private TipoRetencaoIssqn tipoRetencaoIssqn;

    /** Alíquota do ISSQN. */
    private Double aliquota;

    /** Suspensão da exigibilidade do ISSQN. 1 - Suspenso por decisão judicial 2 - Suspenso por decisão administrativa */
    private TipoSuspensao tipoSuspensao;

    /** Número do processo judicial ou administrativo de suspensão da exigibilidade. */
    private String numeroProcessoSuspensao;

    /** Benefício Municipal. */
    private BeneficioMunicipalData beneficioMunicipal;

    /** Código da Situação Tributária do PIS/COFINS. */
    private CstPisCofins cstPisCofins;

    /** Base de cálculo PIS/COFINS. */
    private Double baseCalculoPisCofins;

    /** Alíquota PIS. */
    private Double aliquotaPis;

    /** Alíquota COFINS. */
    private Double aliquotaCofins;

    /** Valor PIS (R$). NT 007/2026: este campo registra o valor de PIS como débito de apuração própria do prestador. Não deve ser usado para informar valores retidos. Para retenção, consolidar no campo vRetCSLL. */
    private Double valorPis;

    /** Valor COFINS (R$). NT 007/2026: este campo registra o valor de COFINS como débito de apuração própria do prestador. Não deve ser usado para informar valores retidos. Para retenção, consolidar no campo vRetCSLL. */
    private Double valorCofins;

    /** Tipo de Retenção PIS/COFINS e CSLL. Códigos 0 e 3-9 definidos pela NT 007/2026. Atualmente o schema aceita apenas os códigos 1 e 2. Os demais serão habilitados quando os grupos IBSCBS se tornarem obrigatórios. */
    private TipoRetencaoPisCofins tipoRetencaoPisCofins;

    /** Valor retido de IRRF (R$). */
    private Double valorRetidoIrrf;

    /** Valor retido de contribuições sociais (R$). NT 007/2026: se houver retenções de PIS, COFINS e/ou CSLL, elas devem ser SOMADAS e informadas neste campo, de acordo com o tipo de retenção indicado em tpRetPisCofins. Exemplo: para tpRetPisCofins=1 (PIS/COFINS Retido) com CSLL também retida, este campo deve conter: PIS retido + COFINS retido + CSLL retida. */
    private Double valorRetidoCsll;

    /** Valor total dos tributos federais. */
    private Double valorTotalTributosFederais;

    /** Valor total dos tributos estaduais. */
    private Double valorTotalTributosEstaduais;

    /** Valor total dos tributos municipais. */
    private Double valorTotalTributosMunicipais;

    /** Valor percentual total aproximado dos tributos federais, estaduais e municipais. */
    private Double percentualTotalTributosSN;

    /** Percentual total aproximado dos tributos federais. */
    private Double percentualTotalTributosFederais;

    /** Percentual total aproximado dos tributos estaduais. */
    private Double percentualTotalTributosEstaduais;

    /** Percentual total aproximado dos tributos municipais. */
    private Double percentualTotalTributosMunicipais;

    /** Indicador de informação de valor total de tributos. 0 - Nenhum */
    private IndicadorTotalTributos indicadorTotalTributos;

}
