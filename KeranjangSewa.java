import java.math.BigDecimal;
import java.util.ArrayList;

public class KeranjangSewa {
    private ArrayList<Barang> items;

    public KeranjangSewa() {
        this.items = new ArrayList<>();
    }

    public void tambahBarang(Barang b) {
        if (!b.isTersedia()) {
            throw new RentalException("Barang '" + b.getNama() + "' tidak tersedia untuk disewa.");
        }
        for (Barang item : items) {
            if (item.getId().equals(b.getId())) {
                throw new RentalException("Barang '" + b.getNama() + "' sudah ada di keranjang.");
            }
        }
        items.add(b);
    }

    public void hapusBarang(String id) {
        boolean removed = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                items.remove(i);
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new RentalException("Barang dengan ID '" + id + "' tidak ditemukan di keranjang.");
        }
    }

    public ArrayList<Barang> getItems() {
        return new ArrayList<>(items);
    }

    public void kosongkan() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public BigDecimal hitungTotal(int jumlahHari) {
        BigDecimal total = BigDecimal.ZERO;
        for (Barang b : items) {
            total = total.add(b.hitungBiaya(jumlahHari));
        }
        return total;
    }
}