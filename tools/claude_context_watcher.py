#!/usr/bin/env python3
"""
Claude Context Monitor - 后台持续监控context使用量
使用方式: python3 claude_context_watcher.py
输出到: /tmp/claude-context.log
"""

import os
import time
import subprocess
import signal
import sys
from datetime import datetime

LOG_FILE = "/tmp/claude-context.log"

def get_context():
    """获取context使用量"""
    env = os.environ.copy()
    env["CLAUDECODE"] = ""

    try:
        result = subprocess.run(
            ["claude", "--context"],
            capture_output=True,
            text=True,
            env=env,
            timeout=10
        )
        return result.stdout + result.stderr
    except Exception as e:
        return f"Error: {e}"

def parse_context(output):
    """解析context输出"""
    lines = output.strip().split('\n')
    data = {}

    for line in lines:
        if 'Tokens:' in line:
            data['tokens'] = line.split('Tokens:')[1].strip().split()[0]
        if 'Percentage:' in line:
            data['percentage'] = line.split('Percentage:')[1].strip().split()[0]

    return data

def format_bar(percentage):
    """格式化进度条"""
    try:
        pct = float(percentage.replace('%', ''))
        width = 20
        filled = int(pct / 100 * width)
        bar = '█' * filled + '░' * (width - filled)

        # 颜色代码
        if pct > 80:
            color = '\033[0;31m'  # 红
        elif pct > 50:
            color = '\033[1;33m'  # 黄
        else:
            color = '\033[0;32m'  # 绿

        return f"{color}{bar}\033[0m {pct:.1f}%"
    except:
        return percentage

def main():
    print("Claude Context Monitor 开始运行...")
    print(f"日志文件: {LOG_FILE}")
    print("按 Ctrl+C 停止")
    print()

    # 信号处理
    def signal_handler(sig, frame):
        print("\n\n监控已停止")
        sys.exit(0)

    signal.signal(signal.SIGINT, signal_handler)

    while True:
        timestamp = datetime.now().strftime("%H:%M:%S")
        output = get_context()
        data = parse_context(output)

        if data:
            tokens = data.get('tokens', 'N/A')
            percentage = data.get('percentage', 'N/A')
            bar = format_bar(percentage)

            log_line = f"[{timestamp}] Tokens: {tokens} | Usage: {bar}"

            # 写入文件
            with open(LOG_FILE, 'w') as f:
                f.write(log_line + '\n')
                f.write('─' * 50 + '\n')
                f.write(output)

            # 控制台输出（ANSI转义）
            print(f"\r\033[K[{timestamp}] Tokens: {tokens} | Usage: {bar}", end='', flush=True)
        else:
            with open(LOG_FILE, 'w') as f:
                f.write(f"[{timestamp}] 无法获取context信息\n")
            print(f"\r\033[K[{timestamp}] 等待...", end='', flush=True)

        time.sleep(5)

if __name__ == "__main__":
    main()
