package com.nfsenacional.signer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Enumeration;

/**
 * Certificado digital A1 (PKCS#12/PFX) usado para autenticação mTLS junto à Sefin Nacional e para
 * assinar (XMLDSig) a DPS/eventos antes do envio.
 * <p>
 * Porte de {@code Nfse\Signer\Certificate} (php-api) — usando {@link KeyStore} nativo do JDK
 * (PKCS12) em vez de {@code openssl_pkcs12_read}.
 *
 * @author Renato
 */
public class NfseCertificate {

    private static final int TAMANHO_MINIMO_CHAVE_BITS = 2048;

    private final PrivateKey privateKey;
    private final X509Certificate certificate;
    private final byte[] pfxBytes;
    private final String password;

    public NfseCertificate(String pfxPath, String password) {
        this(readFile(pfxPath), password);
    }

    public static NfseCertificate fromContent(byte[] pfxContent, String password) {
        return new NfseCertificate(pfxContent, password);
    }

    private NfseCertificate(byte[] pfxBytes, String password) {
        this.pfxBytes = pfxBytes;
        this.password = password;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream is = new ByteArrayInputStream(pfxBytes)) {
                keyStore.load(is, password.toCharArray());
            }

            String alias = firstAlias(keyStore);
            this.privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            this.certificate = (X509Certificate) keyStore.getCertificate(alias);

            if (this.privateKey == null || this.certificate == null) {
                throw new NfseCertificateException("Senha do certificado incorreta ou arquivo inválido/corrompido.");
            }
        } catch (NfseCertificateException e) {
            throw e;
        } catch (Exception e) {
            throw new NfseCertificateException(
                    "Senha do certificado incorreta ou arquivo inválido/corrompido. Detalhes: " + e.getMessage(), e);
        }

        validarValidade();
        validarForcaDaChave();
    }

    private static String firstAlias(KeyStore keyStore) throws Exception {
        Enumeration<String> aliases = keyStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keyStore.isKeyEntry(alias)) {
                return alias;
            }
        }
        throw new NfseCertificateException("Nenhuma chave privada encontrada no arquivo PFX.");
    }

    private void validarValidade() {
        try {
            certificate.checkValidity();
        } catch (CertificateExpiredException e) {
            String validTo = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(certificate.getNotAfter());
            throw new NfseCertificateException(
                    "O certificado digital está vencido. Data de validade: " + validTo + ". Por favor, utilize um certificado válido.");
        } catch (CertificateNotYetValidException e) {
            throw new NfseCertificateException("O certificado digital ainda não é válido (data de início no futuro).");
        }
    }

    private void validarForcaDaChave() {
        if (certificate.getPublicKey() instanceof RSAPublicKey) {
            int bits = ((RSAPublicKey) certificate.getPublicKey()).getModulus().bitLength();
            if (bits < TAMANHO_MINIMO_CHAVE_BITS) {
                throw new NfseCertificateException(
                        "O certificado digital possui uma chave muito fraca (" + bits + " bits, menor que "
                                + TAMANHO_MINIMO_CHAVE_BITS + " bits) e foi rejeitado pelas políticas de segurança do servidor. "
                                + "Por favor, utilize um certificado mais seguro (A1 ou A3 atualizado).");
            }
        }
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    /** Bytes originais do PFX — usados para configurar o client certificate do cliente HTTP (mTLS). */
    public byte[] getPfxBytes() {
        return pfxBytes;
    }

    /** Certificado X.509 codificado em Base64, sem os delimitadores PEM — usado dentro de {@code <X509Certificate>} no XMLDSig. */
    public String getCleanCertificateBase64() {
        try {
            return Base64.getEncoder().encodeToString(certificate.getEncoded());
        } catch (Exception e) {
            throw new NfseCertificateException("Falha ao codificar o certificado: " + e.getMessage(), e);
        }
    }

    /**
     * Assina o conteúdo com a chave privada do certificado.
     *
     * @param content   bytes a assinar (tipicamente o resultado da canonicalização C14N)
     * @param algorithm algoritmo Java (ex: {@code "SHA1withRSA"}, {@code "SHA256withRSA"})
     */
    public byte[] sign(byte[] content, String algorithm) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(content);
            return signature.sign();
        } catch (Exception e) {
            throw new NfseCertificateException("Falha ao assinar o conteúdo: " + e.getMessage(), e);
        }
    }

    /**
     * Monta um {@link SSLContext} configurado com este certificado como client certificate (mTLS)
     * e um trust store explícito pra validar o servidor — usado pelos clientes HTTP
     * ({@code SefinClient}, {@code AdnClient}, {@code CncClient}).
     * <p>
     * <b>Por que trust store explícito, e não o {@code cacerts} do sistema?</b> Porque
     * {@code sefin.producaorestrita.nfse.gov.br} serve um certificado cuja cadeia sobe até a raiz
     * pública GlobalSign Root R46 — legítima, mas ausente no {@code cacerts} de JDKs mais antigos, e
     * corrigir isso no {@code cacerts} do sistema normalmente exige privilégio de administrador. Gere
     * um trust store próprio com {@link com.nfsenacional.support.TrustStoreGenerator#gerar}.
     *
     * @param trustStorePath     caminho do trust store PKCS12
     * @param trustStorePassword senha do trust store
     */
    public javax.net.ssl.SSLContext buildMtlsSslContext(String trustStorePath, String trustStorePassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream is = new ByteArrayInputStream(pfxBytes)) {
                keyStore.load(is, password.toCharArray());
            }

            javax.net.ssl.KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance(
                    javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password.toCharArray());

            javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance(
                    javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(carregarTrustStore(trustStorePath, trustStorePassword));

            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
            return sslContext;
        } catch (NfseCertificateException e) {
            throw e;
        } catch (Exception e) {
            throw new NfseCertificateException("Falha ao preparar SSLContext (mTLS): " + e.getMessage(), e);
        }
    }

    /** {@link X509TrustManager} do trust store explícito informado — usar junto com {@link #buildMtlsSslContext}. */
    public javax.net.ssl.X509TrustManager trustManager(String trustStorePath, String trustStorePassword) {
        try {
            javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance(
                    javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(carregarTrustStore(trustStorePath, trustStorePassword));
            for (javax.net.ssl.TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof javax.net.ssl.X509TrustManager) {
                    return (javax.net.ssl.X509TrustManager) tm;
                }
            }
            throw new NfseCertificateException("Nenhum X509TrustManager disponível no trust store informado.");
        } catch (NfseCertificateException e) {
            throw e;
        } catch (Exception e) {
            throw new NfseCertificateException("Falha ao carregar trust manager: " + e.getMessage(), e);
        }
    }

    private KeyStore carregarTrustStore(String path, String password) throws Exception {
        if (path == null) {
            throw new NfseCertificateException(
                    "trustStorePath não informado. Gere um com com.nfsenacional.support.TrustStoreGenerator.gerar(...).");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new NfseCertificateException(
                    "Trust store não encontrado: " + path + ". Gere um com com.nfsenacional.support.TrustStoreGenerator.gerar(...).");
        }
        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = new java.io.FileInputStream(file)) {
            trustStore.load(is, password != null ? password.toCharArray() : null);
        }
        return trustStore;
    }

    private static byte[] readFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new NfseCertificateException("Certificado não encontrado: " + path);
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            int total = 0;
            int read;
            while (total < bytes.length && (read = fis.read(bytes, total, bytes.length - total)) != -1) {
                total += read;
            }
            return bytes;
        } catch (IOException e) {
            throw new NfseCertificateException("Erro ao ler o certificado: " + path, e);
        }
    }
}
