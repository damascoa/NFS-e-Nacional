package com.nfsenacional.signer;

/**
 * Erro ao carregar, validar ou usar o certificado digital A1 (PFX).
 *
 * @author Renato
 */
public class NfseCertificateException extends RuntimeException {

    public NfseCertificateException(String message) {
        super(message);
    }

    public NfseCertificateException(String message, Throwable cause) {
        super(message, cause);
    }
}
