<template>
  <div class="profile-page">
    <section class="profile-hero">
      <div class="profile-container">
        <div class="profile-image">
          <img src="https://via.placeholder.com/200" alt="Profile Picture" />
        </div>
        <div class="profile-info">
          <h1>DAS Developer</h1>
          <p class="profile-title">Full Stack Developer & Technical Writer</p>
          <p class="profile-bio">
            Passionate about building scalable web applications and sharing knowledge
            through technical writing. Specializing in modern JavaScript frameworks,
            microservices architecture, and cloud technologies.
          </p>
          <div class="profile-social">
            <a href="https://github.com" target="_blank" class="social-link">
              <span>GitHub</span>
            </a>
            <a href="https://linkedin.com" target="_blank" class="social-link">
              <span>LinkedIn</span>
            </a>
            <a href="https://twitter.com" target="_blank" class="social-link">
              <span>Twitter</span>
            </a>
          </div>
        </div>
      </div>
    </section>

    <section class="profile-stats">
      <div class="stat-card">
        <h3>{{ stats.posts }}</h3>
        <p>Blog Posts</p>
      </div>
      <div class="stat-card">
        <h3>{{ stats.projects }}</h3>
        <p>Projects</p>
      </div>
      <div class="stat-card">
        <h3>{{ stats.views }}</h3>
        <p>Total Views</p>
      </div>
      <div class="stat-card">
        <h3>{{ stats.years }}</h3>
        <p>Years Experience</p>
      </div>
    </section>

    <section class="profile-section">
      <h2>About Me</h2>
      <div class="about-content">
        <p>
          I'm a Full Stack Developer with over 5 years of experience in building modern web applications.
          My expertise spans across frontend and backend technologies, with a strong focus on creating
          scalable, maintainable, and user-friendly solutions.
        </p>
        <p>
          I believe in continuous learning and sharing knowledge with the community. Through my blog,
          I share insights, tutorials, and best practices that I've learned throughout my journey.
        </p>
      </div>
    </section>

    <section class="profile-section">
      <h2>Technical Skills</h2>
      <div class="skills-grid">
        <div class="skill-category">
          <h3>Frontend</h3>
          <ul>
            <li>Vue.js / React</li>
            <li>JavaScript / TypeScript</li>
            <li>HTML5 / CSS3</li>
            <li>Responsive Design</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Backend</h3>
          <ul>
            <li>Node.js / Express</li>
            <li>Java / Spring Boot</li>
            <li>Python / FastAPI</li>
            <li>RESTful APIs / GraphQL</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Database</h3>
          <ul>
            <li>PostgreSQL / MySQL</li>
            <li>MongoDB</li>
            <li>Redis</li>
            <li>Database Design</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>DevOps</h3>
          <ul>
            <li>Docker / Kubernetes</li>
            <li>AWS / Azure</li>
            <li>CI/CD Pipelines</li>
            <li>Git / GitHub Actions</li>
          </ul>
        </div>
      </div>
    </section>

    <section class="profile-section">
      <h2>Experience</h2>
      <div class="experience-timeline">
        <div class="experience-item">
          <div class="experience-date">2021 - Present</div>
          <div class="experience-content">
            <h3>Senior Full Stack Developer</h3>
            <p class="company">Tech Company Inc.</p>
            <p>Leading development of microservices-based applications using modern tech stack.</p>
          </div>
        </div>
        <div class="experience-item">
          <div class="experience-date">2019 - 2021</div>
          <div class="experience-content">
            <h3>Full Stack Developer</h3>
            <p class="company">Startup Solutions</p>
            <p>Built and maintained web applications serving thousands of users.</p>
          </div>
        </div>
        <div class="experience-item">
          <div class="experience-date">2018 - 2019</div>
          <div class="experience-content">
            <h3>Junior Developer</h3>
            <p class="company">Web Agency</p>
            <p>Developed client websites and learned best practices in web development.</p>
          </div>
        </div>
      </div>
    </section>

    <section class="profile-section">
      <h2>Let's Connect</h2>
      <p class="connect-message">
        Interested in collaboration or have a question? Feel free to reach out through
        <router-link to="/portfolio" class="link">my portfolio contact form</router-link>
        or connect with me on social media.
      </p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '../stores/blog'

