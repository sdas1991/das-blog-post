<template>
  <div class="portfolio">
    <h1>Portfolio</h1>
    <p class="intro">Here are some of the projects I've worked on</p>

    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="projects.length" class="grid grid-2">
      <div v-for="project in projects" :key="project.id" class="card project-card">
        <img v-if="project.imageUrl" :src="project.imageUrl" :alt="project.title" class="project-image" />
        <h2>{{ project.title }}</h2>
        <p class="project-description">{{ project.description }}</p>
        <div v-if="project.technologies && project.technologies.length" class="project-tech">
          <span v-for="tech in project.technologies" :key="tech" class="tech-tag">{{ tech }}</span>
        </div>
        <div class="project-links">
          <a v-if="project.liveUrl" :href="project.liveUrl" target="_blank" class="btn btn-primary">
            Live Demo
          </a>
          <a v-if="project.githubUrl" :href="project.githubUrl" target="_blank" class="btn btn-secondary">
            GitHub
          </a>
        </div>
      </div>
    </div>
    <p v-else>No projects yet.</p>

    <section v-if="skills.length" class="skills-section card">
      <h2>Skills & Technologies</h2>
      <div class="skills-grid">
        <div v-for="skill in skills" :key="skill.id" class="skill-item">
          <h3>{{ skill.name }}</h3>
          <div class="skill-bar">
            <div class="skill-level" :style="{ width: skill.level + '%' }"></div>
          </div>
        </div>
      </div>
    </section>

    <section class="contact-section card">
      <h2>Get In Touch</h2>
      <form @submit.prevent="submitContact" class="contact-form">
        <div class="form-group">
          <label>Name</label>
          <input v-model="contactForm.name" type="text" required />
        </div>
        <div class="form-group">
          <label>Email</label>
          <input v-model="contactForm.email" type="email" required />
        </div>
        <div class="form-group">
          <label>Message</label>
          <textarea v-model="contactForm.message" rows="5" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="submitting">
          {{ submitting ? 'Sending...' : 'Send Message' }}
        </button>
        <p v-if="contactMessage" :class="contactSuccess ? 'success-message' : 'error-message'">
          {{ contactMessage }}
        </p>
      </form>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { portfolioService } from '../services/api'

const projects = ref([])
const skills = ref([])
const loading = ref(false)
const submitting = ref(false)
const contactMessage = ref('')
const contactSuccess = ref(false)

const contactForm = ref({
  name: '',
  email: '',
  message: ''
})

onMounted(async () => {
  loading.value = true
  try {
    const [projectsData, skillsData] = await Promise.all([
      portfolioService.getProjects(),
      portfolioService.getSkills()
    ])
    projects.value = projectsData
    skills.value = skillsData
  } catch (error) {
    console.error('Failed to load portfolio:', error)
  } finally {
    loading.value = false
  }
})

const submitContact = async () => {
  submitting.value = true
  contactMessage.value = ''

  try {
    await portfolioService.submitContact(contactForm.value)
    contactSuccess.value = true
    contactMessage.value = 'Message sent successfully! I\'ll get back to you soon.'
    contactForm.value = { name: '', email: '', message: '' }
  } catch (error) {
    contactSuccess.value = false
    contactMessage.value = 'Failed to send message. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.portfolio h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.intro {
  font-size: 1.25rem;
  color: #666;
  margin-bottom: 3rem;
}

.project-card {
  display: flex;
  flex-direction: column;
}

.project-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.project-card h2 {
  font-size: 1.5rem;
  margin-bottom: 0.75rem;
}

.project-description {
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

.project-links {
  display: flex;
  gap: 1rem;
}

.skills-section {
  margin-top: 3rem;
}

.skills-section h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
}

.skills-grid {
  display: grid;
  gap: 1.5rem;
}

.skill-item h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}

.skill-bar {
  width: 100%;
  height: 10px;
  background: #e9ecef;
  border-radius: 5px;
  overflow: hidden;
}

.skill-level {
  height: 100%;
  background: linear-gradient(90deg, #42b983, #2c9868);
  transition: width 0.3s;
}

.contact-section {
  margin-top: 3rem;
}

.contact-section h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
}

.contact-form {
  max-width: 600px;
}
</style>
