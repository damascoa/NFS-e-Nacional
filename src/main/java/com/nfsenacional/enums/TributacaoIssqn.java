package com.nfsenacional.enums;

/**
 * Tributação do ISSQN
 * <p>Porte de {@code Nfse\Enums\TributacaoIssqn} (php-api).
 */
public enum TributacaoIssqn {
    /** Operação tributável */
    OPERACAO_TRIBUTAVEL(1, "Operação tributável"),
    /** Imunidade */
    IMUNIDADE(2, "Imunidade"),
    /** Exportação de serviço */
    EXPORTACAO_SERVICO(3, "Exportação de serviço"),
    /** Não Incidência */
    NAO_INCIDENCIA(4, "Não Incidência");

    private final int codigo;
    private final String descricao;

    TributacaoIssqn(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TributacaoIssqn fromCodigo(int codigo) {
        for (TributacaoIssqn v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TributacaoIssqn: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
