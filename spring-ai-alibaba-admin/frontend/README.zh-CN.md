![](https://gw.alicdn.com/imgextra/i4/O1CN01e5pj8L1K9T2t2TP1u_!!6000000001121-1-tps-1000-625.gif)

<p align="center"><a href="./README.md">English</a> | 中文</p>

## 📋 项目概述
frontend-monorepo 是一个基于 Monorepo 架构的前端项目，主要包含工作台应用和可视化工作流编辑器两大核心功能模块。项目采用现代前端技术栈，支持多语言国际化。

### 📂 项目目录
```
frontend/
├── packages/                # Monorepo 子项目
│   ├── main/                # 主工作台应用
│   ├── spark-flow/          # 可视化工作流编辑器
│   └── spark-i18n/          # 国际化支持
└── package.json             # 根项目配置
```

## 💎 核心模块
### 🖥️ main (主工作台)
- 基于 Umi 4 构建的单页应用
- 主要功能模块:
  - 工作流编辑器集成
  - Agent 管理
  - MCP 管理
  - 插件管理
  - 知识库管理
  - 模型服务配置
- 技术特点:
  - 使用 TailwindCSS 进行样式管理
  - 集成 spark-flow 作为工作流编辑器
  - 支持 Python 和 Java 两种后端模式
  - 支持国际化

### 🎨 spark-flow (工作流编辑器)
- 核心功能:
  - 可视化节点编辑
  - 节点类型系统(开始/结束/大模型/脚本/插件等)
  - 流程测试与调试
  - 版本管理
- 技术实现:
  - 基于 XYFlow (React Flow) 的流程图渲染
  - 使用 ELK.js 进行自动布局
  - 状态管理采用 Zustand
  - 支持国际化

### 🌐 spark-i18n (国际化)
- 提供多语言支持
- 自动化翻译工具链
- 支持中英文切换

## ⚡ 快速开始

### 环境要求

在启动 Web 前端服务之前，请确保以下环境已准备就绪。
- [Node.js](https://nodejs.org) >= v20
- [pnpm](https://pnpm.io) >= 9.0（包管理器）

**📦 安装 pnpm：**
```bash
npm install -g pnpm
# 或者
corepack enable
corepack prepare pnpm@latest --activate
```

> **⚠️ 重要提示：** 本项目使用 **pnpm** 作为包管理器，请勿使用 npm 或 yarn，以避免依赖冲突。

### 通过源码运行

首先，安装所有项目依赖：

```bash
pnpm install
```

然后，配置环境变量。在当前目录中创建一个名为`.env`的文件，并从`.env.example`复制内容。根据您的需求修改这些环境变量的值：

```bash
cd packages/main
```
在 macOS/Linux 中:
```bash
cp .env.example .env
```
在 Windows (PowerShell) 中:
```bash
Copy-Item .env.example .env
```

```
# 后端接口的 Host
WEB_SERVER="http://127.0.0.1:8080"

# 后端语言类型（部分功能会根据实现不同略有不同，比如 java、python）
BACK_END="java"

# 默认账号
DEFAULT_USERNAME=saa

# 默认密码
DEFAULT_PASSWORD=123456
```

最后，运行开发服务器：

```bash
cd ../..
pnpm run dev
```

用浏览器打开 [http://localhost:8000](http://localhost:8000) 查看结果。

你可以开始编辑 `main/src` 文件夹下的文件。当你编辑文件时，页面会自动更新。

## 🚢 部署

```bash
# 如果后端是 java
pnpm run build:subtree:java
```

命令完成以后，会生成 `./packages/main/dist` 目录，将其部署到你的服务器即可。

## 📦 其他特殊依赖
- @spark-ai/design
  - [https://www.npmjs.com/package/@spark-ai/design](https://www.npmjs.com/package/@spark-ai/design)
- @spark-ai/chat
  - [https://www.npmjs.com/package/@spark-ai/chat](https://www.npmjs.com/package/@spark-ai/chat)
