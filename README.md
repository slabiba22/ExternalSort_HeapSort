# ExternalSort_HeapSort
Sorts a file using a modified version of Heapsort. The array being sorted will be the file itself, rather than an array stored in memory. All accesses to the file will be mediated by a buffer pool. The buffer pool will store 4096-byte blocks (1024 records). The buffer pool will be organized using the Least Recently Used (LRU) replacement scheme.
