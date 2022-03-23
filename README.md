# gc-sample

```$xslt

jdk >= 11

-Xmx20m
-Xmn4m
-XX:+UseConcMarkSweepGC
-verbose:gc
-Xlog:gc,gc+ref=debug,gc+heap=debug,gc+age=trace:file=./gclogs/gc_%p.log:tags,uptime,time,level
-Xlog:safepoint:file=./gclogs/safepoint_%p.log:tags,uptime,time,level
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./gclogs
-XX:ErrorFile=./gclogs/hs_error_pid%p.log
-XX:-OmitStackTraceInFastThrow

```
```
-Xmx20m
-Xmn4m
-XX:+UseG1GC
-verbose:gc
-Xlog:gc,gc+ref=debug,gc+heap=debug,gc+age=trace:file=./gclogs/gc_%p.log:tags,uptime,time,level
-Xlog:safepoint:file=./gclogs/safepoint_%p.log:tags,uptime,time,level
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./gclogs
-XX:ErrorFile=./gclogs/hs_error_pid%p.log
-XX:-OmitStackTraceInFastThrow
-XX:MetaspaceSize=16M
-XX:MaxMetaspaceSize=16M
```