<template>
  <div class="admin-profile">
    <h1>Profile Management</h1>

    <div class="tabs">
      <button
        :class="['tab-btn', { active: activeTab === 'basic' }]"
        @click="activeTab = 'basic'"
      >
        Basic Info
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'resume' }]"
        @click="activeTab = 'resume'"
      >
        Resume Upload
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'experience' }]"
        @click="activeTab = 'experience'"
      >
        Experience
      </button>
      <button
        :class="['tab-btn', { active: activeTab === 'skills' }]"
        @click="activeTab = 'skills'"
      >
        Skills
      </button>
    </div>

    <!-- Basic Info Tab -->
    <div v-if="activeTab === 'basic'" class="tab-content">
      <div class="card">
        <h2>Basic Information</h2>
        <form @submit.prevent="saveBasicInfo" class="form">
          <div class="form-group">
            <label>Full Name</label>
            <input v-model="profile.name" type="text" required />
          </div>

          <div class="form-group">
            <label>Title</label>
            <input v-model="profile.title" type="text" required />
          </div>

          <div class="form-group">
            <label>Email</label>
            <input v-model="profile.email" type="email" required />
          </div>

          <div class="form-group">
            <label>LinkedIn URL</label>
            <input v-model="profile.linkedinUrl" type="url" />
          </div>

          <div class="form-group">
            <label>GitHub URL</label>
            <input v-model="profile.githubUrl" type="url" />
          </div>

          <div class="form-group">
            <label>Bio/Summary</label>
            <textarea v-model="profile.bio" rows="4" required></textarea>
          </div>

          <div class="form-group">
            <label>Profile Image URL</label>
            <input v-model="profile.imageUrl" type="url" />
          </div>

          <div class="form-group">
            <label>Years of Experience</label>
            <input v-model.number="profile.yearsExperience" type="number" required />
          </div>

          <button type="submit" class="btn-primary">Save Basic Info</button>
          <span v-if="saveMessage" class="save-message">{{ saveMessage }}</span>
        </form>
      </div>
    </div>

    <!-- Resume Upload Tab -->
    <div v-if="activeTab === 'resume'" class="tab-content">
      <div class="card">
        <h2>Resume/CV Upload</h2>
        <p class="info">Upload a Word document (.docx) that will be converted and made available for download on your profile page.</p>

        <div class="upload-section">
          <div class="current-resume" v-if="currentResume">
            <h3>Current Resume</h3>
            <div class="resume-info">
              <span>📄 {{ currentResume.filename }}</span>
              <span class="resume-date">Uploaded: {{ formatDate(currentResume.uploadedAt) }}</span>
            </div>
            <button @click="downloadCurrentResume" class="btn-secondary">Download Current</button>
          </div>

          <div class="upload-area">
            <h3>Upload New Resume</h3>
            <input
              type="file"
              ref="resumeFileInput"
              @change="handleResumeUpload"
              accept=".doc,.docx,.pdf"
              class="file-input"
            />
            <div class="upload-dropzone" @click="$refs.resumeFileInput.click()">
              <p v-if="!uploadedFile">Click to select file or drag & drop</p>
              <p v-else>Selected: {{ uploadedFile.name }}</p>
            </div>
            <button
              v-if="uploadedFile"
              @click="uploadResume"
              :disabled="uploading"
              class="btn-primary"
            >
              {{ uploading ? 'Uploading...' : 'Upload Resume' }}
            </button>
          </div>

          <div v-if="uploadMessage" class="upload-message" :class="uploadError ? 'error' : 'success'">
            {{ uploadMessage }}
          </div>
        </div>
      </div>
    </div>

    <!-- Experience Tab -->
    <div v-if="activeTab === 'experience'" class="tab-content">
      <div class="card">
        <h2>Work Experience</h2>
        <button @click="addExperience" class="btn-secondary mb-3">Add Experience</button>

        <div v-for="(exp, index) in profile.experience" :key="index" class="experience-item">
          <div class="form-row">
            <div class="form-group">
              <label>Company</label>
              <input v-model="exp.company" type="text" required />
            </div>
            <div class="form-group">
              <label>Title</label>
              <input v-model="exp.title" type="text" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Start Date (MM/YYYY)</label>
              <input v-model="exp.startDate" type="text" placeholder="05/2019" required />
            </div>
            <div class="form-group">
              <label>End Date (MM/YYYY or "Present")</label>
              <input v-model="exp.endDate" type="text" placeholder="Present" required />
            </div>
          </div>

          <div class="form-group">
            <label>Location</label>
            <input v-model="exp.location" type="text" />
          </div>

          <div class="form-group">
            <label>Achievements (one per line)</label>
            <textarea v-model="exp.achievements" rows="5" placeholder="Each line will be a bullet point"></textarea>
          </div>

          <button @click="removeExperience(index)" class="btn-danger">Remove Experience</button>
          <hr />
        </div>

        <button @click="saveExperience" class="btn-primary">Save Experience</button>
        <span v-if="saveMessage" class="save-message">{{ saveMessage }}</span>
      </div>
    </div>

    <!-- Skills Tab -->
    <div v-if="activeTab === 'skills'" class="tab-content">
      <div class="card">
        <h2>Technical Skills</h2>
        <button @click="addSkillCategory" class="btn-secondary mb-3">Add Skill Category</button>

        <div v-for="(category, index) in profile.skills" :key="index" class="skill-category-item">
          <div class="form-group">
            <label>Category Name</label>
            <input v-model="category.name" type="text" placeholder="e.g., Frameworks" required />
          </div>

          <div class="form-group">
            <label>Skills (comma separated)</label>
            <textarea
              v-model="category.items"
              rows="3"
              placeholder="Spring Boot, Ktor, RxJava, JUnit, Mockito"
            ></textarea>
          </div>

          <button @click="removeSkillCategory(index)" class="btn-danger">Remove Category</button>
          <hr />
        </div>

        <button @click="saveSkills" class="btn-primary">Save Skills</button>
        <span v-if="saveMessage" class="save-message">{{ saveMessage }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../../services/api'

const activeTab = ref('basic')
const saveMessage = ref('')
const uploadMessage = ref('')
const uploadError = ref(false)
const uploading = ref(false)
const uploadedFile = ref(null)
const resumeFileInput = ref(null)
const currentResume = ref(null)

const profile = ref({
  name: 'Sourangshu Das',
  title: 'Senior Software Engineer',
  email: 'sourangshu.das01@gmail.com',
  linkedinUrl: 'https://www.linkedin.com/in/sourangshu-das-548ba4129',
  githubUrl: 'https://github.com',
  bio: 'Senior Software Engineer with 7+ years of experience architecting scalable microservices, modernizing APIs through REST-to-GraphQL migration and implementing reactive systems with RxJava.',
  imageUrl: 'https://via.placeholder.com/200',
  yearsExperience: 7,
  experience: [
    {
      company: 'KINECTIVE',
      title: 'Senior Software Engineer',
      location: 'USA',
      startDate: '05/2019',
      endDate: 'Present',
      achievements: `Spearheaded migration from REST to GraphQL using Kotlin and Ktor
Led design and implementation of reactive systems using RxJava
Architected Java-based Remote Transaction feature
Achieved 50% reduction in build-to-deployment time with IaC
Designed secure microservices on AWS (EKS, ELB, IAM)
Won company hackathon with AI-powered log analysis
Managed 7 microservices while mentoring 7 engineers`
    }
  ],
  skills: [
    {
      name: 'Languages & Core',
      items: 'Java, Kotlin, Python, SQL, LINUX'
    },
    {
      name: 'Frameworks',
      items: 'Spring Boot, Spring JPA, Ktor, RxJava, JUnit, Mockito'
    }
  ]
})

onMounted(async () => {
  await loadProfile()
  await loadCurrentResume()
})

const loadProfile = async () => {
  try {
    // Load profile from localStorage or API
    const savedProfile = localStorage.getItem('adminProfile')
    if (savedProfile) {
      profile.value = JSON.parse(savedProfile)
    }
  } catch (error) {
    console.error('Failed to load profile:', error)
  }
}

const loadCurrentResume = async () => {
  try {
    const savedResume = localStorage.getItem('currentResume')
    if (savedResume) {
      currentResume.value = JSON.parse(savedResume)
    }
  } catch (error) {
    console.error('Failed to load current resume:', error)
  }
}

const saveBasicInfo = async () => {
  try {
    localStorage.setItem('adminProfile', JSON.stringify(profile.value))
    saveMessage.value = 'Basic info saved successfully!'
    setTimeout(() => saveMessage.value = '', 3000)
  } catch (error) {
    saveMessage.value = 'Failed to save basic info'
    console.error('Save error:', error)
  }
}

const saveExperience = async () => {
  try {
    localStorage.setItem('adminProfile', JSON.stringify(profile.value))
    saveMessage.value = 'Experience saved successfully!'
    setTimeout(() => saveMessage.value = '', 3000)
  } catch (error) {
    saveMessage.value = 'Failed to save experience'
    console.error('Save error:', error)
  }
}

const saveSkills = async () => {
  try {
    localStorage.setItem('adminProfile', JSON.stringify(profile.value))
    saveMessage.value = 'Skills saved successfully!'
    setTimeout(() => saveMessage.value = '', 3000)
  } catch (error) {
    saveMessage.value = 'Failed to save skills'
    console.error('Save error:', error)
  }
}

const addExperience = () => {
  profile.value.experience.push({
    company: '',
    title: '',
    location: '',
    startDate: '',
    endDate: '',
    achievements: ''
  })
}

const removeExperience = (index) => {
  profile.value.experience.splice(index, 1)
}

const addSkillCategory = () => {
  profile.value.skills.push({
    name: '',
    items: ''
  })
}

const removeSkillCategory = (index) => {
  profile.value.skills.splice(index, 1)
}

const handleResumeUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    uploadedFile.value = file
    uploadMessage.value = ''
  }
}

