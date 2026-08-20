package com.nfsenacional.http;

import com.nfsenacional.NfseContext;
import com.nfsenacional.enums.TipoAmbiente;
import com.nfsenacional.support.TrustStoreGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Verificação isolada de parte da Etapa 6 (TASKS.md): confirma que o {@link SefinClient} monta o
 * client mTLS (KeyStore → SSLContext → OkHttpClient) sem exceção usando o certificado de teste e
 * um trust store gerado por {@link TrustStoreGenerator}.
 * <p>
 * <b>Não</b> faz nenhuma chamada de rede real — isso é a Etapa 10 (verificação contra homologação
 * real, exige certificado de teste emitido de verdade, não o autoassinado gerado via keytool).
 */
class SefinClientTest {

    @Test
    void montaClienteMtlsSemExcecao(@TempDir Path tempDir) {
        Path trustStorePath = TrustStoreGenerator.gerar(tempDir.resolve("truststore.p12"), "changeit".toCharArray());

        NfseContext context = NfseContext.builder()
                .ambiente(TipoAmbiente.HOMOLOGACAO)
                .certificatePath("src/test/resources/certificado-teste.p12")
                .certificatePassword("senha123")
                .trustStorePath(trustStorePath.toString())
                .trustStorePassword("changeit")
                .build();

        assertDoesNotThrow(() -> new SefinClient(context));
    }
}
