#!/bin/bash

# Claude Code Context Monitor
# 实时监控显示context使用量

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 获取context使用量
get_context_usage() {
    claude --context 2>/dev/null | grep -E "Tokens:|Percentage:" || echo "无法获取context信息"
}

# 解析并格式化输出
format_output() {
    local output=$(get_context_usage)
    echo "$output"
}

# 显示进度条
show_progress_bar() {
    local percentage=$1
    local width=20
    local filled=$((percentage * width / 100))
    local empty=$((width - filled))

    printf "["
    printf "%${filled}s" | tr ' ' '='
    printf "%${empty}s" | tr ' ' '-'
    printf "] %d%%" "$percentage"
}

# 主循环
echo -e "${BLUE}=== Claude Code Context Monitor ===${NC}"
echo -e "${YELLOW}按 Ctrl+C 退出${NC}"
echo ""

while true; do
    # 获取当前时间
    current_time=$(date "+%H:%M:%S")

    # 尝试获取context信息
    context_info=$(claude --context 2>&1)

    # 解析tokens和percentage
    tokens=$(echo "$context_info" | grep -oP 'Tokens:\s*\K[0-9.]+[kKmM]?' | head -1)
    percentage=$(echo "$context_info" | grep -oP 'Percentage:\s*\K[0-9.]+' | head -1)

    # 清空当前行并显示
    printf "\r${GREEN}[%s]${NC} " "$current_time"

    if [ -n "$tokens" ] && [ -n "$percentage" ]; then
        printf "Tokens: ${BLUE}%s${NC} | Usage: " "$tokens"

        # 根据使用量显示不同颜色
        if (( $(echo "$percentage > 80" | bc -l 2>/dev/null || echo 0) )); then
            printf "${RED}"
        elif (( $(echo "$percentage > 50" | bc -l 2>/dev/null || echo 0) )); then
            printf "${YELLOW}"
        else
            printf "${GREEN}"
        fi

        show_progress_bar "${percentage%.*}"
        printf "${NC}"
    else
        printf "${YELLOW}等待更新...${NC}"
    fi

    sleep 5
done
