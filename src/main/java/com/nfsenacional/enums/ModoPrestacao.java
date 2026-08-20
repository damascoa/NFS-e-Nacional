package com.nfsenacional.enums;

/**
 * Modo de Prestação do Serviço (Comércio Exterior) Baseado no schema: TSMdPrestacao
 * <p>Porte de {@code Nfse\Enums\ModoPrestacao} (php-api).
 */
public enum ModoPrestacao {
    /** Desconhecido ou Não Aplicável */
    DESCONHECIDO("0", "Desconhecido ou Não Aplicável"),
    /** Transfronteiriço */
    TRANSFRONTEIRICO("1", "Transfronteiriço"),
    /** Consumo no Exterior */
    CONSUMO_NO_EXTERIOR("2", "Consumo no Exterior"),
    /** Presença Comercial no Exterior */
    PRESENCA_COMERCIAL_NO_EXTERIOR("3", "Presença Comercial no Exterior"),
    /** Movimento Temporário de Pessoas Físicas */
    MOVIMENTO_TEMPORARIO_PESSOAS_FISICAS("4", "Movimento Temporário de Pessoas Físicas");

    private final String codigo;
    private final String descricao;

    ModoPrestacao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static ModoPrestacao fromCodigo(String codigo) {
        for (ModoPrestacao v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para ModoPrestacao: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
