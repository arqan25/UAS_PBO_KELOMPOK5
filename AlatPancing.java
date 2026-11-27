import java.math.BigDecimal;

public class AlatPancing extends Barang {
    private BigDecimal biayaPerawatan;

    public AlatPancing(String id, String nama, BigDecimal tarifHarian, String deskripsi, BigDecimal biayaPerawatan) {
        super(id, nama, "Alat Pancing", tarifHarian, deskripsi);
        this.biayaPerawatan = biayaPerawatan;
    }

    public AlatPancing(String id, String nama, BigDecimal tarifHarian, String deskripsi, BigDecimal biayaPerawatan,
            int stok) {
        super(id, nama, "Alat Pancing", tarifHarian, deskripsi, stok);
        this.biayaPerawatan = biayaPerawatan;
    }

    public BigDecimal getBiayaPerawatan() {
        return biayaPerawatan;
    }

    public void setBiayaPerawatan(BigDecimal biayaPerawatan) {
        this.biayaPerawatan = biayaPerawatan;
    }

    public BigDecimal hitungBiaya(int jumlahHari) {
        return getTarifHarian().multiply(new BigDecimal(jumlahHari)).add(biayaPerawatan);
    }

    public String getFormulaDescription() {
        return "Tarif Harian x Jumlah Hari + Biaya Perawatan";
    }
}
