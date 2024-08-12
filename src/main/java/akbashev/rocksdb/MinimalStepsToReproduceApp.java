package akbashev.rocksdb;

import org.rocksdb.*;

public class MinimalStepsToReproduceApp {

    public static final byte[] KEY = "123".getBytes();

    public static void main(String[] args) throws RocksDBException {
        RocksDB.loadLibrary();

        final BlockBasedTableConfig tableOptions = new BlockBasedTableConfig()
                .setCacheIndexAndFilterBlocks(true) // keyExists() works as expected if it's false
                .setBlockCache(new LRUCache(128 * 1024L * 1024L));
        try (Options options = new Options().setCreateIfMissing(true)
                                            .setTableFormatConfig(tableOptions)) {
            try (TtlDB db = openTtlDB(options)) {
                db.put(KEY, new byte[0]);
                db.compactRange();
            }
        }

        tableOptions.setBlockCache(new LRUCache(128 * 1024L * 1024L));
        try (Options options = new Options().setCreateIfMissing(true)
                                            .setTableFormatConfig(tableOptions)) {
            try (TtlDB db = openTtlDB(options)) {
                if (db.keyExists(KEY)) {
                    System.out.println("Key found");
                } else {
                    System.out.println("Key not found");
                }
                System.out.println("get() returns a value = " + (db.get(KEY) != null));

                System.out.println("\nTrying again after the cache got warmed up:");
                if (db.keyExists(KEY)) {
                    System.out.println("Key found");
                } else {
                    System.out.println("Key not found");
                }
            }
        }
    }

    private static TtlDB openTtlDB(Options options) throws RocksDBException {
        return TtlDB.open(options, "/tmp/rocksdb-test", 86400, false);
    }
}
