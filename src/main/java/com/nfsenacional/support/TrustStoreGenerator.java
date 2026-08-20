package com.nfsenacional.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Gera um trust store PKCS12 próprio da aplicação (raízes confiáveis padrão da JVM + raízes extras
 * necessárias pra validar o TLS da Sefin Nacional/ADN/CNC), pra usar como
 * {@code NfseContext.trustStorePath} sem precisar alterar o {@code cacerts} do JDK do sistema
 * (que normalmente exige privilégio de administrador pra gravar).
 * <p>
 * Motivo de existir: {@code sefin.producaorestrita.nfse.gov.br} serve um certificado TLS cuja
 * cadeia sobe até a raiz pública <b>GlobalSign Root R46</b> — legítima, mas relativamente nova
 * (submetida à Mozilla Root Store no fim de 2019), então JDKs mais antigos (ex: 1.8.0_202, de
 * março/2019) não vêm com ela no {@code cacerts} padrão. Isso derruba o handshake mTLS com
 * {@code PKIX path building failed}, mesmo com um certificado de cliente A1/A3 100% válido.
 *
 * @author Renato
 */
public final class TrustStoreGenerator {

    /**
     * GlobalSign Root R46 (DER, base64) — {@code CN=GlobalSign Root R46, O=GlobalSign nv-sa, C=BE}.
     * SHA-256: {@code 4F:A3:12:6D:8D:3A:11:D1:C4:85:5A:4F:80:7C:BA:D6:CF:91:9D:3A:5A:88:B0:3B:EA:2C:63:72:D9:3C:40:C9}.
     * Baixado de {@code https://secure.globalsign.com/cacert/rootr46.crt} e conferido contra o
     * fingerprint oficial publicado pela GlobalSign.
     */
    private static final String GLOBALSIGN_ROOT_R46_BASE64 =
            "MIIFWjCCA0KgAwIBAgISEdK7udcjGJ5AXwqdLdDfJWfRMA0GCSqGSIb3DQEBDAUAMEYxCzAJBgNVBAYT"
          + "AkJFMRkwFwYDVQQKExBHbG9iYWxTaWduIG52LXNhMRwwGgYDVQQDExNHbG9iYWxTaWduIFJvb3QgUjQ2"
          + "MB4XDTE5MDMyMDAwMDAwMFoXDTQ2MDMyMDAwMDAwMFowRjELMAkGA1UEBhMCQkUxGTAXBgNVBAoTEEds"
          + "b2JhbFNpZ24gbnYtc2ExHDAaBgNVBAMTE0dsb2JhbFNpZ24gUm9vdCBSNDYwggIiMA0GCSqGSIb3DQEB"
          + "AQUAA4ICDwAwggIKAoICAQCsrHQy6LNl5brtQyYdpokNRbopiLKkHWPd08EsCVeJOaFV6Wc0dwxu5FUd"
          + "UiXSE2te4R2pt32JMl8Nnp8semNgQB+msLZ4j5lUlghYruQGvGIFAha/r6gjA7aUD7xubMLL1aa7DOn2"
          + "wQL7Id5m3RerdELv8HQvJfTqa1VbkNud316HCkD7rRlr+/fKYIje2sGP1q7Vf9Q8g+7XFkyDRTNrJ9CG"
          + "0Bwta/OrffGFqfUo0q3v84RLHIf8E6M6cqJaESvWJ3En7YEtbWaBkoe0G1h6zD8K+kZPTXhc+CtI4wSE"
          + "y132tGqzZfxCnlEmIyDLPRT5ge1lFgBPGmSXZgjPjHvjK8Cd+RTyG/FWaha/LIWFzXg4mutCagI0GIMX"
          + "TpRW+LaCtfOW3T3zvn8gdz57GSNrLNRyc0NXfeD412lPFzYE+cCQYDdF3uYM2HSNrpyibXRdQr4G9dlk"
          + "bgIQrImwTDsHTUB+JMWKmIJ5jqSngiCNI/onccnfxkF0oE32kRbcRoxfKWMxWXEM2G/CtjJ9++ZdU6Z+"
          + "Ffy7dXxd7Pj2Fxzsx2sZy/N78CsHpdlseVR2bJ0cpm4O6XkMqCNqo98bMDGfsVR7/mrLZqrcZdCinkqa"
          + "ByFrgY/bxFn63iLABJzjqls2k+g9vXqhnQt2sQvHnf3PmKgGwvgqo6GDoLclcqUC4wIDAQABo0IwQDAO"
          + "BgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUA1yrc4GHqMywptWU4jaWSf8F"
          + "mSwwDQYJKoZIhvcNAQEMBQADggIBAHx47PYCLLtbfpIrXTncvtgdokIzTfnvpCo7RGkerNlFo048p9gk"
          + "UbJUHJNOxO97k4VgJuoJSOD1u8fpaNK7ajFxzHmuEajwmf3lH7wvqMxX63bEIaZHU1VNaL8FpO7XJqti"
          + "2kM3S+LGteWygxk6x9PbTZ4IevPuzz5i+6zoYMzRx6Fcg0XERczzF2sUyQQCPtIkpnnpHs6i58FZFZ8d"
          + "4kuaPp92CC1r2LpXFNqD6v6MVenQTqnMdzGxRBF6XLE+0xRFFRhiJBPSy03OXIPBNvIQtQ6IbbjhVp+J"
          + "3pZmOUdkLG5NrmJ7v2B0GbhWrJKsFjLtrWhV/pi60zTe9Mlhww6G9kuEYO4Ne7UyWHmRVSyBQ7N0H3qq"
          + "JZ4d16GLuc1CLgSkZoNNiTW2bKg2SnkheCLQQrzRQDGQob4Ez8pn7fXwgNNgyYMqIgXQBztSvwyeqiv5"
          + "u+YfjyW6hY0XHgL+XVAEV8/+LbzvXMAaq7afJMbfc2hIkCwU9D9SGuTSyxTDYWnP4vkYxboznxSjBF25"
          + "cfe1lNj2M8FawTSLfJvdkzrnE6JwYZ+vj+vYxXX4M2bUdGc6N3ec592kD3ZDZopD8p/7DEJ4Y9HiD297"
          + "1KE9dJeFt0g5QdYg/NA6s/rob8SKunE3vouXsXgxT7PntgMTzlSdriVZzH81Xwj3QEUxeCp6";

