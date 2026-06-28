@[TOC](目录)
---

# 前言

本文将介绍 `FileWriter` 和 `FileReader` 的基本使用，解析它们在读写过程中的底层原理（如缓冲区和文件描述符），并深入探讨如何使用包装流 `BufferedWriter` 与 `BufferedReader` 来大幅度提升读写效率和开发便捷性。

---

# 前置知识

在 Java 的文件操作中，主要分为两大流派：**字节流**与**字符流**。

无论是 `.jpg` 图片、`.txt` 文本还是 `.mp4` 视频，这些文件在计算机底层都是由 `0` 和 `1` 组成的二进制数据。每 8 个 `0` 或 `1` 凑成一组，就叫做一个字节（Byte）。字节是计算机中最基础的存储单位，这也是为什么我们在电脑上看到的文件，基本都是以字节（以及 KB、MB、GB）为单位来描述大小的。

然而，人类是无法直接看懂这一大堆 `0` 和 `1` 的。我们需要通过文字来理解信息。像一个汉字“中”，或者一个英文字母“A”，就是一个**字符**。根据不同的编码格式，一个字符占用的字节数也不同。例如，英文字母在常见编码下通常占 1 个字节，而汉字“中”在 GBK 编码下占 2 个字节，在现代常用的 UTF-8 编码下则占 3 个字节。

- **字节流**：它完全不管文件内容是什么，只管把一串串二进制的“字节”原封不动地搬运过去。这非常适合处理图片、音频、视频等非文本文件。
- **字符流**：它会以**字符**为基本单位来读写（例如直接读写一个“中”字）。字符流在底层会自动帮我们处理“字符”与“二进制字节”之间的转换（编码与解码），让我们在操作 `.txt`、`.md` 等文本文件时，不需要手动去转换字节，更符合人类的阅读和编写直觉。

---

# 创建 FileWriter 

`FileWriter` 是 Java 提供的一个以**字符**为基本单位、专门用来将文本数据写入文件的 API（字符输出流）。

### 1. 基础用法与代码实现

创建 `FileWriter` 的基本操作如下：
```java
FileWriter writer = new FileWriter(String fileName);
```
这里的 `fileName` 是你打算操作的文件路径或名称。

假设我们想往一个叫 `"demo.md"` 的文件中写入一行 `"hello world"`，完整的 Java 代码实现如下：

