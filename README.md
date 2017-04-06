
## Basic Usage

```java
  Path path = Paths.get("test.bw");
  SeekableByteChannel rbc = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ));
  BigWigFile bwf = new BigWigFile(rbc);

  BbiFileIterator it = bwf.Query("chr11", 112643206, 112658727, 0);

  while (it.hasNext()) {
    BbiFileIteratorType r = it.next();
    BbiSummaryRecord s = r.GetSummary();
    System.out.printf("from : %d\n", s.getFrom());
    System.out.printf("to   : %d\n", s.getTo());
    System.out.printf("value: %f\n", s.getMean());
    System.out.println();
  }
```