    private TrustStoreGenerator() {
    }

    /**
     * Gera (ou regenera) o trust store em {@code destino}: todas as raízes confiáveis padrão da
     * JVM que está rodando + as raízes extras listadas em {@link #raizesExtras()}.
     *
     * @param destino caminho do arquivo PKCS12 a criar (sobrescreve se já existir)
     * @param senha   senha do trust store gerado
     * @return o próprio {@code destino}, por conveniência
     */
    public static Path gerar(Path destino, char[] senha) {
        try {
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(null, null);

            int i = 0;
            for (X509Certificate cert : raizesPadraoDaJvm()) {
                trustStore.setCertificateEntry("jvm-default-" + (i++), cert);
            }
            for (X509Certificate cert : raizesExtras()) {
                trustStore.setCertificateEntry(aliasPara(cert), cert);
            }

            if (destino.getParent() != null) {
                Files.createDirectories(destino.getParent());
            }
            try (OutputStream os = Files.newOutputStream(destino)) {
                trustStore.store(os, senha);
            }

            return destino;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new IllegalStateException("Falha ao gerar trust store em " + destino + ": " + e.getMessage(), e);
        }
    }

    /** As raízes que a JVM em execução já considera confiáveis (o {@code cacerts} do JDK atual). */
    private static X509Certificate[] raizesPadraoDaJvm() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);
        for (javax.net.ssl.TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return ((X509TrustManager) tm).getAcceptedIssuers();
            }
        }
        return new X509Certificate[0];
    }

    /**
     * Raízes que faltam em JDKs mais antigos mas são necessárias pra validar o TLS dos domínios do
     * Sistema Nacional NFS-e. Hoje só a GlobalSign Root R46 (usada por {@code *.nfse.gov.br} via
     * SERPRO) — se outro domínio do sistema precisar de outra raiz, adicionar aqui.
     */
    private static X509Certificate[] raizesExtras() throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate globalSignRootR46 = (X509Certificate) cf.generateCertificate(
                new ByteArrayInputStream(Base64.getDecoder().decode(GLOBALSIGN_ROOT_R46_BASE64)));
        return new X509Certificate[]{globalSignRootR46};
    }

    private static String aliasPara(X509Certificate cert) {
        return "extra-" + cert.getSubjectDN().getName().replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
    }
}
