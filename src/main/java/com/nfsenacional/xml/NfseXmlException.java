package com.nfsenacional.xml;

/**
 * Erro ao montar ou interpretar o XML da DPS/NFS-e/evento.
 *
 * @author Renato
 */
public class NfseXmlException extends RuntimeException {

    public NfseXmlException(String message) {
        super(message);
    }

    public NfseXmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
