<template>
  <div class="profile-page">
    <section class="profile-hero">
      <div class="profile-container">
        <div class="profile-image">
          <img src="https://via.placeholder.com/200" alt="Sourangshu Das" />
        </div>
        <div class="profile-info">
          <h1>Sourangshu Das</h1>
          <p class="profile-title">Senior Software Engineer</p>
          <p class="profile-bio">
            Senior Software Engineer with 7+ years of experience architecting scalable microservices,
            modernizing APIs through REST-to-GraphQL migration and implementing reactive systems with RxJava.
          </p>
          <div class="profile-contact">
            <a href="tel:+14094446236" class="contact-item">
              <span>📞 (409) 444-6236</span>
            </a>
            <a href="mailto:sourangshu.das01@gmail.com" class="contact-item">
              <span>✉️ sourangshu.das01@gmail.com</span>
            </a>
          </div>
          <div class="profile-social">
            <a href="https://www.linkedin.com/in/sourangshu-das-548ba4129" target="_blank" class="social-link">
              <span>LinkedIn</span>
            </a>
            <a href="https://github.com" target="_blank" class="social-link">
              <span>GitHub</span>
            </a>
          </div>
          <button @click="downloadResume" class="download-btn">
            📄 Download Resume (PDF)
          </button>
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
        <h3>7+</h3>
        <p>Years Experience</p>
      </div>
    </section>

    <section class="profile-section">
      <h2>Professional Summary</h2>
      <div class="about-content">
        <p>
          Senior Software Engineer with 7+ years of experience architecting scalable microservices,
          modernizing APIs through REST-to-GraphQL migration and implementing reactive systems with RxJava.
          Skilled in Java, Kotlin, and DevOps practices including Kubernetes, CI/CD (Harness, GitLab),
          and Infrastructure-as-Code (IaC) with CDK8s and Terraform.
        </p>
        <p>
          Developed a strong entrepreneurial mindset and successfully led end-to-end product delivery,
          growth experiments, and real-time optimizations at a fast-paced start-up. Eager to leverage
          expertise in full-stack development to drive engineering excellence.
        </p>
      </div>
    </section>

    <section class="profile-section">
      <h2>Technical Skills</h2>
      <div class="skills-grid">
        <div class="skill-category">
          <h3>Languages & Core</h3>
          <ul>
            <li>Java, Kotlin</li>
            <li>Python</li>
            <li>SQL</li>
            <li>LINUX</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Frameworks</h3>
          <ul>
            <li>Spring Boot, Spring JPA</li>
            <li>Ktor</li>
            <li>RxJava</li>
            <li>JUnit, Mockito</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Web Services</h3>
          <ul>
            <li>GraphQL</li>
            <li>REST (Swagger)</li>
            <li>SOAP, gRPC</li>
            <li>SSE (Server-Sent Events)</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Databases</h3>
          <ul>
            <li>PostgreSQL, MySQL</li>
            <li>MongoDB, Redis</li>
            <li>CockroachDB</li>
            <li>Microsoft SQL Server, H2</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>DevOps & Cloud</h3>
          <ul>
            <li>Docker, Kubernetes</li>
            <li>AWS (EKS, ELB, IAM)</li>
            <li>CDK8s, Terraform</li>
            <li>Apache Kafka</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>CI/CD & Tools</h3>
          <ul>
            <li>Harness, Jenkins</li>
            <li>Git, GitLab</li>
            <li>Gradle, Maven</li>
            <li>Liquibase</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Technologies</h3>
          <ul>
            <li>JPA, Drools, Feign</li>
            <li>Retrofit, JSLT</li>
            <li>Apache Tomcat, JBoss</li>
            <li>Apache Camel</li>
          </ul>
        </div>
        <div class="skill-category">
          <h3>Leadership</h3>
          <ul>
            <li>Code Reviews</li>
            <li>Mentorship, Scrum</li>
            <li>End-to-end Product Dev</li>
            <li>Technical Innovation</li>
          </ul>
        </div>
      </div>
    </section>

    <section class="profile-section">
      <h2>Work Experience</h2>
      <div class="experience-timeline">
        <div class="experience-item">
          <div class="experience-date">05/2019 - Present</div>
          <div class="experience-content">
            <h3>Senior Software Engineer</h3>
            <p class="company">KINECTIVE | USA</p>
            <ul class="achievements">
              <li>Spearheaded migration from REST to GraphQL using Kotlin and Ktor, reducing network calls and eliminating over-fetching/under-fetching to significantly improve performance.</li>
              <li>Led design and implementation of reactive systems using RxJava with Spring Boot for asynchronous event processing, improving system responsiveness and reducing code complexity.</li>
              <li>Architected and delivered Java-based Remote Transaction feature for banking tellers from concept to production, becoming the most widely used feature by end-users.</li>
              <li>Developed Technical Design Documents for Kotlin-based payment platform with modular microservices, JWT-based DPOP validation, and SSE-based event-driven service backed by Kafka.</li>
              <li>Achieved 50% reduction in build-to-deployment time by implementing Infrastructure-as-Code (IaC) with CDK8s and Terraform for Kubernetes management using Harness.</li>
              <li>Designed secure microservices architecture on AWS leveraging EKS, ELB, IAM, integrating third-party SaaS services like MongoDB, Kafka, and CockroachDB.</li>
              <li>Won company-wide hackathon creating AI-powered application using DistilBERT and TinyLlama for log analysis with Python backend and Vue.js frontend.</li>
              <li>Managed deliverables across 7 microservices while mentoring 3 onshore and 4 offshore engineers.</li>
            </ul>
          </div>
        </div>
        <div class="experience-item">
          <div class="experience-date">08/2018 - 05/2019</div>
          <div class="experience-content">
            <h3>Technology Analyst</h3>
            <p class="company">Mastech Digital (Contractor to AMEX) | USA</p>
            <ul class="achievements">
              <li>Developed APIs and business logic using Apache Camel with Java DSL and Spring MVC.</li>
              <li>Incorporated dependency injections for server-side and AJAX clients.</li>
              <li>Implemented Spring Batch jobs to automate daily file processing tasks.</li>
            </ul>
          </div>
        </div>
        <div class="experience-item">
          <div class="experience-date">08/2014 - 07/2016</div>
          <div class="experience-content">
            <h3>Systems Engineer</h3>
            <p class="company">INFOSYS Limited | INDIA</p>
            <ul class="achievements">
              <li>Developed efficient, client-centric full-stack applications using Java, Spring Boot/MVC, and Hibernate.</li>
              <li>Designed and implemented SOAP web service architecture.</li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <section class="profile-section">
      <h2>Education</h2>
      <div class="education-item">
        <div class="education-header">
          <h3>Master of Science in Computer Science</h3>
          <span class="education-year">2018</span>
        </div>
        <p class="education-school">Lamar University, Beaumont, TX</p>
        <p class="education-gpa">GPA: 3.65/4.00</p>
      </div>
    </section>

    <section class="profile-section">
      <h2>Publications</h2>
      <div class="publication-item">
        <h3>Sentiment Probing of Social Media Data using Various Supervised Learners</h3>
        <p class="publication-venue">
          14th International Conference on Data Science (ICDATA'18)
        </p>
        <p class="publication-details">
          July 30 - August 2, 2018 | Las Vegas, USA
        </p>
      </div>
    </section>

    <section class="profile-section">
      <h2>Let's Connect</h2>
      <p class="connect-message">
        Interested in collaboration or have a question? Feel free to reach out via email at
        <a href="mailto:sourangshu.das01@gmail.com" class="link">sourangshu.das01@gmail.com</a>
        or connect with me on
        <a href="https://www.linkedin.com/in/sourangshu-das-548ba4129" target="_blank" class="link">LinkedIn</a>.
      </p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '../stores/blog'
import { jsPDF } from 'jspdf'

const blogStore = useBlogStore()

const stats = ref({
  posts: 0,
  projects: 8,
  views: 0
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

const downloadResume = () => {
  const doc = new jsPDF()

  // Set font sizes
  const titleSize = 20
  const headerSize = 14
  const subHeaderSize = 12
  const bodySize = 10

  let y = 20
  const lineHeight = 6
  const margin = 20
  const pageWidth = doc.internal.pageSize.getWidth()

  // Header
  doc.setFontSize(titleSize)
  doc.setFont('helvetica', 'bold')
  doc.text('Sourangshu Das', margin, y)

  y += 8
  doc.setFontSize(bodySize)
  doc.setFont('helvetica', 'normal')
  doc.text('(409) 444-6236 | sourangshu.das01@gmail.com', margin, y)

  y += 5
  doc.setTextColor(0, 0, 255)
  doc.textWithLink('LinkedIn Profile', margin, y, { url: 'https://www.linkedin.com/in/sourangshu-das-548ba4129' })
  doc.setTextColor(0, 0, 0)

  y += 10
  doc.setFontSize(headerSize)
  doc.setFont('helvetica', 'bold')
  doc.text('Senior Software Engineer', margin, y)

  // Professional Summary
  y += 10
  doc.setFontSize(subHeaderSize)
  doc.text('PROFESSIONAL SUMMARY', margin, y)
  y += 7

  doc.setFontSize(bodySize)
  doc.setFont('helvetica', 'normal')
  const summary = 'Senior Software Engineer with 7+ years of experience architecting scalable microservices, modernizing APIs through REST-to-GraphQL migration and implementing reactive systems with RxJava. Skilled in Java, Kotlin, and DevOps practices including Kubernetes, CI/CD (Harness, GitLab), and Infrastructure-as-Code (IaC) with CDK8s and Terraform. Developed a strong entrepreneurial mindset and successfully led end-to-end product delivery, growth experiments, and real-time optimizations at a fast-paced start-up.'
  const splitSummary = doc.splitTextToSize(summary, pageWidth - 2 * margin)
  doc.text(splitSummary, margin, y)
  y += splitSummary.length * lineHeight + 5

  // Work Experience
  doc.setFontSize(subHeaderSize)
  doc.setFont('helvetica', 'bold')
  doc.text('WORK EXPERIENCE', margin, y)
  y += 7

  // KINECTIVE
  doc.setFontSize(bodySize)
  doc.setFont('helvetica', 'bold')
  doc.text('KINECTIVE | USA', margin, y)
  doc.setFont('helvetica', 'normal')
  doc.text('05/2019 - Present', pageWidth - margin - 40, y)
  y += 5

  doc.setFont('helvetica', 'italic')
  doc.text('Senior Software Engineer', margin, y)
  y += 5

  doc.setFont('helvetica', 'normal')
  const kinectivePoints = [
    'Spearheaded migration from REST to GraphQL using Kotlin and Ktor, reducing network calls',
    'Led design of reactive systems using RxJava with Spring Boot for asynchronous processing',
    'Architected Java-based Remote Transaction feature becoming most widely used by end-users',
    'Achieved 50% reduction in build-to-deployment time with CDK8s and Terraform IaC',
    'Designed secure microservices on AWS leveraging EKS, ELB, IAM with MongoDB, Kafka',
    'Won company hackathon with AI-powered log analysis using DistilBERT and Vue.js',
    'Managed 7 microservices while mentoring 3 onshore and 4 offshore engineers'
  ]

  kinectivePoints.forEach(point => {
    const lines = doc.splitTextToSize('• ' + point, pageWidth - 2 * margin - 5)
    doc.text(lines, margin + 3, y)
    y += lines.length * lineHeight
  })

  y += 5

  // Mastech Digital
  if (y > 250) {
    doc.addPage()
    y = 20
  }

  doc.setFont('helvetica', 'bold')
  doc.text('Mastech Digital (Contractor to AMEX) | USA', margin, y)
  doc.setFont('helvetica', 'normal')
  doc.text('08/2018 - 05/2019', pageWidth - margin - 40, y)
  y += 5

  doc.setFont('helvetica', 'italic')
  doc.text('Technology Analyst', margin, y)
  y += 5

  doc.setFont('helvetica', 'normal')
  const amexPoints = [
    'Developed APIs using Apache Camel with Java DSL and Spring MVC',
    'Implemented Spring Batch jobs to automate daily file processing tasks'
  ]

  amexPoints.forEach(point => {
    const lines = doc.splitTextToSize('• ' + point, pageWidth - 2 * margin - 5)
    doc.text(lines, margin + 3, y)
    y += lines.length * lineHeight
  })

  y += 5

  // INFOSYS
  doc.setFont('helvetica', 'bold')
  doc.text('INFOSYS Limited | INDIA', margin, y)
  doc.setFont('helvetica', 'normal')
  doc.text('08/2014 - 07/2016', pageWidth - margin - 40, y)
  y += 5

  doc.setFont('helvetica', 'italic')
  doc.text('Systems Engineer', margin, y)
  y += 5

  doc.setFont('helvetica', 'normal')
  const infosysPoints = [
    'Developed full-stack applications using Java, Spring Boot/MVC, and Hibernate',
    'Designed and implemented SOAP web service architecture'
  ]

  infosysPoints.forEach(point => {
    const lines = doc.splitTextToSize('• ' + point, pageWidth - 2 * margin - 5)
    doc.text(lines, margin + 3, y)
    y += lines.length * lineHeight
  })

  y += 8

  // Check if new page needed
  if (y > 240) {
    doc.addPage()
    y = 20
  }

  // Technical Skills
  doc.setFontSize(subHeaderSize)
  doc.setFont('helvetica', 'bold')
  doc.text('TECHNICAL SKILLS', margin, y)
  y += 7

  doc.setFontSize(bodySize)
  doc.setFont('helvetica', 'normal')
  const skills = [
    'Technologies: JAVA, JPA, Kotlin, Drools, Feign, LINUX, Liquibase, Retrofit, RxJava, SQL, SSE, JSLT, Python',
    'Web Services: GraphQL, REST (Swagger Docs), SOAP, gRPC',
    'Frameworks: Spring Boot, Spring JPA, Spring Metrics, Junit, Ktor, Mockito',
    'Tools: Docker, Apache Kafka, Kubernetes, Gradle, Maven, Cdk8s, Terraform',
    'Databases: H2, Microsoft SQL server, MySQL, Oracle SQL, PostgreSQL, Redis, MongoDB, CockroachDB',
    'Cloud Platforms: AWS (EKS, ELB, IAM)',
    'CI/CD: Git Pipeline, Harness, Jenkins'
  ]

  skills.forEach(skill => {
    const lines = doc.splitTextToSize(skill, pageWidth - 2 * margin)
    doc.text(lines, margin, y)
    y += lines.length * lineHeight + 2
  })

  y += 5

  // Education
  doc.setFontSize(subHeaderSize)
  doc.setFont('helvetica', 'bold')
  doc.text('EDUCATION', margin, y)
  y += 7

  doc.setFontSize(bodySize)
  doc.setFont('helvetica', 'bold')
  doc.text('Master of Science in Computer Science', margin, y)
  doc.setFont('helvetica', 'normal')
  doc.text('2018', pageWidth - margin - 20, y)
  y += 5

  doc.text('Lamar University, Beaumont, TX', margin, y)
  y += 5
  doc.text('GPA: 3.65/4.00', margin, y)
  y += 8

  // Publications
  doc.setFontSize(subHeaderSize)
  doc.setFont('helvetica', 'bold')
  doc.text('PUBLICATIONS', margin, y)
  y += 7

  doc.setFontSize(bodySize)
  doc.setFont('helvetica', 'normal')
  const pubTitle = 'Sentiment Probing of Social Media Data using Various Supervised Learners'
  const pubTitleLines = doc.splitTextToSize(pubTitle, pageWidth - 2 * margin)
  doc.text(pubTitleLines, margin, y)
  y += pubTitleLines.length * lineHeight

  doc.setFont('helvetica', 'italic')
  doc.text('14th International Conference on Data Science (ICDATA\'18)', margin, y)
  y += 5
  doc.text('July 30 - August 2, 2018, Las Vegas, USA', margin, y)

  // Save the PDF
  doc.save('Sourangshu_Das_Resume.pdf')
}
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
  margin-bottom: 1.5rem;
  color: rgba(255, 255, 255, 0.9);
}

.profile-contact {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
}

.contact-item {
  color: rgba(255, 255, 255, 0.95);
  text-decoration: none;
  font-size: 0.95rem;
}

.contact-item:hover {
  color: #a0c4ff;
}

.profile-social {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
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

.download-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.4);
  padding: 0.875rem 1.75rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.download-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
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
  margin-bottom: 0.75rem;
}

.achievements {
  list-style: none;
  padding: 0;
}

.achievements li {
  color: #555;
  line-height: 1.6;
  margin-bottom: 0.5rem;
  position: relative;
  padding-left: 1.2rem;
}

.achievements li:before {
  content: "•";
  position: absolute;
  left: 0;
  color: #2c5282;
  font-weight: bold;
}

/* Education */
.education-item {
  padding: 1rem 0;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 0.5rem;
}

.education-header h3 {
  font-size: 1.3rem;
  color: #1e3a5f;
  margin: 0;
}

.education-year {
  font-weight: 600;
  color: #2c5282;
}

.education-school {
  color: #666;
  font-style: italic;
  margin-bottom: 0.25rem;
}

.education-gpa {
  color: #555;
  font-weight: 500;
}

/* Publications */
.publication-item {
  padding: 1rem 0;
}

.publication-item h3 {
  font-size: 1.2rem;
  color: #1e3a5f;
  margin-bottom: 0.5rem;
}

.publication-venue {
  color: #666;
  font-style: italic;
  margin-bottom: 0.25rem;
}

.publication-details {
  color: #555;
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

  .profile-contact {
    flex-direction: column;
    align-items: center;
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

  .education-header {
    flex-direction: column;
    gap: 0.25rem;
  }
}
</style>
