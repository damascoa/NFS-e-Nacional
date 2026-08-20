package com.nfsenacional.enums;

/**
 * Tipo de Retenção do PIS/COFINS e CSLL
 * <p>Porte de {@code Nfse\Enums\TipoRetencaoPisCofins} (php-api).
 */
public enum TipoRetencaoPisCofins {
    /** PIS/COFINS/CSLL Não Retidos */
    NAO_RETIDOS(0, "PIS/COFINS/CSLL Não Retidos"),
    /** PIS/COFINS Retido */
    PIS_COFINS_RETIDO(1, "PIS/COFINS Retido"),
    /** PIS/COFINS Não Retido */
    PIS_COFINS_NAO_RETIDO(2, "PIS/COFINS Não Retido"),
    /** PIS/COFINS/CSLL Retidos */
    RETIDOS(3, "PIS/COFINS/CSLL Retidos"),
    /** PIS/COFINS Retidos, CSLL Não Retido */
    PIS_COFINS_RETIDOS_CSLL_NAO_RETIDO(4, "PIS/COFINS Retidos, CSLL Não Retido"),
    /** PIS Retido, COFINS/CSLL Não Retido */
    PIS_RETIDO_COFINS_CSLL_NAO_RETIDO(5, "PIS Retido, COFINS/CSLL Não Retido"),
    /** COFINS Retido, PIS/CSLL Não Retido */
    COFINS_RETIDO_PIS_CSLL_NAO_RETIDO(6, "COFINS Retido, PIS/CSLL Não Retido"),
    /** PIS Não Retido, COFINS/CSLL Retidos */
    PIS_NAO_RETIDO_COFINS_CSLL_RETIDOS(7, "PIS Não Retido, COFINS/CSLL Retidos"),
    /** PIS/COFINS Não Retidos, CSLL Retido */
    PIS_COFINS_NAO_RETIDOS_CSLL_RETIDO(8, "PIS/COFINS Não Retidos, CSLL Retido"),
    /** COFINS Não Retido, PIS/CSLL Retidos */
    COFINS_NAO_RETIDO_PIS_CSLL_RETIDOS(9, "COFINS Não Retido, PIS/CSLL Retidos");

    private final int codigo;
    private final String descricao;

    TipoRetencaoPisCofins(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoRetencaoPisCofins fromCodigo(int codigo) {
        for (TipoRetencaoPisCofins v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoRetencaoPisCofins: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
