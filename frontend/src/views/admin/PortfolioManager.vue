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
          <label>Description *</label>
          <textarea v-model="projectForm.description" rows="4" required></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Image URL</label>
            <input v-model="projectForm.imageUrl" type="url" />
          </div>

          <div class="form-group">
            <label>Live URL</label>
            <input v-model="projectForm.liveUrl" type="url" />
          </div>
        </div>

        <div class="form-group">
          <label>GitHub URL</label>
          <input v-model="projectForm.githubUrl" type="url" />
        </div>

        <div class="form-group">
          <label>Technologies</label>
          <input v-model="technologiesInput" type="text" placeholder="React, Node.js, MongoDB" />
          <small>Comma-separated list</small>
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
      <h2>Your Projects</h2>
      <div v-if="loading && !showProjectForm" class="loading">Loading...</div>
      <div v-else-if="projects.length" class="grid grid-2">
        <div v-for="project in projects" :key="project.id" class="card project-card">
          <img v-if="project.imageUrl" :src="project.imageUrl" :alt="project.title" class="project-image" />
          <h3>{{ project.title }}</h3>
          <p>{{ project.description }}</p>
          <div v-if="project.technologies" class="project-tech">
            <span v-for="tech in project.technologies" :key="tech" class="tech-tag">{{ tech }}</span>
          </div>
          <div class="card-actions">
            <button @click="handleEditProject(project)" class="btn-small btn-primary">Edit</button>
            <button @click="handleDeleteProject(project.id)" class="btn-small btn-danger">Delete</button>
          </div>
        </div>
      </div>
      <p v-else>No projects yet.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { portfolioService } from '../../services/api'

const projects = ref([])
const showProjectForm = ref(false)
const editingProject = ref(null)
const loading = ref(false)
const error = ref('')

const projectForm = ref({
  title: '',
  description: '',
  imageUrl: '',
  liveUrl: '',
  githubUrl: '',
  technologies: []
})

const technologiesInput = ref('')

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
  } finally {
    loading.value = false
  }
}

const handleEditProject = (project) => {
  editingProject.value = project
  projectForm.value = {
    title: project.title,
    description: project.description,
    imageUrl: project.imageUrl || '',
    liveUrl: project.liveUrl || '',
    githubUrl: project.githubUrl || '',
    technologies: project.technologies || []
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
    imageUrl: '',
    liveUrl: '',
    githubUrl: '',
    technologies: []
  }
  technologiesInput.value = ''
  error.value = ''
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

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.projects-list h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
}

.project-card {
  display: flex;
  flex-direction: column;
}

.project-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.project-card h3 {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
}

.project-card p {
  flex: 1;
  color: #666;
  margin-bottom: 1rem;
}

.project-tech {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tech-tag {
  background: #42b983;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
}

.card-actions {
  display: flex;
  gap: 0.5rem;
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
  background: #42b983;
  color: white;
}

.btn-small.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-small:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
