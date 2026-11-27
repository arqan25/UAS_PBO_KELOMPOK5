import java.math.BigDecimal;

public abstract class Barang {
    private String id;
    private String nama;
    private String kategori;
    private BigDecimal tarifHarian;
    private String deskripsi;
    private boolean tersedia;
    private boolean aktif;
    private int stok;

    public Barang(String id, String nama, String kategori, BigDecimal tarifHarian, String deskripsi) {
        this(id, nama, kategori, tarifHarian, deskripsi, 1);
    }

    public Barang(String id, String nama, String kategori, BigDecimal tarifHarian, String deskripsi, int stok) {
        if (tarifHarian.compareTo(BigDecimal.ZERO) < 0) {
            throw new RentalException("Tarif harian tidak boleh negatif.");
        }
        if (stok < 0) {
            throw new RentalException("Stok barang tidak boleh negatif.");
        }
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.tarifHarian = tarifHarian;
        this.deskripsi = deskripsi;
        this.stok = stok;
        this.tersedia = (stok > 0);
        this.aktif = true;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new RentalException("Stok barang tidak boleh negatif.");
        }
        this.stok = stok;
        this.tersedia = (stok > 0);
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public BigDecimal getTarifHarian() {
        return tarifHarian;
    }

    public void setTarifHarian(BigDecimal tarifHarian) {
        if (tarifHarian.compareTo(BigDecimal.ZERO) < 0) {
            throw new RentalException("Tarif harian tidak boleh negatif.");
        }
        this.tarifHarian = tarifHarian;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public boolean isAktif() {
        return aktif;
    }

    public void setAktif(boolean aktif) {
        this.aktif = aktif;
    }

    public abstract BigDecimal hitungBiaya(int jumlahHari);

    public abstract String getFormulaDescription();
}
