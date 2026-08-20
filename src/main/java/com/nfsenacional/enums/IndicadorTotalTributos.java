package com.nfsenacional.enums;

/**
 * Indicador de informação de valor total de tributos
 * <p>Porte de {@code Nfse\Enums\IndicadorTotalTributos} (php-api).
 */
public enum IndicadorTotalTributos {
    /** Nenhum */
    NENHUM(0, "Nenhum"),
    /** Valor total aproximado dos tributos federais, estaduais e municipais (Lei 12.741/12) */
    LEI12741(1, "Valor total aproximado dos tributos federais, estaduais e municipais (Lei 12.741/12)"),
    /** Sem informação de tributos totais */
    SEM_INFORMACAO(2, "Sem informação de tributos totais");

    private final int codigo;
    private final String descricao;

    IndicadorTotalTributos(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static IndicadorTotalTributos fromCodigo(int codigo) {
        for (IndicadorTotalTributos v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para IndicadorTotalTributos: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
