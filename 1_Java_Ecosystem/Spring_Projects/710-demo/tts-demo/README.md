# TTS Demo

Java 后端 + HTML/CSS/原生 JavaScript 前端。前端文件由 Antigravity CLI（agy）按 [接口协议](docs/api-contract.md) 实现。

- Gemini：`gemini-3.8-flash-tts`，30 种音色，可描述朗读风格，输出 WAV。
- 微软：Edge 浏览器在线朗读语音，无需密钥，提供中文、粤语、日语和英语音色，可调语速，输出 MP3。
- 支持播放、下载、错误提示；单次最多 3000 字符，同时最多两条合成任务。
- 两种引擎均需网络。Gemini 使用你的项目额度；Edge 依赖浏览器在线朗读协议，其上游接口可能变化。

## 启动

环境：Java 17+、Maven 3.9+、PowerShell 7。当前机器已具备。

在本目录执行（也可双击 `start.cmd`）：

```powershell
.\start.ps1
```

脚本先运行测试并打包，再启动服务。打开 **http://localhost:8787**。前台终端按 Ctrl+C 停止。

已构建后快速启动：

```powershell
.\start.ps1 -SkipBuild
```

后台运行与停止：

```powershell
.\start.ps1 -SkipBuild -Background
.\stop.ps1
```

`-Port 8888` 可更换端口。服务仅监听本机 `127.0.0.1`，无需数据库、Node 服务或前端构建。可执行包位于 `target/tts-demo.jar`。

## 密钥

后端优先读取 `GEMINI_API_KEY`，其次 `GOOGLE_GENERATIVE_AI_API_KEY`。启动脚本支持读取进程、用户和系统环境变量；密钥不写入文件、不传给前端、不输出到日志。修改变量后重启服务。

## 验证

```powershell
mvn -B test
.\scripts\smoke.ps1
```

`smoke.ps1` 会真实调用两个引擎，测试音频保存到 `.local/`。网页也可直接操作验证。

开发用浏览器回归（通过本机 Edge 的独立无头实例运行，测试代码不接触日常浏览器配置）：

```powershell
npm install --prefix .tools --no-save --no-audit --no-fund playwright
node scripts/browser-smoke.cjs
```

浏览器回归验证真实音频解码和下载、加载状态、失败后保留上次结果、错误文本渲染和移动端布局，并保存 `.local/desktop.png`、`.local/mobile.png`。测试工具不参与服务运行。

## 接口与实现

- `GET /api/health`：本地存活检查。
- `GET /api/config`：引擎、音色、限制与密钥是否存在。
- `POST /api/tts`：JSON 请求，成功返回音频二进制，失败返回统一 JSON 错误。

Java 使用 JDK HttpServer 提供接口及静态页面，HttpClient/Java WebSocket 直连语音服务；Jackson 处理 JSON。无 Python 或 Node 后端。

接入资料：[Google Gemini TTS 官方文档](https://ai.google.dev/gemini-api/docs/speech-generation)、[Edge 朗读协议参考实现](https://github.com/rany2/edge-tts)。
