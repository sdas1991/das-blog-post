<template>
  <div class="widget-grid-container">
    <div v-if="title" class="section-header">
      <h2>{{ title }}</h2>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>

    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Loading widgets...</p>
    </div>

    <div v-else-if="error" class="error">
      <p>{{ error }}</p>
    </div>

    <div v-else-if="widgets.length === 0" class="empty">
      <p>No widgets available at the moment.</p>
    </div>

    <div v-else class="widget-grid">
      <WidgetCard
        v-for="widget in widgets"
        :key="widget.id"
        :widget="widget"
      />
    </div>
  </div>
</template>

<script setup>
import WidgetCard from './WidgetCard.vue'

defineProps({
  widgets: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  },
  title: {
    type: String,
    default: null
  },
  subtitle: {
    type: String,
    default: null
  }
})
</script>

<style scoped>
.widget-grid-container {
  width: 100%;
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-header h2 {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 0.5rem;
}

.section-header p {
  font-size: 1.1rem;
  color: #666;
}

.widget-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.loading,
.error,
.empty {
  text-align: center;
  padding: 3rem 1rem;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error p {
  color: #ef4444;
  font-size: 1rem;
}

.empty p {
  color: #666;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .section-header h2 {
    font-size: 2rem;
  }

  .widget-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .widget-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1025px) {
  .widget-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
