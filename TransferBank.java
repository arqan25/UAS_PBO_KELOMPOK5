import java.math.BigDecimal;

public class TransferBank implements Pembayaran {
    public String getNamaPembayaran() {
        return "Transfer Bank";
    }

    public void prosesPembayaran(BigDecimal jumlah) {
        System.out.println("[Transfer Bank] Pembayaran sebesar Rp " + jumlah + " telah dicatat (simulasi).");
    }
}