package com.nfsenacional.enums;

/**
 * Tipo de valor incluído no documento, recebido por estar relacionado a operações de terceiros, objeto de reembolso, repasse ou ressarcimento pelo recebedor, já tributados
 * <p>Porte de {@code Nfse\Enums\TipoReembolsoRepasseRessarcimento} (php-api).
 */
public enum TipoReembolsoRepasseRessarcimento {
    /** Repasse de remuneração por intermediação de imóveis a demais corretores envolvidos na operação */
    INTERMEDIACAO_IMOVEIS("01", "Repasse de remuneração por intermediação de imóveis a demais corretores envolvidos na operação"),
    /** Repasse de valores a fornecedor relativo a fornecimento intermediado por agência de turismo */
    AGENCIA_TURISMO("02", "Repasse de valores a fornecedor relativo a fornecimento intermediado por agência de turismo"),
    /** Reembolso ou ressarcimento recebido por agência de propaganda e publicidade por valores pagos relativos a serviços de produção externa por conta e ordem de terceiro */
    PRODUCAO_EXTERNA("03", "Reembolso ou ressarcimento recebido por agência de propaganda e publicidade por valores pagos relativos a serviços de produção externa por conta e ordem de terceiro"),
    /** Reembolso ou ressarcimento recebido por agência de propaganda e publicidade por valores pagos relativos a serviços de mídia por conta e ordem de terceiro */
    MIDIA("04", "Reembolso ou ressarcimento recebido por agência de propaganda e publicidade por valores pagos relativos a serviços de mídia por conta e ordem de terceiro"),
    /** Outros reembolsos ou ressarcimentos recebidos por valores pagos relativos a operações por conta e ordem de terceiro */
    OUTROS("99", "Outros reembolsos ou ressarcimentos recebidos por valores pagos relativos a operações por conta e ordem de terceiro");

    private final String codigo;
    private final String descricao;

    TipoReembolsoRepasseRessarcimento(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoReembolsoRepasseRessarcimento fromCodigo(String codigo) {
        for (TipoReembolsoRepasseRessarcimento v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoReembolsoRepasseRessarcimento: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