const uploadResume = async () => {
  if (!uploadedFile.value) return

  uploading.value = true
  uploadMessage.value = ''
  uploadError.value = false

  try {
    const formData = new FormData()
    formData.append('file', uploadedFile.value)

    // In production, this would upload to the server
    // For now, we'll store it locally
    const reader = new FileReader()
    reader.onload = (e) => {
      const resume = {
        filename: uploadedFile.value.name,
        uploadedAt: new Date().toISOString(),
        data: e.target.result
      }
      localStorage.setItem('currentResume', JSON.stringify(resume))
      currentResume.value = resume
      uploadMessage.value = 'Resume uploaded successfully!'
      uploadedFile.value = null
      if (resumeFileInput.value) {
        resumeFileInput.value.value = ''
      }
    }
    reader.readAsDataURL(uploadedFile.value)
  } catch (error) {
    uploadMessage.value = 'Failed to upload resume'
    uploadError.value = true
    console.error('Upload error:', error)
  } finally {
    uploading.value = false
  }
}

const downloadCurrentResume = () => {
  if (!currentResume.value) return

  const link = document.createElement('a')
  link.href = currentResume.value.data
  link.download = currentResume.value.filename
  link.click()
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.admin-profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #333;
}

.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  border-bottom: 2px solid #e0e0e0;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 1rem;
  color: #666;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  color: #2c5282;
}

