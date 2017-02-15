```
Linux, i7-3612QM (4x2 cores), Oracle JDK 1.8.0_121-b13, 256 threads

Benchmark                              Mode  Cnt     Score   Error   Units
UUIDBenchmark.testBlockingPooledUUID  thrpt        118.959          ops/ms
UUIDBenchmark.testJavaUtilUUID        thrpt        344.409          ops/ms
UUIDBenchmark.testLivePooledUUID      thrpt        407.690          ops/ms
UUIDBenchmark.testRandomBased1UUID    thrpt        378.887          ops/ms
UUIDBenchmark.testRandomBased2UUID    thrpt       1466.508          ops/ms
UUIDBenchmark.testTimeBasedUUID       thrpt       5170.365          ops/ms
```