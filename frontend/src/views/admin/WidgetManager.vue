<template>
  <div class="widget-manager">
    <div class="header">
      <h1>Widget Manager</h1>
      <button @click="showCreateModal = true" class="btn-primary">
        + Create Widget
      </button>
    </div>

    <div v-if="loading" class="loading">Loading widgets...</div>
    <div v-else-if="error" class="error">{{ error }}</div>

    <div v-else class="widgets-list">
      <div v-if="widgets.length === 0" class="empty">
        No widgets created yet. Click "Create Widget" to get started.
      </div>

      <div v-else class="widget-items">
        <div
          v-for="widget in sortedWidgets"
          :key="widget.id"
          class="widget-item"
          :class="{ inactive: !widget.active }"
        >
          <div class="widget-preview">
            <img :src="widget.imageUrl" :alt="widget.title" />
          </div>
          <div class="widget-info">
            <h3>{{ widget.title }}</h3>
            <p>{{ widget.shortDescription }}</p>
            <div class="widget-meta">
              <span class="badge" :class="widget.active ? 'active' : 'inactive'">
                {{ widget.active ? 'Active' : 'Inactive' }}
              </span>
              <span class="order">Order: {{ widget.displayOrder }}</span>
            </div>
          </div>
          <div class="widget-actions">
            <button @click="editWidget(widget)" class="btn-secondary">Edit</button>
            <button @click="deleteWidgetConfirm(widget)" class="btn-danger">Delete</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>{{ showEditModal ? 'Edit Widget' : 'Create Widget' }}</h2>
          <button @click="closeModal" class="close-btn">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="saveWidget">
            <div class="form-group">
              <label>Title *</label>
              <input
                v-model="formData.title"
                type="text"
                required
                placeholder="e.g., Android Development"
              />
            </div>

            <div class="form-group">
              <label>Short Description *</label>
              <textarea
                v-model="formData.shortDescription"
                required
                rows="3"
                placeholder="Brief description for the widget (max 200 chars)"
                maxlength="200"
              ></textarea>
              <small>{{ formData.shortDescription?.length || 0 }}/200</small>
            </div>

            <div class="form-group">
              <label>Widget Image *</label>
              <div class="image-upload">
                <div v-if="formData.imageUrl" class="image-preview">
                  <img :src="formData.imageUrl" alt="Preview" />
                  <button type="button" @click="removeImage" class="remove-image">
                    &times;
                  </button>
                </div>
                <div v-else class="image-upload-area">
                  <input
                    type="file"
                    ref="fileInput"
                    @change="handleFileSelect"
                    accept="image/*"
                    style="display: none"
                  />
                  <button type="button" @click="$refs.fileInput.click()" class="btn-secondary">
                    Choose Image
                  </button>
                  <p>Max size: 5MB | Formats: JPG, PNG, GIF</p>
                </div>
              </div>
              <div v-if="uploading" class="uploading">Uploading image...</div>
            </div>

            <div class="form-group">
              <label>Link to Blog Post (Optional)</label>
              <select v-model="formData.postId">
                <option :value="null">No blog post</option>
                <option v-for="post in posts" :key="post.id" :value="post.id">
                  {{ post.title }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Display Order</label>
              <input
                v-model.number="formData.displayOrder"
                type="number"
                min="0"
                placeholder="0"
              />
              <small>Lower numbers appear first</small>
            </div>

            <div class="form-group">
              <label class="checkbox-label">
                <input v-model="formData.active" type="checkbox" />
                <span>Active (show on home page)</span>
              </label>
            </div>

            <details class="advanced-options">
              <summary>Advanced Options (Widget Config)</summary>

              <div class="form-group">
                <label>Layout Type</label>
                <select v-model="formData.config.layout">
                  <option value="">Default</option>
                  <option value="card">Card</option>
                  <option value="banner">Banner</option>
                  <option value="featured">Featured</option>
                </select>
              </div>

              <div class="form-group">
                <label>Background Color</label>
                <input v-model="formData.config.backgroundColor" type="color" />
              </div>

              <div class="form-group">
                <label>Text Color</label>
                <input v-model="formData.config.textColor" type="color" />
              </div>

              <div class="form-group">
                <label>Custom Link URL</label>
                <input
                  v-model="formData.config.linkUrl"
                  type="url"
                  placeholder="https://example.com or /about"
                />
                <small>Overrides blog post link if provided</small>
              </div>

              <div class="form-group">
                <label>API Endpoint (for dynamic content)</label>
                <input
                  v-model="formData.config.apiEndpoint"
                  type="text"
                  placeholder="/api/custom-data"
                />
              </div>
            </details>

            <div class="modal-footer">
              <button type="button" @click="closeModal" class="btn-secondary">Cancel</button>
              <button type="submit" class="btn-primary" :disabled="!isFormValid || uploading">
                {{ showEditModal ? 'Update' : 'Create' }} Widget
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useWidgetStore } from '@/stores/widget'
import { useBlogStore } from '@/stores/blog'

const widgetStore = useWidgetStore()
const blogStore = useBlogStore()

const showCreateModal = ref(false)
const showEditModal = ref(false)
const currentWidgetId = ref(null)
const uploading = ref(false)
const fileInput = ref(null)

const formData = ref({
  title: '',
  shortDescription: '',
  imageUrl: '',
  postId: null,
  displayOrder: 0,
  active: true,
  config: {
    layout: '',
    backgroundColor: '',
    textColor: '',
    apiEndpoint: '',
    linkUrl: ''
  }
})

