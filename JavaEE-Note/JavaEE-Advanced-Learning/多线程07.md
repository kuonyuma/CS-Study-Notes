

@[TOC](文章目录)

---

# 前言
通过本文章我们将探讨如下内容
1. 指令重排序
2. wait与notify

---

# 1. 指令重排序

## 1.1 什么是指令重排序？
在我们买房子的时候一般是先看房，再装修，最后再住进去。
如果出现指令重排序的情况，可能就变成了，先看房，再住进去，最后装修。
显然这是不合理的。

在程序中我们创建一个变量。
其执行顺序应该是如下的：
1. 分配内存地址
2. 初始化
3. 将这个数值赋给变量

在多线程下会出现这样的状况：
一个变量确实分配到了内存地址，但是还没有初始化就被另一个线程拿去用了。
也就是说跳过了第二步，直接来到了第三步。

我们看看如下的代码
```java
static int x = 0, y = 0;
    static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int count = 0;

        while (true) {
            count++;
            x = y = a = b = 0;
            Thread t1 = new Thread(() -> {
                a = 1;          // ①
                x = b;          // ②
            });

            Thread t2 = new Thread(() -> {
                b = 1;          // ③
                y = a;          // ④
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

            if (x == 0 && y == 0) {
                System.out.println("第 " + count + " 次出现 (0,0)！");
                break;
            }
        }
    }
```

在正常情况下其运行结果应该是这样的
线程t1开始运行 ，a 被赋值为1,x 被赋值为0；
线程t2开始运行 ，b被赋值为1，y被赋值为1；

我们看看真实的运行结果：
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/7c81777939ec4c84b090ff4833ecd497.png)
我们可以看到 出现了x = 0, y =0的情况。
我们已经知道x会被赋值为0，y会被赋值为1。
但这里y却没有被赋值为1。
也就是说 ``y = a``这一句有问题。
既然y要为0，那么a一定为0。
这里就出现了a并没有被赋值为1。
出现了我们所说的指令重排序的情况。

我们大概就了解到这种程度即可，在程序运行中还会有其他原因导致这样的问题。比如：线程抢占式运行，内存可见性...


我们可以用``volatile``关键字来避免这种情况的发生，
``volatile``不仅可以让程序强制访问内存，还能防止JVM优化带来的指令重排序问题。

只要给变量加上这个关键字修饰即可
```java
		static volatile int x = 0, y = 0;
    	static volatile int a = 0, b = 0;
```

这样后续无论如何都不会再出现x = y =0的情况了

# 2. wait与notify
## 直观理解
你去餐厅吃饭，
找到一个空位置坐下，
你向服务员说点一个套餐，
服务员听后说“这个套餐现成的售空了，若要吃得等厨师有空的时候再做”，
你给服务员说“我就要吃这个，有货的时候通知我，我先等一等”，
你也不好意思一直坐在这个空位置上毕竟现在人很多，
于是你起来，将这个桌子让了出来（wait），
你开始等服务员的通知，
一段时间后服务员给你发了个消息说“你的套餐已经准备好了”（notify),
服务员将你的套餐放到一张空桌子上(t2线程释放锁），
你立刻坐下开始用餐（拿到锁执行后面的语句）
## 演示
我们看看这个代码
```java
public class demo6 {

    public static void main(String[] args) throws RuntimeException{
        Object ob = new Object();
        Thread t1 = new Thread(() -> {
            synchronized (ob) {
                System.out.println("t1线程获取锁ob");
                try {
                
                    System.out.println("t1 由于wait释放锁");
                    Thread.sleep(1000);

                    ob.wait();
                    System.out.println("t1 被notify唤醒");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized(ob){
                System.out.println("t2 拿到锁");
                try {
                    System.out.println("t2 5秒后启动notify");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                
                ob.notify();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("t2 线程释放锁");
            }
        });

        t1.start();
        t2.start();

    }
}
```
我来解释一下
首先t1拿到锁（ob），
一秒后通过``wait()``立刻释放锁(ob),
t2拿到由t1释放的锁(ob),
t2五秒后使用``notify``尝试唤醒wait，
五秒后t2释放锁，
t2释放锁后t1立即获取锁，
输出：t1 被notify唤醒。


这里要注意一下，当线程t2使用notify后，t1线程并不会立刻获取锁。
原因在于，虽然t2使用了notify，但notify并不会使得t2立刻释放锁，而是要等到t2线程正常运行完毕释放锁，t1才能拿到这个锁继续运行。

关于``wait``其实也可以加入参数进去，这个和``sleep()``一样直接传数字即可。
其含义是最多等待多久。

关于``notify``，还有一个``notifyAll``其作用是一次性唤醒当前所有因为wait休眠的线程，
这里不做过多的讨论。




# 总结

通过本文我们学习了以下核心内容：

1. **指令重排序**：JVM为了优化性能可能会对指令执行顺序进行重排，在多线程环境下可能导致非预期结果。可以通过 `volatile` 关键字来禁止指令重排序，保证内存可见性。

2. **wait 与 notify 机制**：`wait()` 会使当前线程释放锁并进入等待状态，`notify()` 用于唤醒等待中的线程。需要注意的是，`notify()` 不会立即释放锁，线程需执行完毕后才释放。

3. **扩展知识**：`wait()` 支持传入超时参数，`notifyAll()` 可以一次性唤醒所有等待线程，适用于更复杂的并发场景。

