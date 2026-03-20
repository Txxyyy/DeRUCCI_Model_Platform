<template>
  <div class="empty-state">
    <div v-if="icon" class="empty-state-icon">{{ icon }}</div>
    <div class="empty-state-title">{{ title }}</div>
    <div v-if="desc" class="empty-state-desc">{{ desc }}</div>
    <el-button v-if="actionLabel" type="primary" @click="$emit('action')">
      {{ actionLabel }}
    </el-button>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  icon:        { type: String, default: '' },
  title:       { type: String, default: '暂无数据' },
  desc:        { type: String, default: '' },
  actionLabel: { type: String, default: '' }
})
defineEmits(['action'])
</script>
