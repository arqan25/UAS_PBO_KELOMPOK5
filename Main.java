import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static RentalService service;
    private static Scanner scanner;
    private static DecimalFormat rupiahFormat;

    public static void main(String[] args) {
        service = new RentalService();
        scanner = new Scanner(System.in);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        rupiahFormat = new DecimalFormat("#,###", symbols);

        System.out.println("============================================================");
        System.out.println("   SELAMAT DATANG DI RENTAL SNORKELING & ALAT PANCING       ");
        System.out.println("        Aplikasi CLI UAS Praktikum Pemrograman Berorientasi Objek");
        System.out.println("============================================================");

        boolean berjalan = true;
        while (berjalan) {
            System.out.println("\n================ MENU AUTENTIKASI ================");
            System.out.println("1. Login sebagai Penyewa");
            System.out.println("2. Login sebagai Admin");
            System.out.println("3. Keluar dari Aplikasi");
            System.out.println("==================================================");
            System.out.print("Pilih opsi (1-3): ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    prosesLoginPenyewa();
                    break;
                case "2":
                    prosesLoginAdmin();
                    break;
                case "3":
                    System.out.println("\nTerima kasih telah menggunakan sistem Rental Snorkeling & Alat Pancing.");
                    berjalan = false;
                    break;
                default:
                    System.out.println("[PERINGATAN] Pilihan tidak valid. Silakan pilih 1, 2, atau 3.");
            }
        }

        scanner.close();
    }

    private static void prosesLoginPenyewa() {
        System.out.println("\n--- FORM LOGIN PENYEWA ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Akun akun = service.login(username, password);
        if (akun == null) {
            System.out.println("[GAGAL LOGIN] Username atau password salah! Silakan coba lagi.");
            return;
        }

        if (!(akun instanceof Penyewa)) {
            System.out.println("[GAGAL LOGIN] Akun '" + username
                    + "' adalah akun Admin. Silakan login lewat menu '2. Login sebagai Admin'!");
            return;
        }

        System.out.println("\n[LOGIN BERHASIL] Selamat datang, " + akun.getUsername() + " (Penyewa)!");
        jalankanMenuPenyewa((Penyewa) akun);
    }

    private static void prosesLoginAdmin() {
        System.out.println("\n--- FORM LOGIN ADMIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Akun akun = service.login(username, password);
        if (akun == null) {
            System.out.println("[GAGAL LOGIN] Username atau password salah! Silakan coba lagi.");
            return;
        }

        if (!(akun instanceof Admin)) {
            System.out.println("[GAGAL LOGIN] Akun '" + username
                    + "' adalah akun Penyewa. Silakan login lewat menu '1. Login sebagai Penyewa'!");
            return;
        }

        System.out.println("\n[LOGIN BERHASIL] Selamat datang, " + akun.getUsername() + " (Admin)!");
        jalankanMenuAdmin((Admin) akun);
    }

    private static void jalankanMenuAdmin(Admin admin) {
        boolean tetapLogin = true;
        while (tetapLogin) {
            System.out.println();
            admin.tampilkanMenu();
            System.out.print("Pilih menu (1-8): ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    adminLihatInventaris();
                    break;
                case "2":
                    adminTambahBarang();
                    break;
                case "3":
                    adminEditAtauNonaktifkanBarang();
                    break;
                case "4":
                    adminLihatTransaksiMenunggu();
                    break;
                case "5":
                    adminSetujuiAtauTolakTransaksi();
                    break;
                case "6":
                    adminLihatTransaksiAktif();
                    break;
                case "7":
                    adminKonfirmasiPengembalian();
                    break;
                case "8":
                    System.out.println("[LOGOUT] Anda telah keluar dari sesi Admin.");
                    tetapLogin = false;
                    break;
                default:
                    System.out.println("[PERINGATAN] Opsi menu tidak valid. Silakan pilih 1-8.");
            }
        }
    }

    private static void adminLihatInventaris() {
        ArrayList<Barang> semua = service.getSemuaBarang();
        System.out.println(
                "\n=============================================== DAFTAR INVENTARIS ===============================================");
        if (semua.isEmpty()) {
            System.out.println("Inventaris masih kosong.");
            System.out.println(
                    "==================================================================================================================");
            return;
        }

        String garis = "+---------+------------------------+------------------------+---------------+----------------------+----------+----------+-------+";
        System.out.println(garis);
        System.out.printf("| %-7s | %-22s | %-22s | %-13s | %-20s | %-8s | %-8s | %-5s |\n",
                "ID", "NAMA BARANG", "KATEGORI", "TARIF/HARI", "BIAYA TAMBAHAN", "STOK", "STATUS", "AKTIF");
        System.out.println(garis);
        for (Barang b : semua) {
            String status = (b.isTersedia() && b.getStok() > 0) ? "TERSEDIA" : "HABIS";
            String aktif = b.isAktif() ? "Ya" : "Tidak";
            String stokStr = b.getStok() + " unit";
            System.out.printf("| %-7s | %-22s | %-22s | Rp %-10s | %-20s | %-8s | %-8s | %-5s |\n",
                    b.getId(),
                    potongTeks(b.getNama(), 22),
                    potongTeks(b.getKategori(), 22),
                    rupiahFormat.format(b.getTarifHarian()),
                    getBiayaTambahanStr(b),
                    stokStr,
                    status,
                    aktif);
        }
        System.out.println(garis);
    }

    /**
     * Memandu Admin untuk menambahkan barang baru ke inventaris.
     */
    private static void adminTambahBarang() {
        System.out.println("\n--- TAMBAH BARANG INVENTARIS ---");
        System.out.println("Pilih Kategori Barang:");
        System.out.println("1. Paket Snorkeling (Formula: Tarif Harian x Hari)");
        System.out.println("2. Alat Pancing (Formula: Tarif Harian x Hari + Biaya Perawatan)");
        System.out.println("3. Perlengkapan Keselamatan (Formula: Tarif Harian x Hari + Biaya Sanitasi)");
        System.out.print("Pilih kategori (1-3): ");
        String katPilihan = scanner.nextLine().trim();

        if (!katPilihan.equals("1") && !katPilihan.equals("2") && !katPilihan.equals("3")) {
            System.out.println("[BATAL] Kategori tidak valid.");
            return;
        }

        try {
            System.out.print("ID Barang (unik, misal SNK-003 / PNC-003): ");
            String id = scanner.nextLine().trim();
            if (id.isEmpty()) {
                System.out.println("[BATAL] ID tidak boleh kosong.");
                return;
            }

            System.out.print("Nama Barang: ");
            String nama = scanner.nextLine().trim();
            if (nama.isEmpty()) {
                System.out.println("[BATAL] Nama tidak boleh kosong.");
                return;
            }

            System.out.print("Tarif Harian (angka): Rp ");
            String tarifInput = scanner.nextLine().trim();
            BigDecimal tarifHarian = parseUang(tarifInput);

            System.out.print("Stok Awal (jumlah unit): ");
            int stok = Integer.parseInt(scanner.nextLine().trim().replace(".", ""));

            System.out.print("Deskripsi Singkat: ");
            String deskripsi = scanner.nextLine().trim();

            Barang barangBaru;
            if (katPilihan.equals("1")) {
                barangBaru = new PaketSnorkeling(id, nama, tarifHarian, deskripsi, stok);
            } else if (katPilihan.equals("2")) {
                System.out.print("Biaya Perawatan Tetap: Rp ");
                BigDecimal biayaPerawatan = parseUang(scanner.nextLine().trim());
                barangBaru = new AlatPancing(id, nama, tarifHarian, deskripsi, biayaPerawatan, stok);
            } else {
                System.out.print("Biaya Sanitasi Tetap: Rp ");
                BigDecimal biayaSanitasi = parseUang(scanner.nextLine().trim());
                barangBaru = new PerlengkapanKeselamatan(id, nama, tarifHarian, deskripsi, biayaSanitasi, stok);
            }

            service.tambahBarang(barangBaru);
            System.out.println("[SUKSES] Barang '" + nama + "' (" + id + ") dengan stok " + stok
                    + " unit berhasil ditambahkan ke inventaris!");
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL] Input angka atau tarif tidak valid: " + e.getMessage());
        } catch (RentalException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    /**
     * Memandu Admin untuk mengedit, menonaktifkan, atau menghapus barang
     * inventaris.
     */
    private static void adminEditAtauNonaktifkanBarang() {
        System.out.println("\n--- KELOLA / EDIT BARANG ---");
        System.out.println("1. Edit Data Barang (Nama, Tarif, Deskripsi)");
        System.out.println("2. Nonaktifkan Barang (Tidak akan muncul di katalog sewa)");
        System.out.println("3. Hapus Barang dari Sistem");
        System.out.println("4. Kembali");
        System.out.print("Pilih aksi (1-4): ");
        String aksi = scanner.nextLine().trim();

        if (aksi.equals("4"))
            return;

        System.out.print("Masukkan ID Barang: ");
        String id = scanner.nextLine().trim();

        try {
            Barang b = service.cariBarang(id);
            System.out.println("Barang ditemukan: " + b.getNama() + " (" + b.getKategori() + ")");

            if (aksi.equals("1")) {
                System.out.print("Nama Baru (kosongkan jika tidak diubah): ");
                String namaBaru = scanner.nextLine().trim();
                if (namaBaru.isEmpty())
                    namaBaru = null;

                System.out.print("Tarif Harian Baru (kosongkan jika tidak diubah): Rp ");
                String tarifStr = scanner.nextLine().trim();
                BigDecimal tarifBaru = tarifStr.isEmpty() ? null : parseUang(tarifStr);

                System.out.print("Deskripsi Baru (kosongkan jika tidak diubah): ");
                String deskripsiBaru = scanner.nextLine().trim();
                if (deskripsiBaru.isEmpty())
                    deskripsiBaru = null;

                service.editBarang(id, namaBaru, tarifBaru, deskripsiBaru);
                System.out.println("[SUKSES] Informasi barang ID '" + id + "' berhasil diperbarui!");
            } else if (aksi.equals("2")) {
                service.nonaktifkanBarang(id);
                System.out.println("[SUKSES] Barang ID '" + id + "' berhasil dinonaktifkan!");
            } else if (aksi.equals("3")) {
                System.out.print("Apakah Anda yakin ingin menghapus barang ini? (y/n): ");
                String konfirmasi = scanner.nextLine().trim();
                if (konfirmasi.equalsIgnoreCase("y")) {
                    service.hapusBarang(id);
                    System.out.println("[SUKSES] Barang ID '" + id + "' berhasil dihapus dari sistem!");
                } else {
                    System.out.println("[BATAL] Penghapusan dibatalkan.");
                }
            } else {
                System.out.println("[PERINGATAN] Aksi tidak dikenali.");
            }
        } catch (RentalException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL] Format angka tidak valid.");
        }
    }

    /**
     * Menampilkan daftar transaksi yang sedang menunggu persetujuan Admin.
     */
    private static void adminLihatTransaksiMenunggu() {
        ArrayList<Transaksi> list = service.getTransaksiMenunggu();
        System.out.println("\n================ TRANSAKSI MENUNGGU PERSETUJUAN ================");
        if (list.isEmpty()) {
            System.out.println("Tidak ada transaksi yang menunggu persetujuan.");
            return;
        }

        for (Transaksi t : list) {
            System.out.println("----------------------------------------------------------------");
            System.out.println("ID Transaksi    : " + t.getId());
            System.out.println("Penyewa         : " + t.getPenyewa().getUsername());
            System.out.println("Tanggal Pengajuan: " + t.getTanggalTransaksi());
            System.out.println("Durasi Sewa     : " + t.getJumlahHari() + " hari");
            System.out.println("Metode Bayar    : " + t.getMetodePembayaran().getNamaPembayaran());
            System.out.println("Daftar Barang   :");
            for (Barang b : t.getDaftarBarang()) {
                System.out.println("  - [" + b.getId() + "] " + b.getNama() + " (" + b.getKategori() + ")");
            }
            System.out.println("Total Estimasi  : Rp " + rupiahFormat.format(t.getTotalBiaya()));
        }
        System.out.println("================================================================");
    }

    /**
     * Menyetujui atau menolak transaksi yang menunggu persetujuan.
     */
    private static void adminSetujuiAtauTolakTransaksi() {
        adminLihatTransaksiMenunggu();
        ArrayList<Transaksi> list = service.getTransaksiMenunggu();
        if (list.isEmpty())
            return;

        System.out.print("\nMasukkan ID Transaksi yang ingin diproses: ");
        String id = scanner.nextLine().trim();

        try {
            Transaksi t = service.cariTransaksi(id);
            if (!t.getStatus().equals("MENUNGGU_PERSETUJUAN")) {
                System.out.println("[GAGAL] Status transaksi ini adalah: " + t.getStatus());
                return;
            }

            System.out.print("Pilih tindakan: [A] Setujui / [T] Tolak: ");
            String aksi = scanner.nextLine().trim().toUpperCase();

            if (aksi.equals("A")) {
                service.setujuiTransaksi(id);
                System.out.println("\n[SUKSES] Transaksi " + id + " BERHASIL DISETUJUI!");
                System.out.println("Status seluruh barang dalam transaksi kini berubah menjadi 'DISEWA'.");
                System.out.println("\nBerikut Invoice Transaksi yang diterbitkan:");
                System.out.println(Invoice.generate(t));
            } else if (aksi.equals("T")) {
                service.tolakTransaksi(id);
                System.out.println("[SUKSES] Transaksi " + id + " DITOLAK. Barang tetap tersedia di inventaris.");
            } else {
                System.out.println("[BATAL] Tindakan dibatalkan (masukkan A atau T).");
            }
        } catch (RentalException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    /**
     * Menampilkan daftar transaksi yang sedang aktif (barang sedang disewa).
     */
    private static void adminLihatTransaksiAktif() {
        ArrayList<Transaksi> aktif = service.getTransaksiAktif();
        System.out.println("\n======================== TRANSAKSI AKTIF (DISEWA) ========================");
        if (aktif.isEmpty()) {
            System.out.println("Saat ini tidak ada transaksi yang sedang aktif disewa.");
            return;
        }

        System.out.printf("%-9s | %-12s | %-12s | %-12s | %-8s | %-12s\n",
                "ID", "PENYEWA", "TGL MULAI", "JATUH TEMPO", "DURASI", "TOTAL SEWA");
        System.out.println("---------------------------------------------------------------------------------");
        for (Transaksi t : aktif) {
            System.out.printf("%-9s | %-12s | %-12s | %-12s | %-4d hari | Rp %-10s\n",
                    t.getId(),
                    t.getPenyewa().getUsername(),
                    t.getTanggalMulai(),
                    t.getTanggalJatuhTempo(),
                    t.getJumlahHari(),
                    rupiahFormat.format(t.getTotalBiaya()));
            System.out.print("  Barang disewa: ");
            for (Barang b : t.getDaftarBarang()) {
                System.out.print("[" + b.getId() + "] " + b.getNama() + "; ");
            }
            System.out.println();
        }
        System.out.println("=================================================================================");
    }

    /**
     * Mengonfirmasi pengembalian barang sewa dan menghitung denda keterlambatan
     * jika ada.
     */
    private static void adminKonfirmasiPengembalian() {
        adminLihatTransaksiAktif();
        ArrayList<Transaksi> aktif = service.getTransaksiAktif();
        if (aktif.isEmpty())
            return;

        System.out.print("\nMasukkan ID Transaksi yang barangnya dikembalikan: ");
        String id = scanner.nextLine().trim();

        try {
            Transaksi t = service.cariTransaksi(id);
            if (!t.getStatus().equals("DISETUJUI")) {
                System.out.println("[GAGAL] Transaksi tidak berstatus DISETUJUI.");
                return;
            }

            System.out.println("Tanggal Jatuh Tempo : " + t.getTanggalJatuhTempo());
            System.out.println("Tarif Denda Harian  : Rp " + rupiahFormat.format(RentalService.TARIF_DENDA_PER_HARI)
                    + " / hari terlambat");
            System.out.print("Masukkan Tanggal Pengembalian Aktual (Format: YYYY-MM-DD, kosongkan untuk hari ini ["
                    + LocalDate.now() + "]): ");
            String tglInput = scanner.nextLine().trim();

            LocalDate tanggalKembali;
            if (tglInput.isEmpty()) {
                tanggalKembali = LocalDate.now();
            } else {
                tanggalKembali = LocalDate.parse(tglInput);
            }

            String invoiceHasil = service.konfirmasiPengembalian(id, tanggalKembali);
            System.out.println("\n[SUKSES] Pengembalian transaksi " + id + " telah dikonfirmasi!");
            System.out.println("Status barang telah dikembalikan menjadi 'TERSEDIA'.");
            System.out.println("\nBerikut Invoice Pengembalian Akhir:");
            System.out.println(invoiceHasil);

        } catch (DateTimeParseException e) {
            System.out.println("[GAGAL] Format tanggal salah! Gunakan format YYYY-MM-DD (contoh: 2026-11-25).");
        } catch (RentalException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    // =========================================================================
    // MENU PENYEWA
    // =========================================================================

    /**
     * Menjalankan navigasi menu untuk peran Penyewa.
     *
     * @param penyewa objek akun Penyewa yang sedang login
     */
    private static void jalankanMenuPenyewa(Penyewa penyewa) {
        boolean tetapLogin = true;
        while (tetapLogin) {
            System.out.println();
            penyewa.tampilkanMenu();
            System.out.print("Pilih menu (1-6): ");
            String pilihan = scanner.nextLine().trim();

            switch (pilihan) {
                case "1":
                    penyewaLihatBarangTersedia();
                    break;
                case "2":
                    penyewaTambahKeKeranjang(penyewa);
                    break;
                case "3":
                    penyewaLihatAtauHapusKeranjang(penyewa);
                    break;
                case "4":
                    penyewaCheckout(penyewa);
                    break;
                case "5":
                    penyewaLihatRiwayat(penyewa);
                    break;
                case "6":
                    System.out.println("[LOGOUT] Anda telah keluar dari sesi Penyewa.");
                    tetapLogin = false;
                    break;
                default:
                    System.out.println("[PERINGATAN] Opsi menu tidak valid. Silakan pilih 1-6.");
            }
        }
    }

    /**
     * Menampilkan katalog barang yang sedang tersedia dan aktif untuk disewa dalam
     * format tabel rapi.
     */
    private static void penyewaLihatBarangTersedia() {
        ArrayList<Barang> tersedia = service.getBarangTersedia();
        System.out.println(
                "\n============================================ KATALOG BARANG TERSEDIA ============================================");
        if (tersedia.isEmpty()) {
            System.out.println("Saat ini tidak ada barang yang tersedia untuk disewa.");
            System.out.println(
                    "==================================================================================================================");
            return;
        }

        String garis = "+---------+------------------------+------------------------+---------------+----------------------+----------+---------------------------------+";
        System.out.println(garis);
        System.out.printf("| %-7s | %-22s | %-22s | %-13s | %-20s | %-8s | %-31s |\n",
                "ID", "NAMA BARANG", "KATEGORI", "TARIF/HARI", "BIAYA TAMBAHAN", "STOK", "FORMULA HITUNG");
        System.out.println(garis);
        for (Barang b : tersedia) {
            String stokStr = b.getStok() + " unit";
            System.out.printf("| %-7s | %-22s | %-22s | Rp %-10s | %-20s | %-8s | %-31s |\n",
                    b.getId(),
                    potongTeks(b.getNama(), 22),
                    potongTeks(b.getKategori(), 22),
                    rupiahFormat.format(b.getTarifHarian()),
                    getBiayaTambahanStr(b),
                    stokStr,
                    potongTeks(b.getFormulaDescription(), 31));
        }
        System.out.println(garis);
    }

    /**
     * Memandu Penyewa untuk menambahkan barang ke dalam keranjang belanja sewa.
     *
     * @param penyewa akun penyewa aktif
     */
    private static void penyewaTambahKeKeranjang(Penyewa penyewa) {
        penyewaLihatBarangTersedia();
        System.out.print("\nMasukkan ID Barang yang ingin disewa: ");
        String id = scanner.nextLine().trim();

        try {
            Barang b = service.cariBarang(id);
            penyewa.getKeranjang().tambahBarang(b);
            System.out.println(
                    "[SUKSES] Barang '" + b.getNama() + "' (" + b.getId() + ") berhasil ditambahkan ke keranjang!");
            System.out.println("Jumlah barang di keranjang saat ini: " + penyewa.getKeranjang().getItems().size());
        } catch (RentalException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    /**
     * Menampilkan isi keranjang belanja sewa milik penyewa serta menyediakan opsi
     * penghapusan.
     *
     * @param penyewa akun penyewa aktif
     */
    private static void penyewaLihatAtauHapusKeranjang(Penyewa penyewa) {
        KeranjangSewa keranjang = penyewa.getKeranjang();
        ArrayList<Barang> items = keranjang.getItems();

        System.out.println(
                "\n============================================= KERANJANG SEWA ANDA =============================================");
        if (items.isEmpty()) {
            System.out.println("Keranjang sewa Anda saat ini masih kosong.");
            System.out.println(
                    "================================================================================================================");
            return;
        }

        String garis = "+-----+---------+------------------------+------------------------+---------------+----------------------+";
        System.out.println(garis);
        System.out.printf("| %-3s | %-7s | %-22s | %-22s | %-13s | %-20s |\n",
                "NO", "ID", "NAMA BARANG", "KATEGORI", "TARIF/HARI", "BIAYA TAMBAHAN");
        System.out.println(garis);
        int no = 1;
        for (Barang b : items) {
            System.out.printf("| %-3d | %-7s | %-22s | %-22s | Rp %-10s | %-20s |\n",
                    no++,
                    b.getId(),
                    potongTeks(b.getNama(), 22),
                    potongTeks(b.getKategori(), 22),
                    rupiahFormat.format(b.getTarifHarian()),
                    getBiayaTambahanStr(b));
        }
        System.out.println(garis);

        System.out.println("Pilihan:");
        System.out.println("1. Hapus salah satu barang dari keranjang");
        System.out.println("2. Kosongkan seluruh keranjang");
        System.out.println("3. Kembali ke Menu Penyewa");
        System.out.print("Pilih opsi (1-3): ");
        String opsi = scanner.nextLine().trim();

        if (opsi.equals("1")) {
            System.out.print("Masukkan ID barang yang akan dihapus: ");
            String idHapus = scanner.nextLine().trim();
            try {
                keranjang.hapusBarang(idHapus);
                System.out.println("[SUKSES] Barang ID '" + idHapus + "' berhasil dihapus dari keranjang.");
            } catch (RentalException e) {
                System.out.println("[GAGAL] " + e.getMessage());
            }
        } else if (opsi.equals("2")) {
            keranjang.kosongkan();
            System.out.println("[SUKSES] Keranjang sewa telah dikosongkan.");
        }
    }

    /**
     * Memandu alur checkout: input durasi sewa, kalkulasi biaya polimorfik,
     * pemilihan metode pembayaran, konfirmasi, dan pengajuan transaksi ke Admin.
     *
     * @param penyewa akun penyewa aktif
     */
    private static void penyewaCheckout(Penyewa penyewa) {
        KeranjangSewa keranjang = penyewa.getKeranjang();
        if (keranjang.isEmpty()) {
            System.out.println("\n[GAGAL] Keranjang sewa Anda masih kosong. Silakan pilih barang terlebih dahulu.");
            return;
        }

        System.out.println("\n============================= CHECKOUT =============================");
        System.out.print("Masukkan lama sewa (dalam hari, misal 2 atau 3): ");
        String durasiStr = scanner.nextLine().trim();

        int durasi;
        try {
            durasi = Integer.parseInt(durasiStr);
            if (durasi <= 0) {
                System.out.println("[GAGAL] Durasi sewa harus lebih dari 0 hari.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[GAGAL] Durasi sewa harus berupa bilangan bulat positif.");
            return;
        }

        // Tampilkan rincian perhitungan biaya polimorfik
        System.out.println("\n--- RINCIAN PERHITUNGAN BIAYA SEWA (" + durasi + " HARI) ---");
        BigDecimal totalKalkulasi = BigDecimal.ZERO;
        int no = 1;
        for (Barang b : keranjang.getItems()) {
            BigDecimal subtotal = b.hitungBiaya(durasi);
            totalKalkulasi = totalKalkulasi.add(subtotal);
            System.out.printf("%d. %s [%s]\n", no++, b.getNama(), b.getKategori());
            System.out.printf("   Formula : %s\n", b.getFormulaDescription());
            System.out.printf("   Subtotal: Rp %s\n", rupiahFormat.format(subtotal));
        }
        System.out.println("--------------------------------------------------------------------");
        System.out.println("TOTAL ESTIMASI BIAYA: Rp " + rupiahFormat.format(totalKalkulasi));
        System.out.println("--------------------------------------------------------------------");

        // Pemilihan metode pembayaran (Polymorphism)
        System.out.println("\nPilih Metode Pembayaran:");
        System.out.println("1. Transfer Bank");
        System.out.println("2. QRIS");
        System.out.println("3. Tunai (Cash)");
        System.out.print("Pilih metode (1-3): ");
        String metStr = scanner.nextLine().trim();

        Pembayaran metode;
        switch (metStr) {
            case "1":
                metode = new TransferBank();
                break;
            case "2":
                metode = new QRIS();
                break;
            case "3":
                metode = new Tunai();
                break;
            default:
                System.out.println("[BATAL] Metode pembayaran tidak valid. Checkout dibatalkan.");
                return;
        }

        System.out.print("\nApakah Anda yakin ingin mengajukan transaksi ini? (y/n): ");
        String konfirm = scanner.nextLine().trim();
        if (!konfirm.equalsIgnoreCase("y")) {
            System.out.println("[BATAL] Transaksi dibatalkan oleh pengguna.");
            return;
        }

        try {
            String trxId = service.buatTransaksi(penyewa, keranjang.getItems(), durasi, metode);
            // Kosongkan keranjang sewa setelah pengajuan berhasil
            keranjang.kosongkan();

            System.out.println("\n====================================================================");
            System.out.println("[BERHASIL] Transaksi " + trxId + " berhasil diajukan!");
            System.out.println("Status saat ini : MENUNGGU_PERSETUJUAN");
            System.out.println("Metode Pembayaran: " + metode.getNamaPembayaran());
            System.out.println("Silakan menunggu Admin menyetujui transaksi Anda.");
            System.out.println("====================================================================");
        } catch (RentalException e) {
            System.out.println("[GAGAL] " + e.getMessage());
        }
    }

    /**
     * Menampilkan riwayat transaksi milik Penyewa (hanya transaksi yang sudah
     * disetujui / selesai).
     * Serta memberikan opsi untuk mencetak invoice teks lengkap.
     *
     * @param penyewa akun penyewa aktif
     */
    private static void penyewaLihatRiwayat(Penyewa penyewa) {
        ArrayList<Transaksi> riwayat = service.getRiwayatPenyewa(penyewa);
        System.out.println("\n======================== RIWAYAT TRANSAKSI ANDA ========================");
        if (riwayat.isEmpty()) {
            System.out.println("Belum ada riwayat transaksi yang disetujui.");
            System.out.println("(Catatan: Transaksi yang masih MENUNGGU_PERSETUJUAN belum tampil di riwayat aktif)");
            System.out.println("========================================================================");
            return;
        }

        for (Transaksi t : riwayat) {
            System.out.printf("ID: %-8s | Tgl: %s | Durasi: %d hari | Status: %-8s | Total: Rp %s\n",
                    t.getId(),
                    t.getTanggalTransaksi(),
                    t.getJumlahHari(),
                    t.getStatus(),
                    rupiahFormat.format(t.getTotalBiaya()));
            if (t.getDenda().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("    -> Denda Keterlambatan: Rp " + rupiahFormat.format(t.getDenda()));
            }
        }
        System.out.println("========================================================================");

        System.out.print("Masukkan ID Transaksi untuk melihat rincian Invoice (atau tekan Enter untuk kembali): ");
        String idCari = scanner.nextLine().trim();
        if (!idCari.isEmpty()) {
            try {
                Transaksi t = service.cariTransaksi(idCari);
                // Validasi bahwa transaksi ini benar milik penyewa yang sedang login
                if (!t.getPenyewa().getUsername().equalsIgnoreCase(penyewa.getUsername())) {
                    System.out.println("[AKSES DITOLAK] Anda tidak berhak melihat transaksi penyewa lain.");
                    return;
                }
                if (t.getStatus().equals("SELESAI")) {
                    System.out.println(Invoice.generateDenganDenda(t));
                } else {
                    System.out.println(Invoice.generate(t));
                }
            } catch (RentalException e) {
                System.out.println("[GAGAL] " + e.getMessage());
            }
        }
    }

    /**
     * Memotong string jika panjangnya melebihi batas kolom untuk kerapian tabel
     * CLI.
     *
     * @param teks  teks input
     * @param batas panjang maksimal
     * @return string yang dipotong atau asli
     */
    private static String potongTeks(String teks, int batas) {
        if (teks == null)
            return "";
        if (teks.length() > batas) {
            return teks.substring(0, batas - 3) + "...";
        }
        return teks;
    }

    /**
     * Mendapatkan rincian string biaya tambahan sesuai kategori barang.
     *
     * @param b objek barang
     * @return string biaya tambahan
     */
    private static String getBiayaTambahanStr(Barang b) {
        if (b instanceof AlatPancing) {
            return "Perawatan: Rp " + rupiahFormat.format(((AlatPancing) b).getBiayaPerawatan());
        } else if (b instanceof PerlengkapanKeselamatan) {
            return "Sanitasi: Rp " + rupiahFormat.format(((PerlengkapanKeselamatan) b).getBiayaSanitasi());
        } else {
            return "-";
        }
    }

    /**
     * Mem-parsing string input uang dari pengguna menjadi BigDecimal.
     * Mendukung format penulisan Indonesia dengan titik pemisah ribuan (misal:
     * "35.000"),
     * format polos ("35000"), maupun dengan awalan "Rp" atau "Rp.".
     *
     * @param input string input dari pengguna
     * @return objek BigDecimal yang sesuai
     * @throws NumberFormatException jika format angka tidak valid
     */
    private static BigDecimal parseUang(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Input nominal uang kosong.");
        }
        // Hapus "Rp", "rp", titik setelah Rp, dan spasi
        String clean = input.trim().replaceAll("(?i)rp\\.?", "").trim();
        // Hapus titik pemisah ribuan (contoh: "35.000" -> "35000")
        clean = clean.replace(".", "");
        // Ganti koma dengan titik jika pengguna memasukkan desimal (misal "35000,50")
        clean = clean.replace(',', '.');
        return new BigDecimal(clean);
    }
}