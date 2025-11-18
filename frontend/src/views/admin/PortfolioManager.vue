<template>
  <div class="portfolio-manager">
    <h1>Portfolio Manager</h1>

    <div class="manager-actions">
      <button @click="showProjectForm = true" class="btn btn-primary">Add New Project</button>
      <router-link to="/admin" class="btn btn-secondary">Back to Dashboard</router-link>
    </div>

    <section v-if="showProjectForm" class="project-form-section card">
      <h2>{{ editingProject ? 'Edit Project' : 'New Project' }}</h2>
      <form @submit.prevent="handleSubmitProject">
        <div class="form-group">
          <label>Title *</label>
          <input v-model="projectForm.title" type="text" required />
        </div>

        <div class="form-group">
          <label>Summary *</label>
          <input v-model="projectForm.summary" type="text" placeholder="Brief one-line description" required />
          <small>Short description shown in project cards</small>
        </div>

        <div class="form-group">
          <label>Description *</label>
          <textarea v-model="projectForm.description" rows="6" required></textarea>
          <small>Full project description</small>
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input v-model="projectForm.featured" type="checkbox" />
            <span>Featured Project</span>
          </label>
          <small>Featured projects appear at the top of your portfolio</small>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Main Image URL</label>
            <input v-model="projectForm.imageUrl" type="url" placeholder="https://..." />
            <small>Primary project thumbnail</small>
          </div>

          <div class="form-group">
            <label>Live URL</label>
            <input v-model="projectForm.liveUrl" type="url" placeholder="https://..." />
          </div>
        </div>

        <div class="form-group">
          <label>GitHub Repository URL</label>
          <input v-model="projectForm.githubUrl" type="url" placeholder="https://github.com/..." />
          <small>Link to your GitHub repository</small>
        </div>

        <div class="form-group">
          <label>Project Gallery Images</label>
          <div class="gallery-manager">
            <div v-for="(img, index) in projectForm.projectImages" :key="index" class="gallery-item">
              <input v-model="projectForm.projectImages[index]" type="url" placeholder="https://..." />
              <button type="button" @click="removeGalleryImage(index)" class="btn-small btn-danger">✕</button>
            </div>
            <button type="button" @click="addGalleryImage" class="btn btn-secondary">+ Add Image</button>
          </div>
          <small>Additional project screenshots or images</small>
        </div>

        <div class="form-group">
          <label>Technologies</label>
          <input v-model="technologiesInput" type="text" placeholder="React, Node.js, MongoDB, Docker" />
          <small>Comma-separated list of technologies used</small>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? 'Saving...' : 'Save Project' }}
          </button>
          <button type="button" @click="cancelProjectForm" class="btn btn-secondary">
            Cancel
          </button>
        </div>

        <p v-if="error" class="error-message">{{ error }}</p>
      </form>
    </section>

    <section class="projects-list">
      <div class="section-header">
        <h2>Your Projects ({{ projects.length }})</h2>
        <div class="filter-options">
          <select v-model="sortBy" class="sort-select">
            <option value="newest">Newest First</option>
            <option value="oldest">Oldest First</option>
            <option value="title">Title A-Z</option>
            <option value="featured">Featured First</option>
          </select>
        </div>
      </div>

      <div v-if="loading && !showProjectForm" class="loading">Loading...</div>
      <div v-else-if="sortedProjects.length" class="grid grid-2">
        <div v-for="project in sortedProjects" :key="project.id" class="card project-card">
          <div class="project-header">
            <span v-if="project.featured" class="featured-badge">⭐ Featured</span>
          </div>
          <img v-if="project.imageUrl" :src="project.imageUrl" :alt="project.title" class="project-image" />
          <div class="project-content">
            <h3>{{ project.title }}</h3>
            <p class="project-summary">{{ project.summary || 'No summary provided' }}</p>
            <p class="project-desc">{{ truncateText(project.description, 150) }}</p>

            <div v-if="project.projectImages && project.projectImages.length" class="gallery-preview">
              <span class="gallery-count">📸 {{ project.projectImages.length }} images</span>
            </div>

            <div class="project-links">
              <a v-if="project.githubUrl" :href="project.githubUrl" target="_blank" class="link-badge github">
                GitHub
              </a>
              <a v-if="project.liveUrl" :href="project.liveUrl" target="_blank" class="link-badge live">
                Live Demo
              </a>
            </div>

            <div v-if="project.technologies && project.technologies.length" class="project-tech">
              <span v-for="tech in project.technologies" :key="tech" class="tech-tag">{{ tech }}</span>
            </div>
          </div>

          <div class="card-actions">
            <button @click="handleEditProject(project)" class="btn-small btn-primary">Edit</button>
            <button @click="handleDeleteProject(project.id)" class="btn-small btn-danger">Delete</button>
          </div>
        </div>
      </div>
      <p v-else>No projects yet. Click "Add New Project" to get started!</p>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { portfolioService } from '../../services/api'

const projects = ref([])
const showProjectForm = ref(false)
const editingProject = ref(null)
const loading = ref(false)
const error = ref('')
const sortBy = ref('featured')

