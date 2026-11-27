import java.math.BigDecimal;

public class PerlengkapanKeselamatan extends Barang {
    private BigDecimal biayaSanitasi;

    public PerlengkapanKeselamatan(String id, String nama, BigDecimal tarifHarian, String deskripsi,
            BigDecimal biayaSanitasi) {
        super(id, nama, "Perlengkapan Keselamatan", tarifHarian, deskripsi);
        this.biayaSanitasi = biayaSanitasi;
    }

    public PerlengkapanKeselamatan(String id, String nama, BigDecimal tarifHarian, String deskripsi,
            BigDecimal biayaSanitasi, int stok) {
        super(id, nama, "Perlengkapan Keselamatan", tarifHarian, deskripsi, stok);
        this.biayaSanitasi = biayaSanitasi;
    }

    public BigDecimal getBiayaSanitasi() {
        return biayaSanitasi;
    }

    public void setBiayaSanitasi(BigDecimal biayaSanitasi) {
        this.biayaSanitasi = biayaSanitasi;
    }

    public BigDecimal hitungBiaya(int jumlahHari) {
        return getTarifHarian().multiply(new BigDecimal(jumlahHari)).add(biayaSanitasi);
    }

    public String getFormulaDescription() {
        return "Tarif Harian x Jumlah Hari + Biaya Sanitasi";
    }
}
