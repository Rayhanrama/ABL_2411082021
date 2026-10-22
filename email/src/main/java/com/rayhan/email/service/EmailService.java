package com.rayhan.email.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rayhan.email.vo.Order;
import com.rayhan.email.vo.Pelanggan;
import com.rayhan.email.vo.Produk;

/**
 * Menyusun dan mengirim email notifikasi order via SMTP (Mailtrap Sandbox).
 * Data order/produk/pelanggan diambil lebih dulu oleh OrderEmailListener melalui Eureka.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.fallback-to}")
    private String fallbackTo;

    public void sendOrderNotification(Order order, Produk produk, Pelanggan pelanggan) {
        String to = resolveRecipient(pelanggan);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Konfirmasi Order #" + safeId(order));
            message.setText(buildBody(order, produk, pelanggan));

            mailSender.send(message);
            log.info("[MAIL] Email order #{} terkirim ke {}", safeId(order), to);
        } catch (Exception e) {
            log.error("[MAIL] Gagal mengirim email order #{}: {}", safeId(order), e.getMessage(), e);
        }
    }

    private String resolveRecipient(Pelanggan pelanggan) {
        if (pelanggan != null && pelanggan.getEmail() != null && !pelanggan.getEmail().isBlank()) {
            return pelanggan.getEmail();
        }
        return fallbackTo;
    }

    private String safeId(Order order) {
        return order != null && order.getId() != null ? String.valueOf(order.getId()) : "?";
    }

    private String buildBody(Order order, Produk produk, Pelanggan pelanggan) {
        StringBuilder sb = new StringBuilder();
        sb.append("Terima kasih telah melakukan order!\n\n");

        sb.append("Nomor Order  : #").append(safeId(order)).append("\n");
        sb.append("Tanggal      : ").append(order != null ? order.getTgl_transaksi() : "-").append("\n");
        sb.append("Produk       : ").append(produk != null ? produk.getNama() : "-").append("\n");
        sb.append("Harga Satuan : ").append(produk != null ? produk.getHarga() : 0).append("\n");
        sb.append("Jumlah       : ").append(order != null ? order.getJumlah() : 0).append("\n");
        sb.append("Total Bayar  : ").append(order != null ? order.getTotal() : 0).append("\n\n");

        sb.append("Alamat Pengiriman:\n");
        sb.append("  ").append(pelanggan != null ? pelanggan.getNama() : "-").append("\n");
        sb.append("  ").append(pelanggan != null ? pelanggan.getAlamat() : "-").append("\n");

        return sb.toString();
    }
}