.tab-btn.active {
  color: #2c5282;
  border-bottom-color: #2c5282;
  font-weight: 600;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card h2 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #2c5282;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #555;
}

.form-group input,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #2c5282;
  box-shadow: 0 0 0 3px rgba(44, 82, 130, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.btn-primary,
.btn-secondary,
.btn-danger {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background: #2c5282;
  color: white;
}

.btn-primary:hover {
  background: #1e3a5f;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.btn-primary:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #64b5f6;
  color: white;
}

.btn-secondary:hover {
  background: #42a5f5;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
}

.mb-3 {
  margin-bottom: 1rem;
}

.save-message {
  margin-left: 1rem;
  color: #27ae60;
  font-weight: 600;
}

.experience-item,
.skill-category-item {
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.experience-item hr,
.skill-category-item hr {
  border: none;
  border-top: 2px solid #e0e0e0;
  margin: 1.5rem 0 0 0;
}

/* Resume Upload Styles */
.info {
  color: #666;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f0f7ff;
  border-left: 4px solid #2c5282;
  border-radius: 4px;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.current-resume {
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.current-resume h3 {
  margin-bottom: 1rem;
  color: #2c5282;
}

.resume-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.resume-date {
  font-size: 0.9rem;
  color: #666;
}

.upload-area {
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.upload-area h3 {
  margin-bottom: 1rem;
  color: #2c5282;
}

.file-input {
  display: none;
}

.upload-dropzone {
  border: 2px dashed #2c5282;
  border-radius: 8px;
  padding: 3rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 1rem;
}

.upload-dropzone:hover {
  background: #f0f7ff;
  border-color: #1e3a5f;
}

.upload-dropzone p {
  color: #666;
  font-size: 1.1rem;
}

.upload-message {
  padding: 1rem;
  border-radius: 6px;
  font-weight: 600;
}

.upload-message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.upload-message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

@media (max-width: 768px) {
  .admin-profile {
    padding: 1rem;
  }

  .tabs {
    flex-wrap: wrap;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .tab-btn {
    flex: 1;
    min-width: 120px;
  }
}
</style>
