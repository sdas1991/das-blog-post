# AWS Free Tier Deployment Guide

Complete step-by-step guide to deploy the DAS Blog & Portfolio Platform on AWS Free Tier.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Architecture](#architecture)
4. [AWS Services Used](#aws-services-used)
5. [Step-by-Step Deployment](#step-by-step-deployment)
6. [Post-Deployment Configuration](#post-deployment-configuration)
7. [Monitoring and Maintenance](#monitoring-and-maintenance)
8. [Cost Optimization](#cost-optimization)
9. [Troubleshooting](#troubleshooting)

---

## Overview

This guide will walk you through deploying the microservices-based blog and portfolio platform on AWS using Free Tier eligible services.

**Deployment Strategy:** Docker Compose on EC2 (simplest for Free Tier)

**Estimated Monthly Cost:** $0 (within Free Tier limits) to $15 (if exceeding limits)

---

## Prerequisites

### Required Accounts

1. **AWS Account** with Free Tier eligibility
2. **Domain Name** (optional, but recommended)
3. **Email Account** for system notifications

### Required Tools

Install these on your local machine:

```bash
# AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Docker & Docker Compose (for local testing)
sudo apt-get update
sudo apt-get install docker.io docker-compose

# Git
sudo apt-get install git

# SSH Key Pair
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

### Knowledge Requirements

- Basic Linux command line
- Understanding of Docker and Docker Compose
- Familiarity with AWS console
- Basic networking concepts (ports, firewall rules)

---

## Architecture

### Deployment Architecture

```
┌─────────────────────────────────────────────────┐
│            Route 53 (Domain DNS)                │
│            or EC2 Public IP                     │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│          Application Load Balancer              │
│                 (Optional)                      │
└──────────────────┬──────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────┐
│              EC2 Instance                       │
│            (t2.micro - Free Tier)               │
│                                                 │
│  ┌─────────────────────────────────────┐       │
│  │     Docker Compose Environment      │       │
│  │                                     │       │
│  │  ┌──────────┐   ┌──────────────┐  │       │
│  │  │ Frontend │   │ Auth Service │  │       │
│  │  │  (Nginx) │   │   (Node.js)  │  │       │
│  │  │  :3000   │   │    :3001     │  │       │
│  │  └──────────┘   └──────────────┘  │       │
│  │                                     │       │
│  │  ┌──────────┐   ┌──────────────┐  │       │
│  │  │   Blog   │   │  Portfolio   │  │       │
│  │  │ Service  │   │   Service    │  │       │
│  │  │  (Java)  │   │  (Kotlin)    │  │       │
│  │  │  :3002   │   │    :3003     │  │       │
│  │  └──────────┘   └──────────────┘  │       │
│  │                                     │       │
│  │  ┌──────────┐   ┌──────────────┐  │       │
│  │  │PostgreSQL│   │   MongoDB    │  │       │
│  │  │  :5432   │   │    :27017    │  │       │
│  │  └──────────┘   └──────────────┘  │       │
│  │                                     │       │
│  │  ┌──────────┐                      │       │
│  │  │  MySQL   │                      │       │
│  │  │  :3306   │                      │       │
│  │  └──────────┘                      │       │
│  │                                     │       │
│  └─────────────────────────────────────┘       │
│                                                 │
└─────────────────────────────────────────────────┘
           │               │
┌──────────▼────┐    ┌─────▼──────┐
│  EBS Volume   │    │    S3      │
│  (Storage)    │    │ (Backups)  │
└───────────────┘    └────────────┘
```

---

## AWS Services Used

### Free Tier Services

| Service | Free Tier Limit | Purpose |
|---------|----------------|---------|
| **EC2** | 750 hours/month (t2.micro) | Host Docker containers |
| **EBS** | 30 GB General Purpose (SSD) | Persistent storage |
| **Data Transfer** | 15 GB/month | Network egress |
| **S3** | 5 GB storage, 20,000 GET requests | Backups, static assets |
| **Route 53** | $0.50/hosted zone/month (NOT free) | DNS management |
| **CloudWatch** | 10 metrics, 5 GB logs | Monitoring |
| **SNS** | 1,000 email notifications | Alerts |

### Optional Services

- **AWS Certificate Manager (ACM)**: Free SSL/TLS certificates
- **Elastic Load Balancer**: $0.025/hour (NOT in Free Tier)
- **RDS**: Free Tier alternative to self-hosted databases (limited)

---

## Step-by-Step Deployment

### Phase 1: AWS Account Setup

#### 1.1 Create AWS Account

1. Go to [aws.amazon.com](https://aws.amazon.com)
2. Click "Create an AWS Account"
3. Follow the signup process
4. Add payment method (required, but won't be charged for Free Tier usage)
5. Complete phone verification
6. Select "Free" support plan

#### 1.2 Configure IAM User (Best Practice)

```bash
# Login to AWS Console as root
# Go to IAM → Users → Add User

# Create user: blog-admin
# Access type: Programmatic access + AWS Management Console
# Attach policies:
# - AmazonEC2FullAccess
# - AmazonS3FullAccess
# - CloudWatchLogsFullAccess

# Download credentials CSV (IMPORTANT: Save securely!)
```

#### 1.3 Configure AWS CLI

```bash
aws configure
# AWS Access Key ID: <your-access-key>
# AWS Secret Access Key: <your-secret-key>
# Default region: us-east-1
# Default output format: json
```

### Phase 2: EC2 Instance Setup

#### 2.1 Launch EC2 Instance

1. **Login to AWS Console** → EC2 Dashboard

2. **Click "Launch Instance"**

3. **Configure Instance:**
   ```
   Name: blog-portfolio-server

   AMI: Ubuntu Server 22.04 LTS (Free Tier eligible)

   Instance Type: t2.micro (1 vCPU, 1 GB RAM)

   Key Pair: Create new key pair
   - Name: blog-portfolio-key
   - Type: RSA
   - Format: .pem
   - SAVE THE .PEM FILE SECURELY!

   Network Settings:
   - Create security group: blog-portfolio-sg
   - Allow SSH (22) from My IP
   - Allow HTTP (80) from Anywhere (0.0.0.0/0)
   - Allow HTTPS (443) from Anywhere (0.0.0.0/0)
   - Allow Custom TCP (3000-3003) from Anywhere (for testing)

   Storage: 30 GB gp2 (Free Tier)

   Advanced Details:
   - Enable detailed monitoring: NO (costs extra)
   ```

4. **Click "Launch Instance"**

5. **Wait for instance to be "Running"**

#### 2.2 Configure Security Group

```bash
# Add additional rules if needed
aws ec2 authorize-security-group-ingress \
    --group-id <security-group-id> \
    --protocol tcp \
    --port 3000-3003 \
    --cidr 0.0.0.0/0

# For PostgreSQL (if external access needed)
aws ec2 authorize-security-group-ingress \
    --group-id <security-group-id> \
    --protocol tcp \
    --port 5432 \
    --cidr <your-ip>/32
```

#### 2.3 Allocate Elastic IP (Optional but Recommended)

```bash
# Allocate Elastic IP
aws ec2 allocate-address --domain vpc

# Associate with instance
aws ec2 associate-address \
    --instance-id <instance-id> \
    --allocation-id <eip-allocation-id>
```

**Note:** 1 Elastic IP is free when associated with a running instance.

### Phase 3: Server Configuration

#### 3.1 Connect to EC2 Instance

```bash
# Set key permissions
chmod 400 blog-portfolio-key.pem

# Connect via SSH
ssh -i blog-portfolio-key.pem ubuntu@<ec2-public-ip>
```

#### 3.2 Install Docker and Docker Compose

```bash
# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Docker
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Add user to docker group
sudo usermod -aG docker ubuntu
newgrp docker

# Verify installations
docker --version
docker-compose --version
```

#### 3.3 Install Additional Tools

```bash
# Install Git
sudo apt-get install -y git

# Install monitoring tools
sudo apt-get install -y htop ncdu

# Install text editor
sudo apt-get install -y nano vim
```

### Phase 4: Application Deployment

#### 4.1 Clone Repository

```bash
# Create application directory
mkdir -p /home/ubuntu/apps
cd /home/ubuntu/apps

# Clone your repository
git clone https://github.com/YOUR_USERNAME/das-blog-post.git
cd das-blog-post
```

#### 4.2 Configure Environment Variables

```bash
# Create .env file
nano .env
```

**Add the following:**

```env
# Auth Service (PostgreSQL)
DB_HOST=postgres
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres
DB_PASSWORD=YourSecurePassword123!

# Blog Service (MongoDB)
MONGO_HOST=mongodb
MONGO_PORT=27017
MONGO_DATABASE=blog_db
MONGO_USER=
MONGO_PASSWORD=

# Portfolio Service (MySQL)
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=portfolio_db
MYSQL_USER=portfolio_user
MYSQL_PASSWORD=YourSecurePassword456!
MYSQL_ROOT_PASSWORD=YourRootPassword789!

# JWT Configuration
JWT_SECRET=YourVerySecureJWTSecret_ChangeThis_MinimumLength32Characters!
JWT_EXPIRATION=7d

# Email Configuration (Optional)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Application URLs
FRONTEND_URL=http://<your-ec2-ip>:3000
API_BASE_URL=http://<your-ec2-ip>

# Node Environment
NODE_ENV=production
```

**Save and exit:** Ctrl+O, Enter, Ctrl+X

**Secure the .env file:**
```bash
chmod 600 .env
```

#### 4.3 Build and Start Services

```bash
# Build and start all services
docker-compose up -d --build

# Monitor logs
docker-compose logs -f

# Check running containers
docker-compose ps
```

**Expected output:**
```
NAME                  STATUS      PORTS
auth-service          Up          0.0.0.0:3001->3001/tcp
blog-service          Up          0.0.0.0:3002->3002/tcp
portfolio-service     Up          0.0.0.0:3003->3003/tcp
frontend              Up          0.0.0.0:3000->80/tcp
postgres              Up          5432/tcp
mongodb               Up          27017/tcp
mysql                 Up          3306/tcp
```

#### 4.4 Verify Deployment

```bash
# Test frontend
curl http://localhost:3000

# Test auth service
curl http://localhost:3001/health

# Test blog service
curl http://localhost:3002/graphiql

# Test portfolio service
curl http://localhost:3003/api/portfolio/projects

# Check from external (replace with your EC2 IP)
curl http://<ec2-public-ip>:3000
```

### Phase 5: Production Optimization

#### 5.1 Configure Nginx as Reverse Proxy

```bash
# Install Nginx on host (optional, better than exposing ports)
sudo apt-get install -y nginx

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/blog-portfolio
```

**Add configuration:**

```nginx
server {
    listen 80;
    server_name <your-domain-or-ip>;

    # Frontend
    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # Auth Service
    location /api/auth/ {
        proxy_pass http://localhost:3001/api/auth/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Blog Service
    location /graphql {
        proxy_pass http://localhost:3002/graphql;
        proxy_set_header Host $host;
    }

    location /graphiql {
        proxy_pass http://localhost:3002/graphiql;
        proxy_set_header Host $host;
    }

    # Portfolio Service
    location /api/portfolio/ {
        proxy_pass http://localhost:3003/api/portfolio/;
        proxy_set_header Host $host;
    }

    # Uploads
    location /uploads/ {
        alias /home/ubuntu/apps/das-blog-post/backend/auth-service/uploads/;
    }
}
```

**Enable site and restart Nginx:**

```bash
sudo ln -s /etc/nginx/sites-available/blog-portfolio /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### 5.2 Setup SSL with Let's Encrypt (Free)

```bash
# Install Certbot
sudo apt-get install -y certbot python3-certbot-nginx

# Obtain certificate (requires domain name)
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Auto-renewal is configured automatically
# Test renewal
sudo certbot renew --dry-run
```

#### 5.3 Configure Automatic Restart

```bash
# Create systemd service
sudo nano /etc/systemd/system/blog-portfolio.service
```

**Add:**

```ini
[Unit]
Description=Blog Portfolio Docker Compose
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ubuntu/apps/das-blog-post
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

**Enable service:**

```bash
sudo systemctl enable blog-portfolio
sudo systemctl start blog-portfolio
sudo systemctl status blog-portfolio
```

### Phase 6: Database Backup Setup

#### 6.1 Create S3 Bucket for Backups

```bash
# Create S3 bucket
aws s3 mb s3://blog-portfolio-backups-<unique-id>

# Enable versioning
aws s3api put-bucket-versioning \
    --bucket blog-portfolio-backups-<unique-id> \
    --versioning-configuration Status=Enabled
```

#### 6.2 Create Backup Script

```bash
# Create backup directory
mkdir -p /home/ubuntu/backups

# Create backup script
nano /home/ubuntu/backups/backup.sh
```

**Add:**

```bash
#!/bin/bash

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/home/ubuntu/backups"
S3_BUCKET="s3://blog-portfolio-backups-<unique-id>"

# Backup PostgreSQL
docker exec postgres pg_dump -U postgres auth_db > $BACKUP_DIR/postgres_$DATE.sql
gzip $BACKUP_DIR/postgres_$DATE.sql

# Backup MongoDB
docker exec mongodb mongodump --out=/backup --db=blog_db
docker cp mongodb:/backup $BACKUP_DIR/mongodb_$DATE
tar -czf $BACKUP_DIR/mongodb_$DATE.tar.gz -C $BACKUP_DIR mongodb_$DATE
rm -rf $BACKUP_DIR/mongodb_$DATE

# Backup MySQL
docker exec mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD portfolio_db > $BACKUP_DIR/mysql_$DATE.sql
gzip $BACKUP_DIR/mysql_$DATE.sql

# Upload to S3
aws s3 sync $BACKUP_DIR $S3_BUCKET

# Clean old local backups (keep last 7 days)
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "Backup completed: $DATE"
```

**Make executable:**

```bash
chmod +x /home/ubuntu/backups/backup.sh
```

#### 6.3 Schedule Automatic Backups

```bash
# Add to crontab
crontab -e

# Add this line (daily at 2 AM)
0 2 * * * /home/ubuntu/backups/backup.sh >> /home/ubuntu/backups/backup.log 2>&1
```

---

## Post-Deployment Configuration

### Configure Domain (Optional)

#### Using Route 53

1. **Register domain** or transfer existing domain to Route 53
2. **Create hosted zone**
3. **Create A record** pointing to Elastic IP
4. **Update SSL certificate** with Certbot

```bash
sudo certbot --nginx -d yourdomain.com
```

#### Using External DNS Provider

1. **Add A record** in your DNS provider:
   ```
   Type: A
   Name: @
   Value: <your-ec2-elastic-ip>
   TTL: 3600
   ```

2. **Add CNAME for www:**
   ```
   Type: CNAME
   Name: www
   Value: yourdomain.com
   TTL: 3600
   ```

### Configure Email Notifications

For guest author notifications, configure SMTP settings in `.env`:

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

**Gmail App Password Setup:**
1. Enable 2FA on Gmail
2. Go to Security → App Passwords
3. Generate app password for "Mail"
4. Use generated password in `.env`

---

## Monitoring and Maintenance

### CloudWatch Setup

#### Install CloudWatch Agent

```bash
# Download agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb

# Install
sudo dpkg -i amazon-cloudwatch-agent.deb

# Configure
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-config-wizard
```

#### Monitor Docker Logs

```bash
# View all logs
docker-compose logs -f

# View specific service
docker-compose logs -f blog-service

# View last 100 lines
docker-compose logs --tail=100 frontend
```

### Health Checks

Create a monitoring script:

```bash
nano /home/ubuntu/monitor.sh
```

```bash
#!/bin/bash

# Check if services are running
services=("frontend" "auth-service" "blog-service" "portfolio-service")

for service in "${services[@]}"
do
    if ! docker-compose ps | grep -q "$service.*Up"; then
        echo "Service $service is down! Restarting..."
        docker-compose restart $service
    fi
done

# Check disk space
usage=$(df -h / | awk 'NR==2 {print $5}' | cut -d'%' -f1)
if [ $usage -gt 80 ]; then
    echo "Disk usage is above 80%: $usage%"
fi
```

```bash
chmod +x /home/ubuntu/monitor.sh

# Add to crontab (every 5 minutes)
*/5 * * * * /home/ubuntu/monitor.sh >> /home/ubuntu/monitor.log 2>&1
```

### Update Application

```bash
cd /home/ubuntu/apps/das-blog-post

# Pull latest code
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose up -d --build

# Verify
docker-compose ps
```

---

## Cost Optimization

### Stay Within Free Tier

1. **Monitor usage** in AWS Billing Dashboard
2. **Set up billing alerts** at $5, $10, $15
3. **Stop instance when not needed** (development)

```bash
# Stop instance
aws ec2 stop-instances --instance-ids <instance-id>

# Start instance
aws ec2 start-instances --instance-ids <instance-id>
```

4. **Use CloudWatch free tier** (10 metrics)
5. **Optimize Docker images** to reduce storage

### Clean Up Resources

```bash
# Remove unused Docker images
docker system prune -a

# Remove old logs
sudo journalctl --vacuum-time=7d
```

---

## Troubleshooting

### Services Won't Start

```bash
# Check logs
docker-compose logs

# Check disk space
df -h

# Check memory
free -m

# Restart services
docker-compose restart
```

### Can't Connect to Application

```bash
# Check security group rules
aws ec2 describe-security-groups --group-ids <sg-id>

# Check nginx status
sudo systemctl status nginx

# Check if ports are listening
sudo netstat -tlnp | grep -E ':(80|3000|3001|3002|3003)'

# Test locally
curl http://localhost:3000
```

### Database Connection Issues

```bash
# Check database logs
docker-compose logs postgres
docker-compose logs mongodb
docker-compose logs mysql

# Connect to database container
docker exec -it postgres psql -U postgres -d auth_db
docker exec -it mongodb mongo
docker exec -it mysql mysql -u root -p
```

### Out of Memory

```bash
# Check memory usage
docker stats

# Add swap space
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# Make permanent
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

---

## Security Best Practices

1. **Regular Updates:**
   ```bash
   sudo apt-get update && sudo apt-get upgrade -y
   ```

2. **Firewall Configuration:**
   ```bash
   sudo ufw enable
   sudo ufw allow 22/tcp
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   ```

3. **Change Default Passwords** in `.env`

4. **Disable Password Authentication:**
   ```bash
   sudo nano /etc/ssh/sshd_config
   # Set: PasswordAuthentication no
   sudo systemctl restart sshd
   ```

5. **Enable Automatic Security Updates:**
   ```bash
   sudo apt-get install unattended-upgrades
   sudo dpkg-reconfigure -plow unattended-upgrades
   ```

---

## Next Steps

1. **Setup monitoring** with CloudWatch
2. **Configure backups** to S3
3. **Setup CI/CD** with GitHub Actions
4. **Implement caching** with Redis (separate guide)
5. **Scale services** with ECS (when outgrowing Free Tier)

---

## Additional Resources

- [AWS Free Tier](https://aws.amazon.com/free/)
- [Docker Documentation](https://docs.docker.com/)
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Let's Encrypt](https://letsencrypt.org/)
- [AWS CLI Reference](https://docs.aws.amazon.com/cli/)

---

## Support

For issues or questions:
1. Check [API Documentation](./API_DOCUMENTATION.md)
2. Review [GraphQL Documentation](./GRAPHQL_DOCUMENTATION.md)
3. Check application logs: `docker-compose logs`
4. Review AWS CloudWatch logs

**Estimated deployment time:** 2-3 hours for first-time setup
