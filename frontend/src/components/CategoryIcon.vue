<template>
  <svg
    :width="size"
    :height="Math.round(size * 54 / 80)"
    viewBox="0 0 80 54"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    :style="{ color: color, display: 'block' }"
  >
    <!-- 智能床垫 -->
    <template v-if="category === '智能床垫'">
      <!-- 床垫主体 -->
      <rect x="4" y="16" width="72" height="26" rx="5" fill="currentColor" opacity="0.1" stroke="currentColor" stroke-width="2"/>
      <!-- 内部舒适层纹理 -->
      <line x1="10" y1="24" x2="70" y2="24" stroke="currentColor" stroke-width="1" opacity="0.45"/>
      <line x1="10" y1="31" x2="70" y2="31" stroke="currentColor" stroke-width="1" opacity="0.45"/>
      <!-- 床脚 -->
      <path d="M12 42 L12 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
      <path d="M68 42 L68 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
      <!-- IoT传感器 -->
      <circle cx="63" cy="21" r="2.5" fill="currentColor" opacity="0.85"/>
      <circle cx="63" cy="21" r="5" stroke="currentColor" stroke-width="1.2" opacity="0.35"/>
    </template>

    <!-- 电动床 -->
    <template v-else-if="category === '电动床'">
      <!-- 床架 -->
      <rect x="4" y="30" width="72" height="14" rx="3" fill="currentColor" opacity="0.1" stroke="currentColor" stroke-width="2"/>
      <!-- 可调节头部区域 (倾斜抬起) -->
      <path d="M6 30 L6 12 Q6 6 13 6 L36 6 Q42 6 42 12 L42 30Z" fill="currentColor" opacity="0.12" stroke="currentColor" stroke-width="2"/>
      <!-- 铰链关节点 -->
      <circle cx="24" cy="30" r="3" stroke="currentColor" stroke-width="1.8"/>
      <!-- 电动调节控制器 -->
      <rect x="55" y="33" width="14" height="8" rx="2" stroke="currentColor" stroke-width="1.5"/>
      <circle cx="62" cy="37" r="1.8" fill="currentColor" opacity="0.7"/>
      <!-- 床脚 -->
      <path d="M10 44 L10 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
      <path d="M70 44 L70 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
    </template>

    <!-- 智能枕头 -->
    <template v-else-if="category === '智能枕头'">
      <!-- 枕头外形 -->
      <rect x="4" y="10" width="72" height="36" rx="15" fill="currentColor" opacity="0.1" stroke="currentColor" stroke-width="2"/>
      <!-- 内衬轮廓 -->
      <rect x="11" y="16" width="58" height="24" rx="11" stroke="currentColor" stroke-width="1" opacity="0.4"/>
      <!-- 中缝线 -->
      <path d="M4 28 Q40 34 76 28" stroke="currentColor" stroke-width="1" opacity="0.4" fill="none"/>
      <!-- IoT传感器 -->
      <circle cx="40" cy="22" r="3" fill="currentColor" opacity="0.75"/>
      <circle cx="40" cy="22" r="6" stroke="currentColor" stroke-width="1.2" opacity="0.3"/>
    </template>

    <!-- 默认图标 -->
    <template v-else>
      <rect x="8" y="8" width="64" height="40" rx="5" stroke="currentColor" stroke-width="2"/>
      <path d="M18 22h44M18 30h44M18 38h28" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/>
      <circle cx="56" cy="38" r="3" fill="currentColor" opacity="0.5"/>
    </template>
  </svg>
</template>

<script setup>
defineProps({
  category: {
    type: String,
    default: ''
  },
  size: {
    type: Number,
    default: 64
  },
  color: {
    type: String,
    default: 'currentColor'
  }
})
</script>
