package akbashev.rocksdb;

import org.rocksdb.*;

import java.util.UUID;

public class FillDbApp {

    public static void main(String[] args) throws RocksDBException {
        RocksDB.loadLibrary();

        final BlockBasedTableConfig tableOptions = new BlockBasedTableConfig()
                .setFilterPolicy(new BloomFilter(15))
                .setDataBlockIndexType(DataBlockIndexType.kDataBlockBinaryAndHash)
                .setBlockSize(4 * 1024L)
                .setIndexType(IndexType.kTwoLevelIndexSearch)
                .setPartitionFilters(true)
                .setMetadataBlockSize(4096)
                .setCacheIndexAndFilterBlocks(true)
                .setPinTopLevelIndexAndFilter(true)
                .setPinL0FilterAndIndexBlocksInCache(true)
                .setCacheIndexAndFilterBlocksWithHighPriority(true)
                .setOptimizeFiltersForMemory(true);
        tableOptions.setBlockCache(new LRUCache(128 * 1024L * 1024L));
        try (Options options = new Options().setCreateIfMissing(true)
                                            .setLevel0FileNumCompactionTrigger(2)
                                            .setMaxBytesForLevelBase(128 * 1024 * 1024)
                                            .setMaxBackgroundJobs(6)
                                            .setTargetFileSizeBase(12 * 1024 * 1024)
                                            .setLevelCompactionDynamicLevelBytes(true)
                                            .setIncreaseParallelism(4)
                                            .setTableFormatConfig(tableOptions)
                                            .setMemtablePrefixBloomSizeRatio(0.02)
                                            .setMemtableWholeKeyFiltering(true)) {
            try (TtlDB db = TtlDB.open(options, "/tmp/rocksdb-test", 86400, false)) {
                db.put("123".getBytes(), new byte[0]);
                for (int i = 0; i < 5_000_000; i++) {
                    db.put(UUID.randomUUID().toString().getBytes(), new byte[0]);
                    if (i % 10000 == 0) {
                        System.out.println(i);
                    }
                }
            }
        }
    }
}
