# virtual threads 

## no virtual threads


Summary:
Total:	20.1259 secs
Slowest:	10.1037 secs
Fastest:	5.0905 secs
Average:	8.8096 secs
Requests/sec:	1.9875

```
Thread[#73,http-nio-8080-exec-9,5,main]:Thread[#73,http-nio-8080-exec-9,5,main]
Thread[#69,http-nio-8080-exec-5,5,main]:Thread[#69,http-nio-8080-exec-5,5,main]
Thread[#66,http-nio-8080-exec-2,5,main]:Thread[#66,http-nio-8080-exec-2,5,main]
Thread[#68,http-nio-8080-exec-4,5,main]:Thread[#68,http-nio-8080-exec-4,5,main]
Thread[#67,http-nio-8080-exec-3,5,main]:Thread[#67,http-nio-8080-exec-3,5,main]
```

## with virtual threads
Total:	10.1268 secs
Slowest:	5.1193 secs
Fastest:	5.0070 secs
Average:	5.0630 secs
Requests/sec:	3.9499

```
VirtualThread[#114,tomcat-handler-19]/runnable@ForkJoinPool-1-worker-1 : VirtualThread[#114,tomcat-handler-19]/runnable@ForkJoinPool-1-worker-9
VirtualThread[#104,tomcat-handler-13]/runnable@ForkJoinPool-1-worker-13 : VirtualThread[#104,tomcat-handler-13]/runnable@ForkJoinPool-1-worker-16
VirtualThread[#87,tomcat-handler-4]/runnable@ForkJoinPool-1-worker-17 : VirtualThread[#87,tomcat-handler-4]/runnable@ForkJoinPool-1-worker-9
```