package com.nfsenacional.enums;

/**
 * Tipo de Ambiente do Sistema Nacional NFS-e Baseado no schema: TSTipoAmbiente
 * <p>Porte de {@code Nfse\Enums\TipoAmbiente} (php-api).
 */
public enum TipoAmbiente {
    /** Produção */
    PRODUCAO("1", "Produção"),
    /** Homologação */
    HOMOLOGACAO("2", "Homologação");

    private final String codigo;
    private final String descricao;

    TipoAmbiente(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoAmbiente fromCodigo(String codigo) {
        for (TipoAmbiente v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoAmbiente: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
