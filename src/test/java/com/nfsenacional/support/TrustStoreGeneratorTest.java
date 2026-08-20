package com.nfsenacional.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que {@link TrustStoreGenerator#gerar} produz um trust store PKCS12 válido, carregável de
 * volta, contendo tanto as raízes padrão da JVM quanto a raiz extra (GlobalSign Root R46) —
 * a raiz que falta no {@code cacerts} de JDKs mais antigos e que impedia o handshake TLS com
 * {@code sefin.producaorestrita.nfse.gov.br} (achado real, ver TASKS.md).
 */
class TrustStoreGeneratorTest {

    @Test
    void geraTrustStoreCarregavelComRaizExtra(@TempDir Path tempDir) throws Exception {
        Path destino = tempDir.resolve("truststore.p12");
        char[] senha = "changeit".toCharArray();

        TrustStoreGenerator.gerar(destino, senha);

        assertTrue(destino.toFile().exists(), "o arquivo do trust store deve existir");

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(destino.toFile())) {
            trustStore.load(fis, senha);
        }

        boolean temGlobalSignR46 = false;
        boolean temRaizesPadrao = false;

        Enumeration<String> aliases = trustStore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            X509Certificate cert = (X509Certificate) trustStore.getCertificate(alias);
            if (cert.getSubjectDN().getName().contains("GlobalSign Root R46")) {
                temGlobalSignR46 = true;
            }
            if (alias.startsWith("jvm-default-")) {
                temRaizesPadrao = true;
            }
        }

        assertTrue(temGlobalSignR46, "trust store deve conter a raiz GlobalSign Root R46");
        assertTrue(temRaizesPadrao, "trust store deve conter as raízes padrão da JVM também");
    }
}
