package com.nfsenacional.enums;

/**
 * Emitente da DPS Baseado no schema: TSEmitenteDPS
 * <p>Porte de {@code Nfse\Enums\EmitenteDPS} (php-api).
 */
public enum EmitenteDPS {
    /** Prestador */
    PRESTADOR("1", "Prestador"),
    /** Tomador */
    TOMADOR("2", "Tomador"),
    /** Intermediário */
    INTERMEDIARIO("3", "Intermediário");

    private final String codigo;
    private final String descricao;

    EmitenteDPS(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static EmitenteDPS fromCodigo(String codigo) {
        for (EmitenteDPS v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para EmitenteDPS: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