const projectForm = ref({
  title: '',
  description: '',
  summary: '',
  imageUrl: '',
  liveUrl: '',
  githubUrl: '',
  featured: false,
  technologies: [],
  projectImages: []
})

const technologiesInput = ref('')

const sortedProjects = computed(() => {
  const sorted = [...projects.value]

  switch (sortBy.value) {
    case 'newest':
      return sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    case 'oldest':
      return sorted.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
    case 'title':
      return sorted.sort((a, b) => a.title.localeCompare(b.title))
    case 'featured':
      return sorted.sort((a, b) => (b.featured ? 1 : 0) - (a.featured ? 1 : 0))
    default:
      return sorted
  }
})

onMounted(async () => {
  await loadProjects()
})

const loadProjects = async () => {
  loading.value = true
  try {
    projects.value = await portfolioService.getProjects()
  } catch (err) {
    error.value = 'Failed to load projects'
  } finally {
    loading.value = false
  }
}

const handleSubmitProject = async () => {
  loading.value = true
  error.value = ''

  projectForm.value.technologies = technologiesInput.value
    .split(',')
    .map(tech => tech.trim())
    .filter(tech => tech.length > 0)

  // Filter out empty image URLs
  projectForm.value.projectImages = projectForm.value.projectImages.filter(img => img && img.trim())

  try {
    if (editingProject.value) {
      await portfolioService.updateProject(editingProject.value.id, projectForm.value)
    } else {
      await portfolioService.createProject(projectForm.value)
    }

    await loadProjects()
    cancelProjectForm()
  } catch (err) {
    error.value = 'Failed to save project'
    console.error('Save error:', err)
  } finally {
    loading.value = false
  }
}

const handleEditProject = (project) => {
  editingProject.value = project
  projectForm.value = {
    title: project.title,
    description: project.description,
    summary: project.summary || '',
    imageUrl: project.imageUrl || '',
    liveUrl: project.liveUrl || '',
    githubUrl: project.githubUrl || '',
    featured: project.featured || false,
    technologies: project.technologies || [],
    projectImages: project.projectImages ? [...project.projectImages] : []
  }
  technologiesInput.value = project.technologies ? project.technologies.join(', ') : ''
  showProjectForm.value = true
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleDeleteProject = async (id) => {
  if (!confirm('Are you sure you want to delete this project?')) {
    return
  }

  try {
    await portfolioService.deleteProject(id)
    projects.value = projects.value.filter(p => p.id !== id)
  } catch (err) {
    alert('Failed to delete project')
  }
}

const cancelProjectForm = () => {
  showProjectForm.value = false
  editingProject.value = null
  projectForm.value = {
    title: '',
    description: '',
    summary: '',
    imageUrl: '',
    liveUrl: '',
    githubUrl: '',
    featured: false,
    technologies: [],
    projectImages: []
  }
  technologiesInput.value = ''
  error.value = ''
}

const addGalleryImage = () => {
  projectForm.value.projectImages.push('')
}

const removeGalleryImage = (index) => {
  projectForm.value.projectImages.splice(index, 1)
}

const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}
</script>

<style scoped>
.portfolio-manager h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

.manager-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 3rem;
}

.project-form-section {
  margin-bottom: 3rem;
}

.project-form-section h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
  color: #2c5282;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.form-group small {
  display: block;
  margin-top: 0.25rem;
  color: #666;
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

.checkbox-label span {
  font-weight: 600;
  color: #333;
}

.gallery-manager {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.gallery-item {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.gallery-item input {
  flex: 1;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  font-size: 1.75rem;
  margin: 0;
}

.filter-options {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.sort-select {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;
}

.projects-list h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
}

.project-card {
  display: flex;
  flex-direction: column;
  position: relative;
}

.project-header {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 10;
}

.featured-badge {
  background: #ffd700;
  color: #333;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.project-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.project-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.project-card h3 {
  font-size: 1.25rem;
  margin: 0;
  color: #2c5282;
}

.project-summary {
  color: #555;
  font-weight: 500;
  font-style: italic;
  margin: 0;
}

.project-desc {
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.gallery-preview {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.gallery-count {
  font-size: 0.85rem;
  color: #666;
  padding: 0.25rem 0.5rem;
  background: #f0f0f0;
  border-radius: 4px;
}

.project-links {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.link-badge {
  padding: 0.35rem 0.75rem;
  border-radius: 6px;
  font-size: 0.85rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.link-badge.github {
  background: #333;
  color: white;
}

.link-badge.github:hover {
  background: #000;
}

.link-badge.live {
  background: #42b983;
  color: white;
}

.link-badge.live:hover {
  background: #35956e;
}

.project-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tech-tag {
  background: #2c5282;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: auto;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.btn-small {
  padding: 0.5rem 0.75rem;
  font-size: 0.85rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-small.btn-primary {
  background: #2c5282;
  color: white;
}

.btn-small.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-small:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.error-message {
  color: #dc3545;
  margin-top: 1rem;
  padding: 0.75rem;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>
