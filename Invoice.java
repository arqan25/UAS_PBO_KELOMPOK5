import java.util.ArrayList;

public class Invoice {
    public static String generate(Transaksi t) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("                       INVOICE RENTAL                        \n");
        sb.append("============================================================\n");
        sb.append("Nomor Transaksi : ").append(t.getId()).append("\n");
        sb.append("Tanggal Transaksi: ").append(t.getTanggalTransaksi()).append("\n");
        sb.append("Penyewa         : ").append(t.getPenyewa().getUsername()).append("\n");
        sb.append("Status          : ").append(t.getStatus()).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("Rincian Barang:\n");

        ArrayList<Barang> barangList = t.getDaftarBarang();
        int no = 1;
        for (Barang b : barangList) {
            sb.append("  ").append(no++).append(". ").append(b.getNama()).append("\n");
            sb.append("     Kategori : ").append(b.getKategori()).append("\n");
            sb.append("     Formula  : ").append(b.getFormulaDescription()).append("\n");
            sb.append("     Biaya    : Rp ").append(b.hitungBiaya(t.getJumlahHari())).append("\n");
        }

        sb.append("------------------------------------------------------------\n");
        sb.append("Lama Sewa       : ").append(t.getJumlahHari()).append(" hari\n");
        sb.append("Tanggal Mulai   : ").append(t.getTanggalMulai()).append("\n");
        sb.append("Jatuh Tempo     : ").append(t.getTanggalJatuhTempo()).append("\n");
        sb.append("Metode Bayar    : ").append(t.getMetodePembayaran().getNamaPembayaran()).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("TOTAL           : Rp ").append(t.getTotalBiaya()).append("\n");
        sb.append("============================================================\n");

        return sb.toString();
    }

    public static String generateDenganDenda(Transaksi t) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("               INVOICE RENTAL (PENGEMBALIAN)                 \n");
        sb.append("============================================================\n");
        sb.append("Nomor Transaksi : ").append(t.getId()).append("\n");
        sb.append("Tanggal Transaksi: ").append(t.getTanggalTransaksi()).append("\n");
        sb.append("Penyewa         : ").append(t.getPenyewa().getUsername()).append("\n");
        sb.append("Status          : ").append(t.getStatus()).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("Rincian Barang:\n");

        ArrayList<Barang> barangList = t.getDaftarBarang();
        int no = 1;
        for (Barang b : barangList) {
            sb.append("  ").append(no++).append(". ").append(b.getNama()).append("\n");
            sb.append("     Kategori : ").append(b.getKategori()).append("\n");
            sb.append("     Formula  : ").append(b.getFormulaDescription()).append("\n");
            sb.append("     Biaya    : Rp ").append(b.hitungBiaya(t.getJumlahHari())).append("\n");
        }

        sb.append("------------------------------------------------------------\n");
        sb.append("Lama Sewa       : ").append(t.getJumlahHari()).append(" hari\n");
        sb.append("Tanggal Mulai   : ").append(t.getTanggalMulai()).append("\n");
        sb.append("Jatuh Tempo     : ").append(t.getTanggalJatuhTempo()).append("\n");
        sb.append("Tanggal Kembali : ").append(t.getTanggalKembali()).append("\n");
        sb.append("Metode Bayar    : ").append(t.getMetodePembayaran().getNamaPembayaran()).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("Total Sewa      : Rp ").append(t.getTotalBiaya()).append("\n");
        sb.append("Denda           : Rp ").append(t.getDenda()).append("\n");
        sb.append("TOTAL BAYAR     : Rp ").append(t.getTotalBiaya().add(t.getDenda())).append("\n");
        sb.append("============================================================\n");

        return sb.toString();
    }
}
