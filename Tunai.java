import java.math.BigDecimal;

public class Tunai implements Pembayaran {
    public String getNamaPembayaran() {
        return "Tunai";
    }

    public void prosesPembayaran(BigDecimal jumlah) {
        System.out.println("[Tunai] Pembayaran sebesar Rp " + jumlah + " telah dicatat (simulasi).");
    }
}