const blogStore = useBlogStore()

const stats = ref({
  posts: 0,
  projects: 8,
  views: 0,
  years: 5
})

onMounted(async () => {
  try {
    await blogStore.fetchPosts({ published: true })
    stats.value.posts = blogStore.posts.length

    // Calculate total views
    stats.value.views = blogStore.posts.reduce((total, post) => {
      return total + (post.views || 0)
    }, 0)
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 1000px;
  margin: 0 auto;
}

/* Hero Section - Dark Blue Theme */
.profile-hero {
  background: linear-gradient(135deg, #1e3a5f 0%, #2c5282 100%);
  color: white;
  padding: 4rem 2rem;
  border-radius: 12px;
  margin-bottom: 3rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.profile-container {
  display: flex;
  gap: 3rem;
  align-items: center;
}

.profile-image img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.2);
  object-fit: cover;
}

.profile-info {
  flex: 1;
}

.profile-info h1 {
  font-size: 3rem;
  margin-bottom: 0.5rem;
  color: white;
}

.profile-title {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #a0c4ff;
  font-weight: 500;
}

.profile-bio {
  font-size: 1.1rem;
  line-height: 1.7;
  margin-bottom: 2rem;
  color: rgba(255, 255, 255, 0.9);
}

.profile-social {
  display: flex;
  gap: 1rem;
}

.social-link {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  text-decoration: none;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.social-link:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

/* Stats Section */
.profile-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #2c5282;
}

.stat-card h3 {
  font-size: 2.5rem;
  color: #2c5282;
  margin-bottom: 0.5rem;
}

.stat-card p {
  color: #666;
  font-size: 1rem;
  margin: 0;
}

/* Profile Sections */
.profile-section {
  background: white;
  padding: 2.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-section h2 {
  font-size: 2rem;
  color: #1e3a5f;
  margin-bottom: 1.5rem;
  border-bottom: 3px solid #2c5282;
  padding-bottom: 0.5rem;
}

.about-content p {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #444;
  margin-bottom: 1rem;
}

/* Skills Grid */
.skills-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 2rem;
}

.skill-category h3 {
  color: #2c5282;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.skill-category ul {
  list-style: none;
  padding: 0;
}

.skill-category li {
  padding: 0.5rem 0;
  color: #555;
  position: relative;
  padding-left: 1.5rem;
}

.skill-category li:before {
  content: "▸";
  position: absolute;
  left: 0;
  color: #2c5282;
  font-weight: bold;
}

/* Experience Timeline */
.experience-timeline {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.experience-item {
  display: flex;
  gap: 2rem;
  padding-left: 2rem;
  border-left: 3px solid #2c5282;
  position: relative;
}

.experience-item:before {
  content: "";
  position: absolute;
  left: -7px;
  top: 0;
  width: 12px;
  height: 12px;
  background: #2c5282;
  border-radius: 50%;
}

.experience-date {
  min-width: 120px;
  font-weight: 600;
  color: #2c5282;
  padding-top: 0.25rem;
}

.experience-content h3 {
  font-size: 1.3rem;
  color: #1e3a5f;
  margin-bottom: 0.25rem;
}

.company {
  color: #666;
  font-style: italic;
  margin-bottom: 0.5rem;
}

.experience-content p:last-child {
  color: #555;
  line-height: 1.6;
  margin: 0;
}

/* Connect Section */
.connect-message {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #444;
}

.link {
  color: #2c5282;
  font-weight: 600;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

/* Responsive */
@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    text-align: center;
  }

  .profile-info h1 {
    font-size: 2rem;
  }

  .profile-title {
    font-size: 1.2rem;
  }

  .profile-social {
    justify-content: center;
  }

  .skills-grid {
    grid-template-columns: 1fr;
  }

  .experience-item {
    flex-direction: column;
    gap: 0.5rem;
  }

  .experience-date {
    padding-top: 0;
  }
}
</style>
