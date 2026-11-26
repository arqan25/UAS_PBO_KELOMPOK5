public class Admin extends Akun {
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }
    public void tampilkanMenu() {
        System.out.println("========== MENU ADMIN ==========");
        System.out.println("1. Lihat Inventaris");
        System.out.println("2. Tambah Barang");
        System.out.println("3. Edit / Nonaktifkan Barang");
        System.out.println("4. Lihat Transaksi Menunggu");
        System.out.println("5. Setujui / Tolak Transaksi");
        System.out.println("6. Lihat Transaksi Aktif");
        System.out.println("7. Konfirmasi Pengembalian");
        System.out.println("8. Logout");
        System.out.println("================================");
    }
}