const loading = computed(() => widgetStore.loading)
const error = computed(() => widgetStore.error)
const widgets = computed(() => widgetStore.widgets)
const sortedWidgets = computed(() => widgetStore.sortedWidgets)
const posts = computed(() => blogStore.posts)

const isFormValid = computed(() => {
  return formData.value.title &&
    formData.value.shortDescription &&
    formData.value.imageUrl
})

onMounted(async () => {
  await widgetStore.fetchWidgets()
  await blogStore.fetchPosts()
})

const editWidget = (widget) => {
  currentWidgetId.value = widget.id
  formData.value = {
    title: widget.title,
    shortDescription: widget.shortDescription,
    imageUrl: widget.imageUrl,
    postId: widget.postId || null,
    displayOrder: widget.displayOrder,
    active: widget.active,
    config: {
      layout: widget.config?.layout || '',
      backgroundColor: widget.config?.backgroundColor || '',
      textColor: widget.config?.textColor || '',
      apiEndpoint: widget.config?.apiEndpoint || '',
      linkUrl: widget.config?.linkUrl || ''
    }
  }
  showEditModal.value = true
}

const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // Validate file type
  if (!file.type.startsWith('image/')) {
    alert('Please select an image file')
    return
  }

  // Validate file size (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('File size must be less than 5MB')
    return
  }

  uploading.value = true
  try {
    const imageUrl = await widgetStore.uploadImage(file)
    formData.value.imageUrl = imageUrl
  } catch (error) {
    alert('Failed to upload image: ' + error.message)
  } finally {
    uploading.value = false
  }
}

const removeImage = async () => {
  if (formData.value.imageUrl) {
    try {
      await widgetStore.deleteImage(formData.value.imageUrl)
    } catch (error) {
      console.error('Failed to delete image:', error)
    }
    formData.value.imageUrl = ''
  }
}

const saveWidget = async () => {
  try {
    if (showEditModal.value) {
      await widgetStore.updateWidget(currentWidgetId.value, formData.value)
    } else {
      await widgetStore.createWidget(formData.value)
    }
    closeModal()
  } catch (error) {
    alert('Failed to save widget: ' + error.message)
  }
}

const deleteWidgetConfirm = async (widget) => {
  if (confirm(`Are you sure you want to delete "${widget.title}"?`)) {
    try {
      await widgetStore.deleteWidget(widget.id)
    } catch (error) {
      alert('Failed to delete widget: ' + error.message)
    }
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  currentWidgetId.value = null
  formData.value = {
    title: '',
    shortDescription: '',
    imageUrl: '',
    postId: null,
    displayOrder: 0,
    active: true,
    config: {
      layout: '',
      backgroundColor: '',
      textColor: '',
      apiEndpoint: '',
      linkUrl: ''
    }
  }
}
</script>

<style scoped>
.widget-manager {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.loading, .error, .empty {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.error {
  color: #ef4444;
}

.widget-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.widget-item {
  display: flex;
  gap: 1.5rem;
  padding: 1.5rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s;
}

.widget-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.widget-item.inactive {
  opacity: 0.6;
}

.widget-preview {
  flex-shrink: 0;
  width: 150px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  background: #f0f0f0;
}

.widget-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.widget-info {
  flex: 1;
}

.widget-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a1a1a;
}

.widget-info p {
  margin: 0 0 0.75rem 0;
  color: #666;
  line-height: 1.5;
}

.widget-meta {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.badge.active {
  background: #dcfce7;
  color: #16a34a;
}

.badge.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.order {
  font-size: 0.9rem;
  color: #666;
}

.widget-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  justify-content: center;
}

.btn-primary, .btn-secondary, .btn-danger {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-primary:disabled {
  background: #93c5fd;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #1a1a1a;
}

.btn-secondary:hover {
  background: #e5e7eb;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.btn-danger:hover {
  background: #dc2626;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  line-height: 1;
  cursor: pointer;
  color: #9ca3af;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #1a1a1a;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #1a1a1a;
}

.form-group input[type="text"],
.form-group input[type="url"],
.form-group input[type="number"],
.form-group input[type="color"],
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group input[type="color"] {
  height: 40px;
  cursor: pointer;
}

.form-group small {
  display: block;
  margin-top: 0.25rem;
  color: #6b7280;
  font-size: 0.85rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
  cursor: pointer;
}

.image-upload {
  margin-top: 0.5rem;
}

.image-preview {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #e5e7eb;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  transition: background 0.2s;
}

.remove-image:hover {
  background: rgba(0, 0, 0, 0.9);
}

.image-upload-area {
  text-align: center;
  padding: 2rem;
  border: 2px dashed #d1d5db;
  border-radius: 8px;
}

.image-upload-area p {
  margin: 0.5rem 0 0 0;
  font-size: 0.85rem;
  color: #6b7280;
}

.uploading {
  color: #3b82f6;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.advanced-options {
  margin-top: 1rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
}

.advanced-options summary {
  cursor: pointer;
  font-weight: 500;
  color: #3b82f6;
  margin-bottom: 1rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .widget-manager {
    padding: 1rem;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .widget-item {
    flex-direction: column;
  }

  .widget-preview {
    width: 100%;
    height: 150px;
  }

  .widget-actions {
    flex-direction: row;
  }
}
</style>
