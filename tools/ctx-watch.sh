#!/bin/bash
# Claude Context Watcher - 在Claude Code中实时监控
# 使用方式: source ctx-watch.sh

LOG_FILE="/tmp/claude-context.log"

# 检查监控脚本是否在运行
if ! pgrep -f "claude_context_watcher.py" > /dev/null; then
    echo "启动后台监控..."
    cd /Users/tanxiaoyi/AI_Project/DeRUCCI_Model_Platform/tools
    python3 claude_context_watcher.py &
    sleep 2
fi

# 清理之前的函数
unset ctx-watch 2>/dev/null

# 创建监控函数
ctx-watch() {
    if [ -f "$LOG_FILE" ]; then
        clear
        echo "╔════════════════════════════════════════╗"
        echo "║   Claude Code Context Monitor          ║"
        echo "║   按 Ctrl+C 退出监控                  ║"
        echo "╚════════════════════════════════════════╝"
        echo ""
        tail -f "$LOG_FILE"
    else
        echo "监控未启动，请先运行: python3 claude_context_watcher.py"
    fi
}

echo "ctx-watch 已就绪！"
echo "运行 'ctx-watch' 开始监控"
echo ""
