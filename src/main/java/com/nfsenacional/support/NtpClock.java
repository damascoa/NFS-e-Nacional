package com.nfsenacional.support;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Relógio corrigido contra um servidor NTP público (SNTP, RFC 4330 simplificado) — usado pra
 * carimbar {@code dhEmi}/{@code dhEvento} com a hora certa mesmo que o relógio local tenha
 * alguns segundos de deriva.
 * <p>
 * Achado real (ver TASKS.md): a Sefin rejeita ({@code E0008 - A data de emissão da DPS não pode
 * ser posterior à data do seu processamento}) quando o relógio local está só 1-2 segundos à
 * frente do relógio de referência dela — mesmo com o Windows "certo" visualmente. Não dá pra
 * simplesmente confiar em {@link java.time.OffsetDateTime#now()} pra um campo com valor fiscal.
 *
 * @author Renato
 */
public final class NtpClock {

    /** Pool de NTP brasileiro (ntp.br / LNTP - Observatório Nacional). */
    private static final String[] SERVIDORES_PADRAO = {"a.st1.ntp.br", "b.st1.ntp.br", "pool.ntp.org"};
    private static final int PORTA_NTP = 123;
    private static final int TIMEOUT_MS = 3000;

    /** Diferença em nanossegundos entre "época NTP" (1900) e "época Unix" (1970). */
    private static final long OFFSET_EPOCA_NTP_UNIX_SEGUNDOS = 2208988800L;

    private NtpClock() {
    }

    /**
     * @return o instante atual corrigido pelo NTP, na zona local (mesmo comportamento de
     * {@code OffsetDateTime.now()}, mas com a hora certa). Se nenhum servidor responder, cai de
     * volta pro relógio local (com aviso no stderr) em vez de travar quem está usando o SDK.
     */
    public static OffsetDateTime now() {
        return now(ZoneId.systemDefault());
    }

    public static OffsetDateTime now(ZoneId zona) {
        Long offsetMillis = calcularOffsetMillis();
        Instant instanteCorrigido = offsetMillis != null
                ? Instant.now().plusMillis(offsetMillis)
                : Instant.now();
        return instanteCorrigido.atZone(zona).toOffsetDateTime();
    }

    /**
     * @return {@code offset} em milissegundos a somar em {@link Instant#now()} pra obter a hora
     * do servidor NTP, ou {@code null} se nenhum servidor respondeu.
     */
    private static Long calcularOffsetMillis() {
        for (String servidor : SERVIDORES_PADRAO) {
            try {
                return consultarServidor(servidor);
            } catch (IOException e) {
                // tenta o próximo servidor da lista.
            }
        }
        System.err.println("AVISO: não foi possível consultar nenhum servidor NTP ("
                + String.join(", ", SERVIDORES_PADRAO) + ") — usando relógio local sem correção. "
                + "Se a Sefin rejeitar com E0008, sincronize o relógio manualmente.");
        return null;
    }

    private static long consultarServidor(String servidor) throws IOException {
        byte[] pacote = new byte[48];
        pacote[0] = 0x1B; // LI=0, VN=3, Mode=3 (client)

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress endereco = InetAddress.getByName(servidor);

            long instanteEnvioMillis = System.currentTimeMillis();
            DatagramPacket requisicao = new DatagramPacket(pacote, pacote.length, endereco, PORTA_NTP);
            socket.send(requisicao);

            DatagramPacket resposta = new DatagramPacket(pacote, pacote.length);
            socket.receive(resposta);
            long instanteRecebimentoMillis = System.currentTimeMillis();

            // "Transmit Timestamp" do servidor: bytes 40-47 (segundos NTP + fração).
            long segundosNtp = readUnsignedInt(pacote, 40);
            long fracaoNtp = readUnsignedInt(pacote, 44);
            long segundosUnix = segundosNtp - OFFSET_EPOCA_NTP_UNIX_SEGUNDOS;
            long fracaoMillis = (fracaoNtp * 1000L) / 0x100000000L;

            long horaServidorMillis = segundosUnix * 1000L + fracaoMillis;

            // Corrige metade do round-trip (ida+volta), como um cliente SNTP simples faz.
            long roundTripMillis = instanteRecebimentoMillis - instanteEnvioMillis;
            long horaServidorAjustadaMillis = horaServidorMillis + (roundTripMillis / 2);

            return horaServidorAjustadaMillis - instanteRecebimentoMillis;
        } catch (SocketTimeoutException e) {
            throw new IOException("Timeout consultando " + servidor, e);
        }
    }

    private static long readUnsignedInt(byte[] buffer, int offset) {
        return ((buffer[offset] & 0xFFL) << 24)
                | ((buffer[offset + 1] & 0xFFL) << 16)
                | ((buffer[offset + 2] & 0xFFL) << 8)
                | (buffer[offset + 3] & 0xFFL);
    }
}
