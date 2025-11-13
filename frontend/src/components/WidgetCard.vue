<template>
  <div class="widget-card" :style="widgetStyle" @click="handleClick">
    <div class="widget-image">
      <img :src="widget.imageUrl" :alt="widget.title" />
    </div>
    <div class="widget-content">
      <h3 class="widget-title">{{ widget.title }}</h3>
      <p class="widget-description">{{ widget.shortDescription }}</p>
      <div class="widget-action">
        <span class="read-more">Read More →</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  widget: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const widgetStyle = computed(() => {
  const config = props.widget.config || {}
  return {
    backgroundColor: config.backgroundColor || '#ffffff',
    color: config.textColor || '#333333'
  }
})

const handleClick = () => {
  if (props.widget.config?.linkUrl) {
    if (props.widget.config.linkUrl.startsWith('http')) {
      window.open(props.widget.config.linkUrl, '_blank')
    } else {
      router.push(props.widget.config.linkUrl)
    }
  } else if (props.widget.postId) {
    router.push(`/blog/${props.widget.postId}`)
  }
}
</script>

<style scoped>
.widget-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.widget-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
}

.widget-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f0f0f0;
}

.widget-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.widget-card:hover .widget-image img {
  transform: scale(1.05);
}

.widget-content {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.widget-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin: 0 0 0.75rem 0;
  color: inherit;
}

.widget-description {
  font-size: 0.95rem;
  line-height: 1.6;
  margin: 0 0 1rem 0;
  flex: 1;
  color: inherit;
  opacity: 0.85;
}

.widget-action {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.read-more {
  font-size: 0.9rem;
  font-weight: 500;
  color: #3b82f6;
  transition: color 0.2s ease;
}

.widget-card:hover .read-more {
  color: #2563eb;
}

@media (max-width: 768px) {
  .widget-image {
    height: 180px;
  }

  .widget-content {
    padding: 1rem;
  }

  .widget-title {
    font-size: 1.1rem;
  }

  .widget-description {
    font-size: 0.9rem;
  }
}
</style>