```java
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterDemo {
    public static void main(String[] args) {  
        FileWriter writer = null;
        try {  
            // 1. 创建指向 demo.md 的写入流对象
            writer = new FileWriter("demo.md");  
            
            // 2. 调用 write 方法将数据写入
            writer.write("hello world");  
            
        } catch (IOException e) {  
            // 异常处理：打印错误堆栈信息，便于调试
            e.printStackTrace();  
        } finally {
            // 3. 在 finally 中关闭流，确保资源一定会释放
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

> **💡 避坑提示：**
> 1. 写入数据的方法名是 **`write()`**，而不是 `writer()`。
> 2. `FileWriter` 在创建和写入时可能会引发 `IOException`（输入输出异常），例如文件被锁死、磁盘满或者无权限创建文件。在 Java 中，这是一个受检异常（Checked Exception），必须进行捕获或声明抛出。

---

### 2. 为什么要调用 `writer.close()`？

在写完数据后，我们必须调用 `close()` 方法关闭资源。这背后有两个核心原因：

#### 原因一：释放系统资源（还书原则）
创建 `FileWriter` 就像是**从图书馆借了一本书**。操作系统会为这个操作分配一个“文件描述符”（File Descriptor），并对该文件加锁，防止其他程序冲突修改。
如果我们只借不还（不调用 `close()`），系统资源就会被一直占用。当占用的文件描述符达到上限（例如很多系统限制单个进程最多打开 1024 个文件）时，程序就无法再打开任何新文件了。

#### 原因二：刷新缓冲区（Buffer）
因为 CPU 读写硬盘的速度远慢于读写内存的速度，如果每写一个字符都直接写入硬盘，效率会非常低下。
为了提高效率，`FileWriter` 底层默认使用了一个大小为 **8 KB (8192 字节)** 的缓冲区（Buffer）。
- 当你调用 `writer.write("hello world")` 时，内容其实**并没有立刻写到硬盘上**，而是暂时存放在内存的缓冲区中。
- 当你调用 `writer.close()` 时，Java 会把缓冲区中的所有残余数据“一次性打包”推送到硬盘上（这个过程称为 Flush），随后关闭流。

---

### 3. 追加模式（Append Mode）

如果你多次运行上面的程序，打开 `demo.md` 会发现里面永远只有一行 `"hello world"`。
这是因为默认的构造函数是**覆写模式**——每次创建流对象都会清空原文件，重新写入。

如果希望多次运行程序时，内容能够**在原内容后面继续写**（保留历史数据），我们需要开启**追加模式**。只需在构造函数中传入第二个参数 `true`：

```java
// 开启追加模式（参数为 true）
FileWriter writer = new FileWriter("demo.md", true);
```

#### 实现换行追加
如果想要每次追加时换行，我们可以写入一个换行符。在不同的操作系统中，换行符的表示可能不同（Windows 是 `\r\n`，Linux/Unix 是 `\n`）。在 Java 中，我们可以直接写入 `\n`，或者使用更加通用的 `System.lineSeparator()`。

追加模式下的效果演示：
![[Pasted image 20260627162847.png|697]]

如果要换行则输入：
```java
writer.write("\n"); // 或者 writer.write(System.lineSeparator());
```
![[Pasted image 20260627163107.png]]

---

### 4. 实时刷新：`flush()`

`close()` 通常是所有内容都写完后、流生命周期结束时的操作。
但是在某些高频或长时间运行的场景下，如果我们 write 了大量内容却不关闭流，万一程序中途异常崩溃或电脑断电，留在内存缓冲区里的数据就会彻底丢失。

为了安全起见，我们希望**写一部分就保存一部分**，这时就可以使用 `flush()` 方法：

```java
writer.write("第一部分内容"); // 数据写入内存缓冲区
writer.flush();             // 立即将当前缓冲区的数据“推”到硬盘上，但不关闭流

writer.write("第二部分内容"); // 继续写入缓冲区
writer.flush();             // 再次推送到硬盘
...
writer.close();             // 最终写完后关闭流并释放资源
```

#### ⚖️ `flush()` 与 `close()` 的区别
- **`flush()`**：只刷新缓冲区，把数据推入硬盘，流依然处于**开启**状态，后续可以继续调用 `write()` 写入。
- **`close()`**：先自动刷新缓冲区，然后**关闭**流并释放系统资源，之后无法再进行写入操作。

---

# 创建 FileReader

`FileReader` 是用于从文件中读取字符数据的 API（字符输入流）。其基本构造语法与 `FileWriter` 非常相似：
```java
FileReader reader = new FileReader(String fileName);
```

### 1. 单字符读取：`read()`

从文件中读取数据可以使用 `read()` 方法。该方法的定义签名如下：
```java
public int read() throws IOException
```
**返回值为什么是 `int` 类型？**
- `read()` 方法每次只读取**一个字符**。
- 它返回的是该字符对应的 Unicode 码点（例如读取到字符 `'0'`，返回的是整数 `48`；读取到字符 `'A'`，返回的是 `65`）。
- 如果已经读取到了文件末尾（EOF，End of File），该方法会返回 `-1`。
- 要得到我们想要的字符，需要通过强制类型转换 `(char)` 将其转换回来。

示例代码：
```java
FileReader reader = new FileReader("demo.md");
int tmp;
while ((tmp = reader.read()) != -1) {
    System.out.print((char) tmp); // 强转为字符输出
}
reader.close();
```

---

### 2. 批量读取：`read(char[] cbuf)`

单字符读取效率较低，因为每次读取都要进行一次 I/O 交互。我们可以使用字符数组作为缓冲区进行**批量读取**：
```java
public int read(char[] cbuf) throws IOException
```
- **参数 `cbuf`**：我们预先定义好的字符数组，用于存放读取到的字符。
- **返回值**：实际读取到的字符个数。如果已经到达文件末尾，返回 `-1`。

#### 批量读取示例代码
```java
import java.io.FileReader;
import java.io.IOException;

