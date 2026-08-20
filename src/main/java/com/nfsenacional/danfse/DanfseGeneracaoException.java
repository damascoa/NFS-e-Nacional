package com.nfsenacional.danfse;

/**
 * Erro ao compilar o template ou preencher/exportar o DANFSe em PDF.
 *
 * @author Renato
 */
public class DanfseGeneracaoException extends RuntimeException {

    public DanfseGeneracaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
