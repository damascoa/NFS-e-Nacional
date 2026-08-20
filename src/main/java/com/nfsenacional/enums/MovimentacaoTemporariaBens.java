package com.nfsenacional.enums;

/**
 * Vínculo da Operação à Movimentação Temporária de Bens Baseado no schema: TSMovTempBens
 * <p>Porte de {@code Nfse\Enums\MovimentacaoTemporariaBens} (php-api).
 */
public enum MovimentacaoTemporariaBens {
    /** Nenhum */
    NENHUM("0", "Nenhum"),
    /** Não */
    NAO("1", "Não"),
    /** Sim (Importação) */
    SIM_IMPORTACAO("2", "Sim (Importação)"),
    /** Sim (Exportação) */
    SIM_EXPORTACAO("3", "Sim (Exportação)");

    private final String codigo;
    private final String descricao;

    MovimentacaoTemporariaBens(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static MovimentacaoTemporariaBens fromCodigo(String codigo) {
        for (MovimentacaoTemporariaBens v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para MovimentacaoTemporariaBens: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
