#!/bin/sh

# 独立脚本，用于检查项目中是否包含敏感信息

# 定义要检查的敏感信息模式
SENSITIVE_PATTERNS=("password=" "secret=" "key=" "token=" "private_key=" "access_key=" "secret_key=" "username=")
# 定义要检查的文件类型
FILE_TYPES=("*.java" "*.xml" "*.yml" "*.properties" "*.md" "*.html" "*.js" "*.ts" "*.json")
# 定义要跳过的目录
SKIP_DIRS=(".git" "target" "build" "node_modules" ".idea" ".vscode")

# 标记是否发现敏感信息
SENSITIVE_FOUND=0

# 获取脚本所在目录
SCRIPT_DIR=$(dirname "$0")

# 遍历所有文件类型
for FILE_TYPE in "${FILE_TYPES[@]}"; do
  # 构建查找命令，排除跳过的目录
  FIND_CMD="find $SCRIPT_DIR -type f -name \"$FILE_TYPE\""
  
  for DIR in "${SKIP_DIRS[@]}"; do
    FIND_CMD="$FIND_CMD -not -path \"*/$DIR/*\""
  done
  
  # 执行查找并检查文件 - 使用管道代替双重重定向以提高兼容性
  eval "$FIND_CMD" | while IFS= read -r FILE; do
    # 遍历所有敏感信息模式
    for PATTERN in "${SENSITIVE_PATTERNS[@]}"; do
      if grep -q "$PATTERN" "$FILE"; then
        SENSITIVE_FOUND=1
        echo "\033[31m警告: 检测到敏感信息\033[0m"
        echo "\033[33m文件: \033[31m$FILE\033[0m"
        echo "\033[33m模式: \033[31m$PATTERN\033[0m"
        echo ""
      fi
    done
  done
done

# 特别检查数据库配置文件
DB_CONFIG_FILES=$(find "$SCRIPT_DIR" -type f -name "application*.yml" -o -name "application*.properties")
for FILE in $DB_CONFIG_FILES; do
  if grep -q -E "password:\s*[^\s#]*\b|password=\S*|url:\s*jdbc:\S*|username:\s*\S*|username=\S*" "$FILE"; then
    SENSITIVE_FOUND=1
    echo "\033[31m警告: 检测到数据库配置文件包含敏感信息\033[0m"
    echo "\033[33m文件: \033[31m$FILE\033[0m"
    echo "\033[33m建议: 使用环境变量或配置中心管理敏感信息\033[0m"
    echo ""
  fi
done

# 输出结果
if [ $SENSITIVE_FOUND -eq 1 ]; then
  echo "\033[31m检查完成: 发现敏感信息。请检查并移除这些信息。\033[0m"
  exit 1
else
  echo "\033[32m检查完成: 未发现敏感信息。\033[0m"
  exit 0
fi