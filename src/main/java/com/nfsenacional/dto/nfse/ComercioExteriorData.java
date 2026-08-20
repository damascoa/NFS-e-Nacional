package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.ModoPrestacao;
import com.nfsenacional.enums.MovimentacaoTemporariaBens;
import com.nfsenacional.enums.TipoPessoa;

/**
 * Porte de {@code Nfse\Dto\Nfse\ComercioExteriorData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComercioExteriorData {

    /** Modo de prestação do serviço. 1 - Transfronteiriço 2 - Consumo no Brasil 3 - Presença Comercial no Exterior 4 - Movimento Temporário de Pessoas Físicas */
    private ModoPrestacao modoPrestacao;

    /** Vínculo entre as partes no negócio. 1 - Sem vínculo 2 - Com vínculo */
    private Integer vinculoPrestacao;

    /** Tipo de pessoa do exportador. 1 - Pessoa Jurídica 2 - Pessoa Física */
    private TipoPessoa tipoPessoaExportador;

    /** NIF do exportador. */
    private String nifExportador;

    /** Código do país do exportador. */
    private String codigoPaisExportador;

    /** Código do mecanismo de apoio/fomento. */
    private String codigoMecanismoApoioFomento;

    /** Número do enquadramento. */
    private String numeroEnquadramento;

    /** Número do processo. */
    private String numeroProcesso;

    /** Indicador de incentivo fiscal. 1 - Sim 2 - Não */
    private Integer indicadorIncentivo;

    /** Descrição do incentivo fiscal. */
    private String descricaoIncentivo;

    /** Código da moeda da transação (ISO 4217). */
    private String tipoMoeda;

    /** Valor do serviço na moeda estrangeira. */
    private Double valorServicoMoeda;

    /** Mecanismo de apoio/fomento ao Comércio Exterior utilizado pelo prestador. */
    private String mecanismoApoioComexPrestador;

    /** Mecanismo de apoio/fomento ao Comércio Exterior utilizado pelo tomador. */
    private String mecanismoApoioComexTomador;

    /** Movimentação temporária de bens. */
    private MovimentacaoTemporariaBens movimentacaoTemporariaBens;

    /** Número da Declaração de Importação (DI/DSI/DA/DRI-E) averbada. */
    private String numeroDeclaracaoImportacao;

    /** Número do Registro de Exportação (RE) averbado. */
    private String numeroRegistroExportacao;

    /** Compartilhamento de dados com o MDIC. 1 - Sim 2 - Não */
    private String mdic;

}
