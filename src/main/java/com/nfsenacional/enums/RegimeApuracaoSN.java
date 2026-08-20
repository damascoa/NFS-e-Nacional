package com.nfsenacional.enums;

/**
 * Regime de apuração dos tributos (SN) Baseado no schema: TSRegApTribSN
 * <p>Porte de {@code Nfse\Enums\RegimeApuracaoSN} (php-api).
 */
public enum RegimeApuracaoSN {
    /** Regime de apuração dos tributos federais e municipal pelo SN */
    SIMPLES_NACIONAL("1", "Regime de apuração dos tributos federais e municipal pelo SN"),
    /** Regime de apuração dos tributos federais pelo SN e municipal pelo regime normal (ISSQN) */
    NORMAL("2", "Regime de apuração dos tributos federais pelo SN e municipal pelo regime normal (ISSQN)"),
    /** Regime de apuração dos tributos pelo SN (MEI) */
    MICROEMPREENDEDOR_INDIVIDUAL("3", "Regime de apuração dos tributos pelo SN (MEI)");

    private final String codigo;
    private final String descricao;

    RegimeApuracaoSN(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RegimeApuracaoSN fromCodigo(String codigo) {
        for (RegimeApuracaoSN v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para RegimeApuracaoSN: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
