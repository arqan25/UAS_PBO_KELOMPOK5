public class Penyewa extends Akun {
    private KeranjangSewa keranjang;

    public Penyewa(String username, String password) {
        super(username, password, "Penyewa");
        this.keranjang = new KeranjangSewa();
    }

    public KeranjangSewa getKeranjang() {
        return keranjang;
    }

    public void tampilkanMenu() {
        System.out.println("========== MENU PENYEWA ==========");
        System.out.println("1. Lihat Barang Tersedia");
        System.out.println("2. Tambah ke Keranjang");
        System.out.println("3. Lihat / Hapus dari Keranjang");
        System.out.println("4. Checkout");
        System.out.println("5. Lihat Riwayat / Invoice");
        System.out.println("6. Logout");
        System.out.println("==================================");
    }
}