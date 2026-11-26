import java.math.BigDecimal;

public interface Pembayaran {
    String getNamaPembayaran();

    void prosesPembayaran(BigDecimal jumlah);
}