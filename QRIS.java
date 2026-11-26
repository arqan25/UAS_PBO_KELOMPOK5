import java.math.BigDecimal;

public class QRIS implements Pembayaran {

    public String getNamaPembayaran() {
        return "QRIS";
    }

    public void prosesPembayaran(BigDecimal jumlah) {
        System.out.println("[QRIS] Pembayaran sebesar Rp " + jumlah + " telah dicatat (simulasi).");
    }
}
