import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Transaksi {

    /** ID unik transaksi */
    private String id;

    /** Penyewa yang melakukan transaksi */
    private Penyewa penyewa;

    /** Snapshot daftar barang yang disewa */
    private ArrayList<Barang> daftarBarang;

    /** Jumlah hari sewa */
    private int jumlahHari;

    /** Tanggal transaksi dibuat */
    private LocalDate tanggalTransaksi;

    /** Tanggal mulai sewa */
    private LocalDate tanggalMulai;

    /** Tanggal jatuh tempo pengembalian */
    private LocalDate tanggalJatuhTempo;

    /** Tanggal pengembalian aktual (nullable, null jika belum dikembalikan) */
    private LocalDate tanggalKembali;

    /** Metode pembayaran yang digunakan */
    private Pembayaran metodePembayaran;

    /** Status transaksi (MENUNGGU_PERSETUJUAN, DISETUJUI, DITOLAK, SELESAI) */
    private String status;

    /** Total biaya sewa */
    private BigDecimal totalBiaya;

    /** Denda keterlambatan (default 0) */
    private BigDecimal denda;

    public Transaksi(String id, Penyewa penyewa, ArrayList<Barang> daftarBarang, int jumlahHari,
            LocalDate tanggalMulai, Pembayaran metodePembayaran, BigDecimal totalBiaya) {
        this.id = id;
        this.penyewa = penyewa;
        this.daftarBarang = new ArrayList<>(daftarBarang);
        this.jumlahHari = jumlahHari;
        this.tanggalTransaksi = LocalDate.now();
        this.tanggalMulai = tanggalMulai;
        this.tanggalJatuhTempo = tanggalMulai.plusDays(jumlahHari);
        this.tanggalKembali = null;
        this.metodePembayaran = metodePembayaran;
        this.status = "MENUNGGU_PERSETUJUAN";
        this.totalBiaya = totalBiaya;
        this.denda = BigDecimal.ZERO;
    }

    public String getId() {
        return id;
    }

    public Penyewa getPenyewa() {
        return penyewa;
    }

    public ArrayList<Barang> getDaftarBarang() {
        return new ArrayList<>(daftarBarang);
    }

    public int getJumlahHari() {
        return jumlahHari;
    }

    public LocalDate getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public LocalDate getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public Pembayaran getMetodePembayaran() {
        return metodePembayaran;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(BigDecimal totalBiaya) {
        this.totalBiaya = totalBiaya;
    }

    public BigDecimal getDenda() {
        return denda;
    }

    public void setDenda(BigDecimal denda) {
        this.denda = denda;
    }

    public void setujui() {
        if (!status.equals("MENUNGGU_PERSETUJUAN")) {
            throw new RentalException("Transaksi tidak dapat disetujui. Status saat ini: " + status);
        }
        this.status = "DISETUJUI";
    }

    public void tolak() {
        if (!status.equals("MENUNGGU_PERSETUJUAN")) {
            throw new RentalException("Transaksi tidak dapat ditolak. Status saat ini: " + status);
        }
        this.status = "DITOLAK";
    }

    public void selesaikan(LocalDate tanggalKembali, BigDecimal tarifDenda) {
        if (!status.equals("DISETUJUI")) {
            throw new RentalException("Transaksi tidak dapat diselesaikan. Status saat ini: " + status);
        }
        this.tanggalKembali = tanggalKembali;
        this.status = "SELESAI";

        // Hitung denda jika terlambat
        long hariTerlambat = ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalKembali);
        if (hariTerlambat > 0) {
            this.denda = tarifDenda.multiply(new BigDecimal(hariTerlambat));
        } else {
            this.denda = BigDecimal.ZERO;
        }
    }
}
