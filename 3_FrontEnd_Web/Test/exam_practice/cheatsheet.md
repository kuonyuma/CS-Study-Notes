# 📝 闭卷无网《网页设计》期末考试速记秘籍

在闭卷无网的考试环境下（如使用 Adobe Dreamweaver 等老旧编辑器），**代码越简单、越稳健、心智负担越低，得分就越高**。

---

## ⚡ 1. 黄金 HTML5 极简模板 (10秒默写)
考试时，建议**将 CSS 和 JS 全部写在同一个 HTML 文件中**（使用 `<style>` 和 `<script>` 标签），避免多文件路径引用错误导致页面样式丢失或 JS 失效。

```html
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>考试页面标题</title>
    <style>
        /* ================= CSS 样式写在这里 ================= */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box; /* 极重要：防止 padding 撑开盒子 */
        }
        body {
            font-family: Arial, sans-serif;
        }
    </style>
</head>
<body>

    <!-- 网页内容区域 -->
    <div id="app">
        <h1>我的考试页面</h1>
    </div>

    <!-- ================= JS 代码写在 body 底部 ================= -->
    <!-- 这样可以省去 window.onload，确保 DOM 元素已完全加载 -->
    <script>
        console.log("页面已加载完成");
    </script>
</body>
</html>
```

---

## 🎨 2. CSS 三大万能布局公式 (搞定 90% 的页面排版)

### 公式一：Flexbox 水平/垂直居中与对齐 (最常用)
只要想让里面的元素横着排、竖着排、或者居中，就给**父元素**加 Flex。
```css
.parent {
    display: flex;
    justify-content: center; /* 水平居中。可选值: space-between (左右贴边对齐), space-around (均匀分布) */
    align-items: center;     /* 垂直居中 */
    /* flex-direction: column; * 如果需要里面的元素上下垂直排列，取消此行注释 */
}
```

### 公式二：子绝父相定位 (用于图片文字叠加、悬浮窗、返回顶部按钮)
只要想让一个元素**重叠**在另一个元素之上，或者固定在网页某个位置：
```css
.father {
    position: relative; /* 父元素：相对定位，作为子元素的参考坐标 */
    width: 300px;
    height: 200px;
}
.son {
    position: absolute; /* 子元素：绝对定位，脱离文档流 */
    bottom: 10px;       /* 距离父元素底部 10px */
    right: 10px;        /* 距离父元素右侧 10px */
}
```

### 公式三：网页大框架布局 (通用水准)
典型的“头部-主体-尾部”三栏式结构：
```css
.header { height: 80px; background-color: #333; color: white; }
.main { min-height: 500px; display: flex; } /* 主体：左边栏 + 右内容区 */
.sidebar { width: 200px; background-color: #f4f4f4; }
.content { flex: 1; padding: 20px; } /* flex: 1 自动占满剩下宽度 */
.footer { height: 60px; background-color: #222; text-align: center; line-height: 60px; color: #aaa; }
```

---

## ⚡ 3. JS 最稳健的 4 大操作 (绝不报错)

在闭卷无网时，记最简单的 API。不要记过于长或冷门的语法。

### 1. 稳妥获取元素
```javascript
// 方式 A：通过 ID 获取单个元素 (最不容易拼错，最稳健)
var myBtn = document.getElementById("submit-btn");

// 方式 B：通过 CSS 选择器获取 (万能，支持类名、标签等)
var myBox = document.querySelector(".box");
```

### 2. 绑定点击事件
```javascript
myBtn.onclick = function() {
    alert("按钮被点击了！");
};
```

### 3. 控制元素显示与隐藏 (Tab 切换、弹窗核心)
```javascript
// 隐藏元素
myBox.style.display = "none";

// 显示元素
myBox.style.display = "block";
```

### 4. 动态增删 Class (实现激活状态切换)
```javascript
// 添加类名
myBox.classList.add("active");

// 移出类名
myBox.classList.remove("active");

// 切换类名（如果有就去掉，没有就加上）
myBox.classList.toggle("active");
```

---

## 🚨 4. 期末考场保分检查清单 (交卷前必看)

1. **是否有乱码？**
   * 检查 `<head>` 里是否有 `<meta charset="UTF-8">`。
2. **图片路径写对了吗？**
   * 本地图片不要写绝对路径（如 `C:/Users/...`），必须写相对路径（如 `images/logo.png` 或 `./logo.png`）。
3. **JS 逻辑是否失效？**
   * 按下 `F12` 打开浏览器控制台，检查是否有红色的报错信息。
4. **表单验证完后有没有阻止提交？**
   * 如果表单校验失败，必须在事件里写 `event.preventDefault()` 或者 `return false;`，否则页面刷新会导致错误提示消失。
