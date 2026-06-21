package demo2;

/**
 * 教学案例：使用静态内部类实现建造者模式 (Builder Pattern)
 */
public class BuilderPattern {
    
    private String cpu;
    private String ram;
    private String storage;
    private String gpu;

    // 1. 私有化构造器，确保外部只能通过 Builder 来创建对象
    private BuilderPattern(Builder builder) {
        this.cpu = builder.cpu;
        this.ram = builder.ram;
        this.storage = builder.storage;
        this.gpu = builder.gpu;
    }

    @Override
    public String toString() {
        return "电脑配置清单: {" +
                "CPU='" + cpu + '\'' +
                ", 内存='" + ram + '\'' +
                ", 硬盘='" + storage + '\'' +
                ", 显卡='" + gpu + '\'' +
                '}';
    }

    /**
     * 2. 静态内部类 Builder
     * 场景：Builder 只需要负责收集参数，不需要访问外部类的实例成员，
     * 且必须独立于外部类实例存在（用于构建外部类实例），因此定义为 static。
     */
    public static class Builder {
        private String cpu;
        private String ram;
        private String storage;
        private String gpu;

        // 设置参数的方法，通过 return this 实现链式编程
        public Builder setCpu(String cpu) {
            this.cpu = cpu;
            return this;
        }

        public Builder setRam(String ram) {
            this.ram = ram;
            return this;
        }

        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder setGpu(String gpu) {
            this.gpu = gpu;
            return this;
        }

        // 最终调用 build 方法返回外部类对象
        public BuilderPattern build() {
            return new BuilderPattern(this);
        }
    }

    public static void main(String[] args) {
        // 演示：静态内部类的经典应用场景 - 链式构建复杂对象
        BuilderPattern myComputer = new BuilderPattern.Builder()
                .setCpu("Intel i9-13900K")
                .setRam("64GB DDR5")
                .setStorage("2TB NVMe SSD")
                .setGpu("RTX 4090")
                .build();

        System.out.println("--- 建造者模式演示 ---");
        System.out.println(myComputer);
    }
}

