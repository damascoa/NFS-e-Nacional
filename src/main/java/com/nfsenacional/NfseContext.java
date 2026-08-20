package com.nfsenacional;

import com.nfsenacional.dto.http.Endpoint;
import com.nfsenacional.enums.TipoAmbiente;
import lombok.Getter;

/**
 * Configuração de uma integração com o Sistema Nacional NFS-e: ambiente, certificado digital A1 e,
 * opcionalmente, endpoint customizado (municípios com infraestrutura própria).
 * <p>
 * Porte de {@code Nfse\Http\NfseContext} (php-api).
 *
 * @author Renato
 */
@Getter
public final class NfseContext {

    private final TipoAmbiente ambiente;
    private final String certificatePath;
    private final String certificatePassword;
    private final String codigoMunicipio;
    private final Endpoint endpoint;
    private final byte[] certificateContent;
    private final String trustStorePath;
    private final String trustStorePassword;

    private NfseContext(Builder b) {
        if (b.certificatePath == null && b.certificateContent == null) {
            throw new IllegalArgumentException("Informe certificatePath ou certificateContent.");
        }
        if (b.trustStorePath == null) {
            throw new IllegalArgumentException(
                    "Informe trustStorePath — caminho de um trust store PKCS12 confiável pra validar "
                  + "o TLS da Sefin/ADN/CNC. Gere um com com.nfsenacional.support.TrustStoreGenerator.gerar(...) "
                  + "se não tiver um (ex: JDKs mais antigos não confiam na raiz GlobalSign Root R46 usada por "
                  + "*.nfse.gov.br, e corrigir isso no cacerts do sistema normalmente exige admin).");
        }
        this.ambiente = b.ambiente;
        this.certificatePath = b.certificatePath;
        this.certificatePassword = b.certificatePassword;
        this.codigoMunicipio = b.codigoMunicipio;
        this.endpoint = b.endpoint;
        this.certificateContent = b.certificateContent;
        this.trustStorePath = b.trustStorePath;
        this.trustStorePassword = b.trustStorePassword != null ? b.trustStorePassword : "changeit";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TipoAmbiente ambiente;
        private String certificatePath;
        private String certificatePassword;
        private String codigoMunicipio;
        private Endpoint endpoint;
        private byte[] certificateContent;
        private String trustStorePath;
        private String trustStorePassword;

        public Builder ambiente(TipoAmbiente ambiente) {
            this.ambiente = ambiente;
            return this;
        }

        public Builder certificatePath(String certificatePath) {
            this.certificatePath = certificatePath;
            return this;
        }

        public Builder certificatePassword(String certificatePassword) {
            this.certificatePassword = certificatePassword;
            return this;
        }

        public Builder codigoMunicipio(String codigoMunicipio) {
            this.codigoMunicipio = codigoMunicipio;
            return this;
        }

        public Builder endpoint(Endpoint endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder certificateContent(byte[] certificateContent) {
            this.certificateContent = certificateContent;
            return this;
        }

        /** Caminho de um trust store PKCS12 — obrigatório. Ver {@link com.nfsenacional.support.TrustStoreGenerator}. */
        public Builder trustStorePath(String trustStorePath) {
            this.trustStorePath = trustStorePath;
            return this;
        }

        /** Senha do trust store; default {@code "changeit"} (mesmo default do {@code cacerts} do JDK) se omitida. */
        public Builder trustStorePassword(String trustStorePassword) {
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public NfseContext build() {
            return new NfseContext(this);
        }
    }
}
