package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.TipoChaveDFe;
import com.nfsenacional.enums.TipoReembolsoRepasseRessarcimento;

/**
 * Porte de {@code Nfse\Dto\Nfse\ReembolsoDocumentoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReembolsoDocumentoData {

    /** Documento fiscal a que se refere a chave de DF-e do Repositório Nacional. */
    private TipoChaveDFe tipoChaveDfe;

    /** Descrição do DF-e a que se refere a chave. Preenchido apenas quando tipoChaveDFe = 9. */
    private String descricaoTipoChaveDfe;

    /** Chave do Documento Fiscal eletrônico do repositório nacional. */
    private String chaveDfe;

    /** Código do município emissor do documento fiscal que não se encontra no repositório nacional. */
    private String codigoMunicipioDocumentoFiscal;

    /** Número do documento fiscal que não se encontra no repositório nacional. */
    private String numeroDocumentoFiscal;

    /** Descrição do documento fiscal. */
    private String descricaoDocumentoFiscal;

    /** Número do documento não fiscal. */
    private String numeroDocumento;

    /** Descrição do documento não fiscal. */
    private String descricaoDocumento;

    /** Fornecedor do documento referenciado. */
    private FornecedorData fornecedor;

    /** Data da emissão do documento. Formato: AAAA-MM-DD */
    private String dataEmissaoDocumento;

    /** Data da competência do documento. Formato: AAAA-MM-DD */
    private String dataCompetenciaDocumento;

    /** Tipo de reembolso, repasse ou ressarcimento. */
    private TipoReembolsoRepasseRessarcimento tipoReembolso;

    /** Descrição do reembolso ou ressarcimento. Só deve ser informada quando tpReeRepRes = 99. */
    private String descricaoTipoReembolso;

    /** Valor monetário utilizado para não inclusão na base de cálculo do ISSQN, do IBS e da CBS da NFS-e que está sendo emitida (R$). */
    private Double valorReembolso;

}
