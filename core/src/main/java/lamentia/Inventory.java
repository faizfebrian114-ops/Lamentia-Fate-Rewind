package lamentia;

import com.badlogic.gdx.utils.Array;

public class Inventory {
    private Array<Item> items;

    public Inventory() {
        items = new Array<>();
    }

    // Tambah item ke inventory
    public void addItem(Item item) {
        items.add(item);
        System.out.println("Item didapatkan: " + item.getName());
    }

    // Ambil semua list item
    public Array<Item> getItems() {
        return items;
    }

    // Cek apakah ada item tertentu berdasarkan ID
    public boolean hasItem(String id) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Cetak isi inventory ke console untuk debug
    public void printInventory() {
        System.out.println("=== INVENTORY SETA ===");
        for (Item item : items) {
            System.out.println("- [" + item.getType() + "] " + item.getName() + " : " + item.getDescription());
        }
        System.out.println("======================");
    }
}