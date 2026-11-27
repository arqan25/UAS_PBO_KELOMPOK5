import java.math.BigDecimal;
import java.util.ArrayList;

public class DaftarBarang {
    private ArrayList<Barang> daftarBarang;

    public DaftarBarang() {
        this.daftarBarang = new ArrayList<>();
    }

    public void tambahBarang(Barang b) {
        daftarBarang.add(b);
    }

    public void kosongkan() {
        daftarBarang.clear();
    }

    public void hapusBarang(String id) {
        Barang barang = cariBarang(id);
        if (!barang.isTersedia()) {
            throw new RentalException("Barang dengan ID '" + id + "' sedang disewa dan tidak dapat dihapus.");
        }
        daftarBarang.remove(barang);
    }

    public void editBarang(String id, String nama, BigDecimal tarifHarian, String deskripsi) {
        Barang barang = cariBarang(id);
        if (nama != null) {
            barang.setNama(nama);
        }
        if (tarifHarian != null) {
            barang.setTarifHarian(tarifHarian);
        }
        if (deskripsi != null) {
            barang.setDeskripsi(deskripsi);
        }
    }

    public Barang cariBarang(String id) {
        for (Barang b : daftarBarang) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        throw new RentalException("Barang dengan ID '" + id + "' tidak ditemukan.");
    }

    public ArrayList<Barang> getBarangTersedia() {
        ArrayList<Barang> tersedia = new ArrayList<>();
        for (Barang b : daftarBarang) {
            if (b.isTersedia() && b.isAktif() && b.getStok() > 0) {
                tersedia.add(b);
            }
        }
        return tersedia;
    }

    public ArrayList<Barang> getSemuaBarang() {
        return new ArrayList<>(daftarBarang);
    }

    public void nonaktifkanBarang(String id) {
        Barang barang = cariBarang(id);
        barang.setAktif(false);
    }
}
