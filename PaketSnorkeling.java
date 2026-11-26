import java.math.BigDecimal;

public class PaketSnorkeling extends Barang {

    public PaketSnorkeling(String id, String nama, BigDecimal tarifHarian, String deskripsi) {
        super(id, nama, "Paket Snorkeling", tarifHarian, deskripsi);
    }

    public PaketSnorkeling(String id, String nama, BigDecimal tarifHarian, String deskripsi, int stok) {
        super(id, nama, "Paket Snorkeling", tarifHarian, deskripsi, stok);
    }

    public BigDecimal hitungBiaya(int jumlahHari) {
        return getTarifHarian().multiply(new BigDecimal(jumlahHari));
    }

    public String getFormulaDescription() {
        return "Tarif Harian x Jumlah Hari";
    }
}