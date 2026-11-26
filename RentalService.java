import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalService {

    /** Tarif denda keterlambatan per hari (Rp 10.000) */
    public static final BigDecimal TARIF_DENDA_PER_HARI = new BigDecimal("10000");

    /** Pengelola inventaris barang */
    private DaftarBarang daftarBarang;

    /** Daftar akun pengguna terdaftar */
    private ArrayList<Akun> daftarAkun;

    /** Daftar transaksi yang tercatat dalam sistem */
    private ArrayList<Transaksi> daftarTransaksi;

    /** Penghitung untuk menghasilkan ID transaksi unik */
    private int transaksiCounter;

    public RentalService() {
        this.daftarBarang = new DaftarBarang();
        this.daftarAkun = new ArrayList<>();
        this.daftarTransaksi = new ArrayList<>();
        this.transaksiCounter = 1;

        // Inisialisasi Akun Pengguna Bawaan
        daftarAkun.add(new Admin("admin", "admin123"));
        daftarAkun.add(new Penyewa("penyewa", "penyewa123"));

        boolean berhasilMuat = muatDariFile();
        if (!berhasilMuat) {
            initDataDemo();
            simpanKeFile();
        }
    }

    private void initDataDemo() {
        // Inisialisasi Akun Demo jika belum ada
        if (daftarAkun.isEmpty()) {
            daftarAkun.add(new Admin("admin", "admin123"));
            daftarAkun.add(new Penyewa("penyewa", "penyewa123"));
        }

        // Kategori 1: Paket Snorkeling (Formula: tarif * hari)
        daftarBarang.tambahBarang(new PaketSnorkeling(
                "SNK-001",
                "Paket Masker & Snorkel",
                new BigDecimal("50000"),
                "Masker tempered glass dan snorkel dry top",
                5));
        daftarBarang.tambahBarang(new PaketSnorkeling(
                "SNK-002",
                "Paket Fin Snorkeling",
                new BigDecimal("45000"),
                "Fin snorkeling lentur ukuran all size",
                4));

        // Kategori 2: Alat Pancing (Formula: tarif * hari + biaya perawatan)
        daftarBarang.tambahBarang(new AlatPancing(
                "PNC-001",
                "Joran Pancing Laut",
                new BigDecimal("75000"),
                "Joran carbon kuat khusus mancing laut",
                new BigDecimal("15000"),
                3));
        daftarBarang.tambahBarang(new AlatPancing(
                "PNC-002",
                "Reel Pancing Shimano",
                new BigDecimal("60000"),
                "Reel spinning gear ratio halus tahan air laut",
                new BigDecimal("10000"),
                3));

        // Kategori 3: Perlengkapan Keselamatan (Formula: tarif * hari + biaya sanitasi)
        daftarBarang.tambahBarang(new PerlengkapanKeselamatan(
                "SFT-001",
                "Life Jacket Dewasa",
                new BigDecimal("35000"),
                "Rompi pelampung standar keselamatan dewasa",
                new BigDecimal("8000"),
                8));
        daftarBarang.tambahBarang(new PerlengkapanKeselamatan(
                "SFT-002",
                "Pelampung Anak",
                new BigDecimal("25000"),
                "Rompi pelampung anak dengan peluit darurat",
                new BigDecimal("5000"),
                6));
    }

    public Akun login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        for (Akun akun : daftarAkun) {
            if (akun.getUsername().equalsIgnoreCase(username.trim()) && akun.getPassword().equals(password)) {
                return akun;
            }
        }
        return null;
    }

    public DaftarBarang getDaftarBarang() {
        return daftarBarang;
    }

    public ArrayList<Barang> getBarangTersedia() {
        return daftarBarang.getBarangTersedia();
    }

    public ArrayList<Barang> getSemuaBarang() {
        return daftarBarang.getSemuaBarang();
    }

    public void tambahBarang(Barang b) {
        for (Barang item : daftarBarang.getSemuaBarang()) {
            if (item.getId().equalsIgnoreCase(b.getId())) {
                throw new RentalException("Barang dengan ID '" + b.getId() + "' sudah ada di sistem.");
            }
        }
        daftarBarang.tambahBarang(b);
        simpanKeFile();
    }

    public void editBarang(String id, String namaBaru, BigDecimal tarifBaru, String deskripsi) {
        daftarBarang.editBarang(id, namaBaru, tarifBaru, deskripsi);
        simpanKeFile();
    }

    public void hapusBarang(String id) {
        daftarBarang.hapusBarang(id);
        simpanKeFile();
    }

    public void nonaktifkanBarang(String id) {
        daftarBarang.nonaktifkanBarang(id);
        simpanKeFile();
    }

    public Barang cariBarang(String id) {
        return daftarBarang.cariBarang(id);
    }

    public String buatTransaksi(Penyewa penyewa, ArrayList<Barang> items, int jumlahHari, Pembayaran metode) {
        if (items == null || items.isEmpty()) {
            throw new RentalException("Keranjang kosong. Tidak dapat melakukan transaksi.");
        }
        if (jumlahHari <= 0) {
            throw new RentalException("Jumlah hari sewa harus lebih dari 0.");
        }
        if (metode == null) {
            throw new RentalException("Metode pembayaran harus dipilih.");
        }

        // Periksa ketersediaan barang di inventaris saat pengajuan
        BigDecimal totalBiaya = BigDecimal.ZERO;
        for (Barang b : items) {
            Barang inventaris = daftarBarang.cariBarang(b.getId());
            if (!inventaris.isTersedia() || !inventaris.isAktif() || inventaris.getStok() <= 0) {
                throw new RentalException("Barang '" + inventaris.getNama() + "' (" + inventaris.getId()
                        + ") saat ini stoknya habis atau tidak tersedia.");
            }
            totalBiaya = totalBiaya.add(b.hitungBiaya(jumlahHari));
        }

        String trxId = String.format("TRX-%03d", transaksiCounter++);
        Transaksi trx = new Transaksi(
                trxId,
                penyewa,
                items,
                jumlahHari,
                LocalDate.now(),
                metode,
                totalBiaya);

        daftarTransaksi.add(trx);
        return trxId;
    }

    public void setujuiTransaksi(String id) {
        Transaksi trx = cariTransaksi(id);
        if (!"MENUNGGU_PERSETUJUAN".equals(trx.getStatus())) {
            throw new RentalException("Transaksi dengan status '" + trx.getStatus() + "' tidak dapat disetujui.");
        }

        // Validasi ulang: pastikan seluruh barang masih tersedia di inventaris
        for (Barang b : trx.getDaftarBarang()) {
            Barang inv = daftarBarang.cariBarang(b.getId());
            if (!inv.isTersedia() || inv.getStok() <= 0) {
                throw new RentalException("Gagal menyetujui transaksi: Stok barang '" + inv.getNama() + "' ("
                        + inv.getId() + ") telah habis.");
            }
        }

        // Kurangi unit stok barang di inventaris
        for (Barang b : trx.getDaftarBarang()) {
            Barang inv = daftarBarang.cariBarang(b.getId());
            inv.setStok(inv.getStok() - 1);
        }

        // Setujui transaksi
        trx.setujui();

        // Simpan perubahan status & stok barang ke file text
        simpanKeFile();

        // Simulasi pembayaran
        trx.getMetodePembayaran().prosesPembayaran(trx.getTotalBiaya());
    }

    public void tolakTransaksi(String id) {
        Transaksi trx = cariTransaksi(id);
        trx.tolak();
    }

    public ArrayList<Transaksi> getTransaksiMenunggu() {
        ArrayList<Transaksi> list = new ArrayList<>();
        for (Transaksi t : daftarTransaksi) {
            if ("MENUNGGU_PERSETUJUAN".equals(t.getStatus())) {
                list.add(t);
            }
        }
        return list;
    }

    public ArrayList<Transaksi> getTransaksiAktif() {
        ArrayList<Transaksi> list = new ArrayList<>();
        for (Transaksi t : daftarTransaksi) {
            if ("DISETUJUI".equals(t.getStatus())) {
                list.add(t);
            }
        }
        return list;
    }

    public ArrayList<Transaksi> getRiwayatPenyewa(Penyewa p) {
        ArrayList<Transaksi> list = new ArrayList<>();
        for (Transaksi t : daftarTransaksi) {
            if (t.getPenyewa().getUsername().equalsIgnoreCase(p.getUsername())) {
                if ("DISETUJUI".equals(t.getStatus()) || "SELESAI".equals(t.getStatus())) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    public String konfirmasiPengembalian(String id, LocalDate tanggalKembali) {
        Transaksi trx = cariTransaksi(id);
        if (!"DISETUJUI".equals(trx.getStatus())) {
            throw new RentalException(
                    "Transaksi dengan status '" + trx.getStatus() + "' tidak dapat diselesaikan/dikembalikan.");
        }

        // Selesaikan transaksi dan hitung denda
        trx.selesaikan(tanggalKembali, TARIF_DENDA_PER_HARI);

        // Kembalikan stok seluruh barang transaksi ke inventaris
        for (Barang b : trx.getDaftarBarang()) {
            try {
                Barang inv = daftarBarang.cariBarang(b.getId());
                inv.setStok(inv.getStok() + 1);
            } catch (RentalException ignored) {
                // Jika barang dihapus sebelumnya (meski dilindungi sistem)
            }
        }

        // Simpan perubahan status & stok kembali ke file text
        simpanKeFile();

        return Invoice.generateDenganDenda(trx);
    }

    public Transaksi cariTransaksi(String id) {
        for (Transaksi t : daftarTransaksi) {
            if (t.getId().equalsIgnoreCase(id.trim())) {
                return t;
            }
        }
        throw new RentalException("Transaksi dengan ID '" + id + "' tidak ditemukan.");
    }

    public final File getFileBarang() {
        File dir = new File("src").exists() ? new File(".")
                : (new File("../src").exists() ? new File("..") : new File("."));
        File dataDir = new File(dir, "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        return new File(dataDir, "barang.txt");
    }

    public final void simpanKeFile() {
        File file = getFileBarang();
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("# FORMAT DATA BARANG:");
            writer.println("# Kategori|ID|Nama|TarifHarian|Deskripsi|Stok|Tersedia|Aktif|BiayaTambahan");
            for (Barang b : daftarBarang.getSemuaBarang()) {
                String biayaTambahan = "0";
                if (b instanceof AlatPancing) {
                    biayaTambahan = ((AlatPancing) b).getBiayaPerawatan().toPlainString();
                } else if (b instanceof PerlengkapanKeselamatan) {
                    biayaTambahan = ((PerlengkapanKeselamatan) b).getBiayaSanitasi().toPlainString();
                }

                writer.println(String.join("|",
                        b.getKategori(),
                        b.getId(),
                        b.getNama(),
                        b.getTarifHarian().toPlainString(),
                        b.getDeskripsi(),
                        String.valueOf(b.getStok()),
                        String.valueOf(b.isTersedia()),
                        String.valueOf(b.isAktif()),
                        biayaTambahan));
            }
        } catch (IOException e) {
            System.err.println("[PERINGATAN] Gagal menyimpan data barang ke file: " + e.getMessage());
        }
    }

    public final boolean muatDariFile() {
        File file = getFileBarang();
        if (!file.exists() || file.length() == 0) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            ArrayList<Barang> list = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                // Format: Kategori|ID|Nama|Tarif|Deskripsi|Stok|Tersedia|Aktif|BiayaTambahan
                String[] parts = line.split("\\|");
                if (parts.length < 8)
                    continue;

                String kategori = parts[0].trim();
                String id = parts[1].trim();
                String nama = parts[2].trim();
                BigDecimal tarif = new BigDecimal(parts[3].trim());
                String deskripsi = parts[4].trim();
                int stok = Integer.parseInt(parts[5].trim());
                boolean tersedia = Boolean.parseBoolean(parts[6].trim());
                boolean aktif = Boolean.parseBoolean(parts[7].trim());
                BigDecimal biayaTambahan = parts.length >= 9 ? new BigDecimal(parts[8].trim()) : BigDecimal.ZERO;

                Barang b;
                if ("Paket Snorkeling".equalsIgnoreCase(kategori)) {
                    b = new PaketSnorkeling(id, nama, tarif, deskripsi, stok);
                } else if ("Alat Pancing".equalsIgnoreCase(kategori)) {
                    b = new AlatPancing(id, nama, tarif, deskripsi, biayaTambahan, stok);
                } else if ("Perlengkapan Keselamatan".equalsIgnoreCase(kategori)) {
                    b = new PerlengkapanKeselamatan(id, nama, tarif, deskripsi, biayaTambahan, stok);
                } else {
                    b = new PaketSnorkeling(id, nama, tarif, deskripsi, stok);
                }

                b.setTersedia(tersedia);
                b.setAktif(aktif);
                list.add(b);
            }

            if (!list.isEmpty()) {
                daftarBarang.kosongkan();
                for (Barang b : list) {
                    daftarBarang.tambahBarang(b);
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("[PERINGATAN] Gagal membaca file data barang: " + e.getMessage());
        }
        return false;
    }
}
