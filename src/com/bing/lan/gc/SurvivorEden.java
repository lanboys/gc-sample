package com.bing.lan.gc;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 *  -XX:+PrintGCDetails
 *  -Xms512m 起始堆内存
 *  -Xmx512m 最大堆内存
 *  -Xmn300m 新生代大小 优先级大于 -XX:NewRatio=2
 *
 *  -XX:+PrintFlagsFinal
 *  -XX:+PrintFlagsInitial
 *  -XX:+PrintCommandLineFlags
 *
 *  -XX:+UseSerialGC 年轻代和老年代都用串行收集器
 * -XX:+UseParNewGC 年轻代使用 ParNew，老年代使用 Serial Old
 * -XX:+UseParallelGC 年轻代使用 ParallelGC，老年代使用 Serial Old
 * -XX:+UseParallelOldGC 新生代和老年代都使用并行收集器
 * -XX:+UseConcMarkSweepGC，表示年轻代使用 ParNew，老年代的用 CMS
 * -XX:+UseG1GC 使用 G1垃圾回收器
 * -XX:+UseZGC 使用 ZGC 垃圾回收器
 *
 * 设置堆内存大小
 *
 * https://blog.csdn.net/chengqiuming/article/details/118539304
 * https://blog.csdn.net/weixin_44359543/article/details/109170158
 * https://www.cnblogs.com/ysocean/p/11109018.html
 *
 * Java堆区用于存储Java对象实例，那么堆的大小在JVM启动时就已经设定好了，大家可以通过选项”-Xmx"和“-Xms"来进行设置。
 * “-Xms"用于表示堆区的起始内存，等价于-XX：InitialHeapSize , ms是memory start的简称
 * “-Xmx"”则用 于表示堆区的最大内存，等价于-XX:MaxHeapSize , mx是memory max的简称
 * 一旦堆区中的内存大小超过“-Xmx"所指定的最大内存时，将会抛出OutOfMemoryError异常。
 * 通常会将-Xms 和-Xmx两个参数配置相同的值，其目的是为了能够在java垃圾回收机制清理完堆区后不需要重新分隔计算堆区的大小，从而提高性能。
 *
 * 默认情况下，
 *    初始内存大小：物理电脑内存大小/64
 *    最大内存大小：物理电脑内存大小/4
 *
 *      电脑16G内存 初始：256M 最大：4G
 *
 * 配置新生代与老年代在堆结构的占比(一般不会调)
 *
 * 默认-XX:NewRatio=2，表示新生代占1，老年代占2，新生代占整个堆的1/3
 * 可以修改-XX:NewRatio=4，表示新生代占1，老年代占4，新生代占整个堆的1/5
 *
 *  所以默认内存占比  S0:S1:Eden:Old = 1:1:6:16
 */

public class SurvivorEden {

  private static List<byte[]> old = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    printSystemStatus();
    // 返回 Java 虚拟机中的堆内存总量
    long initialMemory = Runtime.getRuntime().totalMemory();
    // 返回 Java 虚拟机试图使用的最大堆内存量
    long maxMemory = Runtime.getRuntime().maxMemory();

    //S0:S1:Eden:Old = 1:1:6:16 = 24
    long size = initialMemory / 23;
    long maxSize = maxMemory / 23;

    // S0C 和 S1C 只计算一个。这两个只能二选一用，所以算出来的跟实际设置的有区别, 少了一份Survivor空间
    System.out.println("实际的 堆内存总量: " + getSizeString(initialMemory));
    System.out.println("实际的 最大堆内存量: " + getSizeString(maxMemory));
    System.out.println("==========jdk1.8 默认参数下的大致算法，不一定准确，而且运行中还会发生变化=============");
    System.out.println("配置的 堆内存总量: " + getSizeString(size * 24));
    System.out.println("Survivor1 内存总量: " + getSizeString(size));
    System.out.println("Survivor2 内存总量: " + getSizeString(size));
    System.out.println("Eden 内存总量: " + getSizeString(size * 6));
    System.out.println("Old 内存总量: " + getSizeString(size * 16));
    System.out.println("默认比例: S0 : S1 : Eden : Old = 1 : 1 : 6 : 16");
    System.out.println("默认比例: Young(S0 : S1 : Eden) : Old = 8 : 16 = 1 : 2");

    while (true) {
      int read = System.in.read();
      System.out.println("main(): " + read);
      if (read == 49) {//1
        System.gc();
      } else if (read == 50) {//2
        //   分配 1m 内存
        byte[] bytes = new byte[1 * 1024 * 1024];
      } else if (read == 51) {//3
        //   分配 10m 内存
        byte[] bytes = new byte[10 * 1024 * 1024];
      } else if (read == 52) {//4
        //   分配 100m 内存
        byte[] bytes = new byte[100 * 1024 * 1024];
      } else if (read == 53) {//5
        //   分配 300m 内存
        byte[] bytes = new byte[300 * 1024 * 1024];
      }else if (read == 54) {//6
        //   分配 200m 常驻内存
        byte[] bytes = new byte[200 * 1024 * 1024];
        old.add(bytes);
      }
    }
  }

  public static String getSizeString(long size) {
    return size / 1024 / 1024 + " Mb, " + size / 1024 + " Kb";
    //return size / 1024 / 1024 + " Mb";
    //return size / 1024 + " Kb";
    //return size + " b";
  }

  public static void printSystemStatus() throws Exception {
    System.out.println("=======================");
    OperatingSystemMXBean osmxb = ManagementFactory.getOperatingSystemMXBean();
    if (osmxb instanceof com.sun.management.OperatingSystemMXBean) {
      com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) osmxb;
      long physicalTotal = os.getTotalPhysicalMemorySize();
      print("physicalTotal", physicalTotal);
      long freePhysicalMemorySize = os.getFreePhysicalMemorySize();
      print("freePhysicalMemorySize", freePhysicalMemorySize);
      //long totalSwapSpaceSize = os.getTotalSwapSpaceSize();
      //print("totalSwapSpaceSize", totalSwapSpaceSize);
      //long freeSwapSpaceSize = os.getFreeSwapSpaceSize();
      //print("freeSwapSpaceSize", freeSwapSpaceSize);
      //
      //long committedVirtualMemorySize = os.getCommittedVirtualMemorySize();
      //print("committedVirtualMemorySize", committedVirtualMemorySize);
      //double processCpuLoad = os.getProcessCpuLoad();
      //long processCpuTime = os.getProcessCpuTime();
    }
    System.out.println("=======================");
  }

  public static void print(String name, long value) throws Exception {
    System.out.println(name + ": " + value + " byte, " + value / (1024 * 1024 * 1024L) + " GB");
  }
}
