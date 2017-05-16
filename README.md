
## Basic Usage

```java
  Path path = Paths.get("test.bw");
  SeekableByteChannel rbc = Files.newByteChannel(path, EnumSet.of(StandardOpenOption.READ));
  BigWigFile bwf = new BigWigFile(rbc);

  BigWigFileIterator it = bwf.query("chr11", 112643206, 112658727, 0);

  while (it.hasNext()) {
    BigWigFileIteratorType r = it.next();
    BigWigSummaryRecord s = r.getSummary();
    System.out.printf("chrom: %d\n", s.getChromName());
    System.out.printf("from : %d\n", s.getFrom());
    System.out.printf("to   : %d\n", s.getTo());
    System.out.printf("value: %f\n", s.getMean());
    System.out.println();
  }
```
