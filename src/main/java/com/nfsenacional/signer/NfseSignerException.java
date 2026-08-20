package com.nfsenacional.signer;

/**
 * Erro ao assinar (XMLDSig) a DPS ou um evento.
 *
 * @author Renato
 */
public class NfseSignerException extends RuntimeException {

    public NfseSignerException(String message) {
        super(message);
    }

    public NfseSignerException(String message, Throwable cause) {
        super(message, cause);
    }
}
