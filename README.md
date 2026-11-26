# UAS_PBO_KELOMPOK5
# Rental Snorkeling & Alat Pancing

Aplikasi Command Line Interface (CLI) berbasis Java untuk mengelola penyewaan perlengkapan snorkeling dan alat memancing. Proyek ini dibangun sebagai tugas UAS Praktikum Pemrograman Berorientasi Objek (PBO).

---

## Anggota Kelompok
1. **[ARQAN SYAPUTRA]** - `[250810701100057]`
2. **[Asy Syifa Zahra]** - `[250810701100027]`
3. **[MISYQA AISARA]** - `[250810701100028]`

---

## Deskripsi Singkat
Aplikasi ini memfasilitasi siklus transaksi rental menyeluruh:
- **Penyewa**: Mengakses katalog perlengkapan, menambahkan berbagai barang ke keranjang sewa, menentukan durasi hari sewa, memilih metode pembayaran (simulasi), mengajukan transaksi, serta memantau riwayat transaksi dan invoice resmi.
- **Admin**: Mengelola inventaris (tambah, edit, nonaktifkan, hapus), memverifikasi ketersediaan stok, menyetujui atau menolak transaksi sewa, serta mengonfirmasi pengembalian barang dan menghitung denda keterlambatan secara otomatis.

---

## Konsep PBO yang Diterapkan

1. **Abstraction**
   - Kelas abstrak `Akun` mendefinisikan kontrak akun pengguna dan metode abstrak `tampilkanMenu()`.
   - Kelas abstrak `Barang` mendefinisikan struktur barang inventaris dan metode abstrak `hitungBiaya(int jumlahHari)` serta `getFormulaDescription()`.
   - Interface `Pembayaran` mendefinisikan kontrak metode pembayaran simulasi (`getNamaPembayaran()` dan `prosesPembayaran(BigDecimal)`).

2. **Inheritance**
   - `Admin` dan `Penyewa` mewarisi kelas `Akun`.
   - `PaketSnorkeling`, `AlatPancing`, dan `PerlengkapanKeselamatan` mewarisi kelas `Barang`.
   - `TransferBank`, `QRIS`, dan `Tunai` mengimplementasikan interface `Pembayaran`.

3. **Polymorphism**
   - Perhitungan biaya sewa dihitung polimorfik melalui method `hitungBiaya()` yang di-override di setiap kategori barang:
     - **Paket Snorkeling**: `Tarif Harian × Jumlah Hari`
     - **Alat Pancing**: `(Tarif Harian × Jumlah Hari) + Biaya Perawatan`
     - **Perlengkapan Keselamatan**: `(Tarif Harian × Jumlah Hari) + Biaya Sanitasi`
   - Pemrosesan pembayaran dipanggil secara dinamis melalui interface `Pembayaran`.

4. **Encapsulation**
   - Seluruh atribut/field objek dibuat `private`.
   - Pengubahan dan pembacaan nilai dilakukan via getter dan setter yang dilengkapi validasi (misalnya: tarif tidak boleh negatif, ID unik, status barang).

5. **Collection**
   - Menggunakan `java.util.ArrayList` untuk mengelola inventaris (`DaftarBarang`), daftar isi keranjang (`KeranjangSewa`), daftar akun, dan daftar riwayat transaksi (`Transaksi`).

6. **Exception Handling**
   - Custom exception `RentalException` digunakan untuk menangani kondisi kesalahan logika bisnis (barang sedang disewa, ID duplikat, barang tidak ditemukan, keranjang kosong).
   - Seluruh input CLI diproteksi menggunakan blok `try-catch` sehingga aplikasi tidak pernah mengalami crash akibat kesalahan input pengguna.

7. **Ketelitian Keuangan & Tanggal**
   - Menggunakan `java.math.BigDecimal` untuk seluruh operasi moneter guna menghindari kehilangan presisi floating-point.
   - Menggunakan `java.time.LocalDate` dan `ChronoUnit` untuk perhitungan tanggal jatuh tempo, tanggal pengembalian, dan hari keterlambatan.

8. **Dokumentasi Javadoc**
   - Seluruh kelas dan method penting dilengkapi komentar standar Javadoc (`@param`, `@return`, `@throws`).

