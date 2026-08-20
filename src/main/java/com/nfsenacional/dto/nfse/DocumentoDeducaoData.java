package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.TipoDeducaoReducao;

/**
 * Porte de {@code Nfse\Dto\Nfse\DocumentoDeducaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoDeducaoData {

    /** Chave de NFS-e. */
    private String chaveNfse;

    /** Chave de NF-e. */
    private String chaveNfe;

    /** Tipo de dedução/redução. */
    private TipoDeducaoReducao tipoDeducaoReducao;

    /** Descrição de outras deduções. */
    private String descricaoOutrasDeducoes;

    /** Data de emissão do documento. */
    private String dataEmissaoDocumento;

    /** Valor dedutível/redutível. */
    private Double valorDedutivelRedutivel;

    /** Valor de dedução/redução. */
    private Double valorDeducaoReducao;

    /** Informações do fornecedor. */
    private FornecedorData fornecedor;

}