public class FileReaderDemo {
    public static void main(String[] args) {
        try (FileReader fileReader = new FileReader("demo.md")) {
            // 定义一个大小为 3 的字符数组作为缓冲区
            char[] chs = new char[3];    
            int len;
            // 循环读取，每次最多读满数组大小（3个字符）
            while ((len = fileReader.read(chs)) != -1) {
                // 将读取到的有效字符转换为字符串打印
                System.out.print(new String(chs, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

> **💡 为什么使用 `try-with-resources` 语法？**
> 上面的代码使用了 `try(FileReader...)` 语法，这是 Java 7 引入的 **try-with-resources** 特性。任何实现了 `AutoCloseable` 接口的资源（如 `FileReader`/`FileWriter`）在 `try` 块执行完毕后，**都会被自动关闭**，无需手动编写 `finally` 和 `close()`，从而让代码更简洁、更安全。

同样，读取文件完毕后必须关闭流，释放系统资源。

![[Pasted image 20260527172531.png]]

---

# 缓冲字符流：BufferedWriter 与 BufferedReader

虽然原生的 `FileWriter` 和 `FileReader` 内部有默认的字符转换缓冲，但当遇到**高频的少量字符读写**或**需要按行处理文本**时，原生的流使用起来不太方便，性能也还有提升空间。

为了提供更好的读写效率和更便捷的 API，Java 提供了包装流（又称处理流/装饰者模式）：**`BufferedWriter`** 与 **`BufferedReader`**。它们就像是给底层的 FileWriter 和 FileReader 套上了一层“高效外壳”。

---

### 1. BufferedWriter

####  语法与声明
使用时，只需将底层的 `FileWriter` 作为参数传入 `BufferedWriter` 的构造函数中：
```java
// 套上 BufferedWriter 这层外壳，同时指定底层 FileWriter 开启追加模式
BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("demo.md", true));
```

####  核心便捷功能
- **写入文本**：使用与以前相同的 `write()` 方法，如：`bufferedWriter.write("hello world");`。
- **跨平台换行**：提供了一个极佳的换行方法 **`newLine()`**。
  - 以前我们需要手动写入 `\n` 或 `\r\n`，不同的操作系统（Windows 与 Linux）识别的换行符不同，移植代码容易出错。
  - 调用 `bufferedWriter.newLine()` 时，Java 会根据当前的操作系统**自动写入匹配的换行符**，优雅地实现了跨平台换行。

示例：
```java
bufferedWriter.write("hello world");
bufferedWriter.newLine(); // 自动根据系统写入换行符
bufferedWriter.write("java is fun");
```

---

### 2. BufferedReader

####  语法与声明
将 `FileReader` 传入其构造函数中：
```java
BufferedReader bufferedReader = new BufferedReader(new FileReader("demo.md"));
```

#### 核心便捷功能：按行读取 `readLine()`
原生的 `FileReader.read()` 每次只能读取一个字符或装满一个数组，如果需要“读取一整行”，写起来非常繁琐。
`BufferedReader` 提供了 **`readLine()`** 方法，可以**一次性读取一整行文本**。

##### `readLine()` 的运行逻辑：
假设在文件中我们存储了三行文本：
```text
今天天气真好
java真有意思
java没有意思
```
而在计算机底层的文本眼中，这三行内容实际上是以换行符相连的长字符串：
`今天天气真好\njava真有意思\njava没有意思`

`readLine()` 的工作原理就是：**持续向下读取字符，直到遇到换行符（`\n` 或 `\r` 或 `\r\n`），然后把这一行前面（不包含换行符本身）的所有字符作为一个字符串返回。**

- 第一次调用 `bufferedReader.readLine()` $\rightarrow$ 返回 `"今天天气真好"`
- 第二次调用 `bufferedReader.readLine()` $\rightarrow$ 返回 `"java真有意思"`
- 第三次调用 `bufferedReader.readLine()` $\rightarrow$ 返回 `"java没有意思"`
- 第四次调用 `bufferedReader.readLine()` $\rightarrow$ 已经到了文件末尾，返回 **`null`**。

##### 💻 循环读取整行的最佳实践：
由于 `readLine()` 读到末尾返回的是 `null`（不同于单字符 `read()` 返回 `-1`），我们可以这样循环读取整个文件：

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BufferedReaderDemo {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("demo.md"))) {
            String line;
            // 循环读取每一行，直到返回 null 结束
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

### 3. 深入底层：缓冲流为什么能提高读写效率？

你可能会问：既然 `FileWriter` 底层已经有了一个缓冲区，为什么套上 `BufferedWriter` 性能会更好？这涉及到 **「锁的持有时间」** 与 **「系统级资源调度」**。

#### 锁的性能开销
在 Java 中，字符流的读写操作都是**线程安全**的。每次你调用 `FileWriter.write()` 方法，Java 底层都会进入一个同步块（`synchronized (lock)`）对流加锁，写完后解锁：

```java
// 底层字符流写入源码节选
public void write(String str, int off, int len) throws IOException {  
    synchronized (lock) {  
        // 每次写入都会抢占这把锁
        ...
        write(cbuf, 0, len);  
    }  
}
```
**加锁与解锁操作是非常消耗 CPU 性能的**。
- 如果我们使用原生的 `FileWriter` **频繁、高频**地写入短小的字符串（例如在一个循环里每次只写一个字符或几个字符），程序就会不断地在 CPU 中进行“申请锁 $\rightarrow$ 写入几字节 $\rightarrow$ 释放锁”的死循环。这就好比你每次寄快递都只寄一张信纸，每次都要重新打包、填单、装车，效率极低。

#### 📦 缓冲包装流的“批量打包”机制
`BufferedWriter` 和底层的 `FileWriter` 共享同一把锁。但是，`BufferedWriter` 在内存中维护了一个字符数组 `char[] cb`（默认大小为 8192 个字符）。
```java
public void write(int c) throws IOException {  
    synchronized (lock) {  
        ensureOpen();  
        growIfNeeded(1);  
        if (nextChar >= nChars)  
            flushBuffer();  
        cb[nextChar++] = (char) c;  //数组cb
    }  
}
```
- 当你调用 `BufferedWriter.write("a")` 时，它**并不会直接去调用底层的 FileWriter**。
- 它做的事情极其简单且快速：**直接在内存的字符数组中进行数组赋值操作（`cb[size++] = 'a'`）**。这个过程只在内存中完成，不需要经过系统级别的写盘操作，因此速度极快。
- 只有当 `BufferedWriter` 维护的这个内存数组**被填满**（或者我们手动调用 `flush()` / `close()`）时，它才会“一次性加锁”，把这 8192 个字符打包交给底层的 `FileWriter` 处理。

这就相当于原本需要频繁加锁解锁 8000 次的操作，现在被缩减成了**只加锁 1 次**。锁的持有时间被大幅度缩短，系统调用和硬件读写次数也减少了成百上千倍，这就是缓冲包装流极高效率的秘密。

---

# 总结

1. **基本概念**：`FileWriter` 和 `FileReader` 分别是用于向文本文件写入/读取字符的字符流 API，底层会自动处理字符编码与字节数据的转换。
2. **缓冲机制**：`FileWriter` 内部自带 8 KB 的缓冲区。为了提高 I/O 效率，写入的内容会先暂存在内存缓冲区中，直至缓冲区满、调用 `flush()` 或 `close()` 时才会被推送到硬盘。
3. **缓冲包装流**：`BufferedWriter` 和 `BufferedReader` 是装饰类（包装流），它们分别包裹了底层的 `FileWriter` 和 `FileReader`，大大提升了性能和便捷性。
4. **跨平台与行读取**：
   - `BufferedWriter` 提供了 **`newLine()`** 方法，能够根据当前操作系统自动匹配并写入相应的换行符。
   - `BufferedReader` 提供了 **`readLine()`** 方法，能够以行为单位读取文本，当读到文件末尾时返回 **`null`**。
5. **底层提效原理**：字符流的底层操作包含同步锁（`synchronized`），频繁写小字符会导致锁的申请与释放开销巨大。`BufferedWriter` 维护了内存字符数组，写数据时只做快速的内存数组赋值，存满后才一次性调用底层流写入，极大地**缩短了锁的持有时间**并减少了硬件交互频率。
6. **资源关闭**：使用完毕后必须释放资源。在使用包装流时，我们**只需关闭最外层的流**（如调用 `bufferedWriter.close()`），它会自动帮我们关闭被包裹的底层流。