9. **File I/O & Persistensi Data (.txt)**
   - Menggunakan file teks [**`data/barang.txt`**](file:///d:/uas%20pbo/data/barang.txt) untuk menyimpan data inventaris secara permanen sehingga data barang (tambah, edit, stok berkurang saat disewa, bertambah saat kembali) tidak hilang ketika program ditutup.

---

## Akun Demo untuk Pengujian / Presentasi

| Peran | Username | Password | Keterangan |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Akses manajemen barang, persetujuan sewa, konfirmasi pengembalian |
| **Penyewa** | `penyewa` | `penyewa123` | Akses katalog, keranjang, checkout sewa, riwayat & invoice |

---

## Data Awal Inventaris Demo

| ID | Nama Barang | Kategori | Tarif Harian | Biaya Tambahan | Stok Awal | Formula Biaya |
|---|---|---|---|---|---|---|
| `SNK-001` | Paket Masker & Snorkel | Paket Snorkeling | Rp 50.000 | - | 5 unit | Tarif Harian × Hari |
| `SNK-002` | Paket Fin Snorkeling | Paket Snorkeling | Rp 45.000 | - | 4 unit | Tarif Harian × Hari |
| `PNC-001` | Joran Pancing Laut | Alat Pancing | Rp 75.000 | Perawatan: Rp 15.000 | 3 unit | (Tarif × Hari) + Perawatan |
| `PNC-002` | Reel Pancing Shimano | Alat Pancing | Rp 60.000 | Perawatan: Rp 10.000 | 3 unit | (Tarif × Hari) + Perawatan |
| `SFT-001` | Life Jacket Dewasa | Perlengkapan Keselamatan | Rp 35.000 | Sanitasi: Rp 8.000 | 8 unit | (Tarif × Hari) + Sanitasi |
| `SFT-002` | Pelampung Anak | Perlengkapan Keselamatan | Rp 25.000 | Sanitasi: Rp 5.000 | 6 unit | (Tarif × Hari) + Sanitasi |

---

## Skenario Alur Pengujian Demo

1. **Login Penyewa**:
   - Pilih opsi `1` (Login sebagai Penyewa), masukkan username `penyewa` / password `penyewa123`.
   - Pilih menu `1` untuk melihat katalog barang tersedia dalam tabel rapi.
   - Pilih menu `2` dan masukkan ID `SNK-001`.
   - Pilih menu `2` lagi dan masukkan ID `PNC-001`.
   - Pilih menu `3` untuk melihat keranjang.
   - Pilih menu `4` (Checkout): masukkan durasi sewa `3` hari, pilih metode pembayaran `2` (QRIS), konfirmasi `y`. Transaksi `TRX-001` terbentuk dengan status `MENUNGGU_PERSETUJUAN`.
   - Pilih menu `6` untuk Logout.

2. **Login Admin**:
   - Pilih opsi `2` (Login sebagai Admin), masukkan username `admin` / password `admin123`.
   - Pilih menu `4` untuk melihat transaksi yang menunggu persetujuan.
   - Pilih menu `5`, masukkan `TRX-001`, lalu pilih `A` (Setujui). Sistem menampilkan invoice dan status barang berubah menjadi `DISEWA`.
   - Pilih menu `1` untuk melihat status inventaris (`SNK-001` dan `PNC-001` kini berstatus `DISEWA`).
   - Pilih menu `8` untuk Logout.

3. **Verifikasi Penyewa**:
   - Login kembali sebagai `penyewa`.
   - Pilih menu `5` (Lihat Riwayat/Invoice) untuk melihat transaksi yang sudah disetujui beserta rincian invoice.
   - Logout.

4. **Konfirmasi Pengembalian & Denda**:
   - Login kembali sebagai `admin`.
   - Pilih menu `7` (Konfirmasi Pengembalian).
   - Masukkan ID `TRX-001`. Masukkan tanggal pengembalian (misal tanggal setelah jatuh tempo untuk menguji perhitungan denda Rp 10.000/hari terlambat).
   - Sistem mencetak invoice final dengan denda dan barang otomatis kembali `TERSEDIA`.
