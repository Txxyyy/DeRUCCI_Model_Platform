# Claude Context Monitor - 实时监控

## 方案说明

由于Claude Code不允许嵌套运行，需要用两个终端配合工作：

### 步骤1: 打开一个新的终端窗口（终端A）

运行监控脚本：
```bash
cd /Users/tanxiaoyi/AI_Project/DeRUCCI_Model_Platform/tools
python3 claude_context_watcher.py
```

这个终端会持续显示context使用量，每5秒刷新。

### 步骤2: 在Claude Code中查看

在Claude Code中运行以下命令查看实时数据：

```bash
tail -f /tmp/claude-context.log
```

## 快速使用

### 终端A - 启动监控
```bash
cd ~/AI_Project/DeRUCCI_Model_Platform/tools
python3 claude_context_watcher.py
```

### Claude Code - 查看实时数据
```bash
tail -f /tmp/claude-context.log
```

## 输出示例

```
[14:30:25] Tokens: 113.2k | Usage: █████████████░░░░░░ 57%
```

## 停止监控

在运行监控脚本的终端按 `Ctrl+C` 即可停止。
