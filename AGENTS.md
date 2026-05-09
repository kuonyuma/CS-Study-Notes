# 🤖 OpenCode Agent Guidelines (AGENTS.md)

Welcome to the `CS-Study-Notes` repository! This file provides essential context and instructions for AI agents (like OpenCode, Copilot, or Claude) operating within this workspace.

## 📁 Repository Structure & Boundaries

This is a personal computer science learning notes and code repository, primarily focused on the Java ecosystem and fundamental algorithms.

*   **`JavaCode/`**: Contains daily practice code (Java fundamentals, OOP, Data Structures). Subdirectories might be organized by year (e.g., `2026/`).
*   **`JavaEE-Note/`**: Stores Markdown notes and technical blogs covering JavaEE, multithreading, and network principles.
*   **`LeetCode-Solutions/`**: algorithmic problem-solving records.
*   **`-Library-Management-System/`**: A comprehensive project directory applying learned concepts to a concrete system.
*   **`JavaCode/2026/StuAIWeb/`**: Contains specific frontend/web assignments (HTML/CSS/JS).

## ⚠️ Critical Rules for Agents

1.  **Strict File Encoding (UTF-8)**
    *   **The Problem:** When running PowerShell scripts (`bash` tool) to modify or replace text in files on this Windows machine, the system defaults to GBK encoding, which corrupts Chinese characters.
    *   **The Rule:** **NEVER** use `Get-Content ... | Set-Content` or regex replace via shell commands for files containing Chinese characters (which is almost all files in this repo).
    *   **The Solution:** **ALWAYS** use the dedicated `edit` or `write` tools provided by the system to modify or create files. These tools handle UTF-8 safely. If you absolutely must use PowerShell for file writing, you *must* append `-Encoding UTF8`.

2.  **Respect the Learning Context**
    *   This is a *study* repository. When generating code, prioritize readability, standard library usage, and clear logic over introducing heavy, enterprise-grade frameworks (unless the user explicitly requests Spring Boot, etc.).
    *   Keep code comments helpful and instructional.

3.  **Frontend Web Tasks**
    *   If working on HTML/CSS/JS inside directories like `StuAIWeb/src/main/resources/static/`, ensure you maintain the existing visual style (e.g., CSS variables, responsive design).
    *   Do not assume a Node.js/NPM environment exists unless a `package.json` is explicitly found in that specific subdirectory. Stick to Vanilla JS and plain CSS unless instructed otherwise.