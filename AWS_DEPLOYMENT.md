# AWS Deployment Guide - Free Tier

Complete guide to deploying the DAS Blog & Portfolio Platform on AWS Free Tier.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                         AWS Cloud                            │
│                                                              │
│  ┌──────────────┐                                           │
│  │ CloudFront   │  (Frontend CDN)                          │
│  │   + S3       │                                           │
│  └──────┬───────┘                                           │
│         │                                                    │
│  ┌──────▼──────────────────────────────────────────┐       │
│  │         Application Load Balancer (ALB)          │       │
│  └──────┬───────────────┬────────────┬─────────────┘       │
│         │               │            │                      │
│  ┌──────▼────┐   ┌─────▼────┐  ┌───▼─────┐  ┌──────────┐ │
│  │ ECS/EC2   │   │ ECS/EC2  │  │ ECS/EC2 │  │ ECS/EC2  │ │
│  │ API       │   │   Auth   │  │  Blog   │  │Portfolio │ │
│  │ Gateway   │   │ Service  │  │ Service │  │ Service  │ │
│  │  :8000    │   │  :3001   │  │  :3002  │  │  :3003   │ │
│  └───────────┘   └──────────┘  └─────────┘  └──────────┘ │
│         │              │            │            │          │
│  ┌──────▼──────┐ ┌─────▼───┐  ┌───▼────┐  ┌────▼──────┐  │
│  │ElastiCache  │ │   RDS   │  │MongoDB │  │    RDS    │  │
│  │   Redis     │ │PostgreSQL│  │ Atlas  │  │   MySQL   │  │
│  └─────────────┘ └─────────┘  └────────┘  └───────────┘  │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              S3 (File Storage - Release Mode)        │   │
│  └─────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

## Prerequisites

1. AWS Account (Free Tier eligible)
2. AWS CLI installed and configured
3. Docker installed locally
4. Domain name (optional, Route53 or external DNS)

## Free Tier Limits

### Compute (ECS/EC2)
- **EC2**: 750 hours/month of t2.micro instances (12 months)
- **Fargate**: 50 GB-hours/month of memory + 200 hours of vCPU

### Database
- **RDS**: 750 hours/month db.t2.micro + 20GB storage
- **ElastiCache**: 750 hours/month cache.t2.micro (12 months)
- **MongoDB Atlas**: 512MB free tier (separate, not AWS)

### Storage & CDN
- **S3**: 5GB storage + 20,000 GET + 2,000 PUT requests/month
- **CloudFront**: 50GB data transfer out

### Networking
- **ALB**: Not free tier ($16-22/month)
- **Data Transfer**: 100GB/month outbound free

---

## Deployment Steps

### 1. Setup AWS Infrastructure

#### 1.1 Create VPC and Networking

```bash
# Create VPC
aws ec2 create-vpc \
  --cidr-block 10.0.0.0/16 \
  --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=das-blog-vpc}]'

# Get VPC ID
export VPC_ID=$(aws ec2 describe-vpcs --filters "Name=tag:Name,Values=das-blog-vpc" --query "Vpcs[0].VpcId" --output text)

# Create Public Subnets (for ALB)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.1.0/24 \
  --availability-zone us-east-1a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=das-blog-public-1a}]'

aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.2.0/24 \
  --availability-zone us-east-1b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=das-blog-public-1b}]'

# Create Private Subnets (for ECS/EC2)
aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.11.0/24 \
  --availability-zone us-east-1a \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=das-blog-private-1a}]'

aws ec2 create-subnet \
  --vpc-id $VPC_ID \
  --cidr-block 10.0.12.0/24 \
  --availability-zone us-east-1b \
  --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=das-blog-private-1b}]'

# Create Internet Gateway
aws ec2 create-internet-gateway \
  --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=das-blog-igw}]'

export IGW_ID=$(aws ec2 describe-internet-gateways --filters "Name=tag:Name,Values=das-blog-igw" --query "InternetGateways[0].InternetGatewayId" --output text)

# Attach Internet Gateway to VPC
aws ec2 attach-internet-gateway --vpc-id $VPC_ID --internet-gateway-id $IGW_ID
```

#### 1.2 Setup Security Groups

```bash
# ALB Security Group (allow HTTP/HTTPS from internet)
aws ec2 create-security-group \
  --group-name das-blog-alb-sg \
  --description "Security group for ALB" \
  --vpc-id $VPC_ID

export ALB_SG=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=das-blog-alb-sg" --query "SecurityGroups[0].GroupId" --output text)

aws ec2 authorize-security-group-ingress --group-id $ALB_SG --protocol tcp --port 80 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-id $ALB_SG --protocol tcp --port 443 --cidr 0.0.0.0/0

# Application Security Group (allow traffic from ALB)
aws ec2 create-security-group \
  --group-name das-blog-app-sg \
  --description "Security group for applications" \
  --vpc-id $VPC_ID

export APP_SG=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=das-blog-app-sg" --query "SecurityGroups[0].GroupId" --output text)

aws ec2 authorize-security-group-ingress --group-id $APP_SG --protocol tcp --port 8000 --source-group $ALB_SG
aws ec2 authorize-security-group-ingress --group-id $APP_SG --protocol tcp --port 3001 --source-group $APP_SG
aws ec2 authorize-security-group-ingress --group-id $APP_SG --protocol tcp --port 3002 --source-group $APP_SG
aws ec2 authorize-security-group-ingress --group-id $APP_SG --protocol tcp --port 3003 --source-group $APP_SG

# Database Security Group
aws ec2 create-security-group \
  --group-name das-blog-db-sg \
  --description "Security group for databases" \
  --vpc-id $VPC_ID

export DB_SG=$(aws ec2 describe-security-groups --filters "Name=group-name,Values=das-blog-db-sg" --query "SecurityGroups[0].GroupId" --output text)

aws ec2 authorize-security-group-ingress --group-id $DB_SG --protocol tcp --port 5432 --source-group $APP_SG  # PostgreSQL
aws ec2 authorize-security-group-ingress --group-id $DB_SG --protocol tcp --port 3306 --source-group $APP_SG  # MySQL
aws ec2 authorize-security-group-ingress --group-id $DB_SG --protocol tcp --port 6379 --source-group $APP_SG  # Redis
```

### 2. Setup Databases

#### 2.1 RDS PostgreSQL (Auth Service)

```bash
# Create DB Subnet Group
aws rds create-db-subnet-group \
  --db-subnet-group-name das-blog-db-subnet-group \
  --db-subnet-group-description "Subnet group for RDS databases" \
  --subnet-ids subnet-xxx subnet-yyy  # Replace with your private subnet IDs

# Create PostgreSQL Instance
aws rds create-db-instance \
  --db-instance-identifier das-blog-postgres \
  --db-instance-class db.t2.micro \
  --engine postgres \
  --engine-version 16.1 \
  --master-username postgres \
  --master-user-password 'YourStrongPassword123!' \
  --allocated-storage 20 \
  --db-name auth_db \
  --vpc-security-group-ids $DB_SG \
  --db-subnet-group-name das-blog-db-subnet-group \
  --publicly-accessible false \
  --backup-retention-period 7 \
  --storage-encrypted
```

#### 2.2 RDS MySQL (Portfolio Service)

```bash
aws rds create-db-instance \
  --db-instance-identifier das-blog-mysql \
  --db-instance-class db.t2.micro \
  --engine mysql \
  --engine-version 8.0.35 \
  --master-username root \
  --master-user-password 'YourStrongPassword123!' \
  --allocated-storage 20 \
  --db-name portfolio_db \
  --vpc-security-group-ids $DB_SG \
  --db-subnet-group-name das-blog-db-subnet-group \
  --publicly-accessible false \
  --backup-retention-period 7 \
  --storage-encrypted
```

#### 2.3 ElastiCache Redis (Rate Limiting)

```bash
# Create Cache Subnet Group
aws elasticache create-cache-subnet-group \
  --cache-subnet-group-name das-blog-cache-subnet-group \
  --cache-subnet-group-description "Subnet group for ElastiCache" \
  --subnet-ids subnet-xxx subnet-yyy  # Replace with your private subnet IDs

# Create Redis Cluster
aws elasticache create-cache-cluster \
  --cache-cluster-id das-blog-redis \
  --cache-node-type cache.t2.micro \
  --engine redis \
  --engine-version 7.0 \
  --num-cache-nodes 1 \
  --cache-subnet-group-name das-blog-cache-subnet-group \
  --security-group-ids $DB_SG
```

#### 2.4 MongoDB Atlas (Blog Service)

1. Go to https://www.mongodb.com/cloud/atlas
2. Create free tier cluster (M0 - 512MB)
3. Whitelist AWS IP ranges
4. Get connection string

### 3. Setup S3 for File Storage

```bash
# Create S3 bucket for file uploads
aws s3 mb s3://das-blog-uploads --region us-east-1

# Configure bucket CORS
cat > cors.json << 'EOF'
{
  "CORSRules": [
    {
      "AllowedHeaders": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedOrigins": ["*"],
      "ExposeHeaders": ["ETag"]
    }
  ]
}
EOF

aws s3api put-bucket-cors --bucket das-blog-uploads --cors-configuration file://cors.json

# Create bucket policy for public read (optional)
cat > bucket-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::das-blog-uploads/*"
    }
  ]
}
EOF

aws s3api put-bucket-policy --bucket das-blog-uploads --policy file://bucket-policy.json
```

### 4. Deploy Microservices

#### Option A: ECS Fargate (Recommended for Free Tier)

```bash
# Create ECS Cluster
aws ecs create-cluster --cluster-name das-blog-cluster

# Create ECR Repositories
aws ecr create-repository --repository-name das-blog/api-gateway
aws ecr create-repository --repository-name das-blog/auth-service
aws ecr create-repository --repository-name das-blog/blog-service
aws ecr create-repository --repository-name das-blog/portfolio-service

# Build and push Docker images
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export AWS_REGION=us-east-1

# Login to ECR
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com

# Build and push API Gateway
cd backend/api-gateway
docker build -t das-blog/api-gateway .
docker tag das-blog/api-gateway:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/api-gateway:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/api-gateway:latest

# Build and push Auth Service
cd ../auth-service
docker build -t das-blog/auth-service .
docker tag das-blog/auth-service:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/auth-service:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/auth-service:latest

# Build and push Blog Service
cd ../blog-service
docker build -t das-blog/blog-service .
docker tag das-blog/blog-service:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/blog-service:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/blog-service:latest

# Build and push Portfolio Service
cd ../portfolio-service
docker build -t das-blog/portfolio-service .
docker tag das-blog/portfolio-service:latest $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/portfolio-service:latest
docker push $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/das-blog/portfolio-service:latest
```

#### Create ECS Task Definitions

Create `task-definition-api-gateway.json`:

```json
{
  "family": "das-blog-api-gateway",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "api-gateway",
      "image": "<AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/das-blog/api-gateway:latest",
      "portMappings": [
        {
          "containerPort": 8000,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {"name": "APP_MODE", "value": "release"},
        {"name": "REDIS_HOST", "value": "<REDIS_ENDPOINT>"},
        {"name": "AUTH_SERVICE_URL", "value": "http://<AUTH_SERVICE_PRIVATE_IP>:3001"},
        {"name": "BLOG_SERVICE_URL", "value": "http://<BLOG_SERVICE_PRIVATE_IP>:3002"},
        {"name": "PORTFOLIO_SERVICE_URL", "value": "http://<PORTFOLIO_SERVICE_PRIVATE_IP>:3003"}
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/das-blog-api-gateway",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

Register and create services:

```bash
# Create CloudWatch log groups
aws logs create-log-group --log-group-name /ecs/das-blog-api-gateway
aws logs create-log-group --log-group-name /ecs/das-blog-auth-service
aws logs create-log-group --log-group-name /ecs/das-blog-blog-service
aws logs create-log-group --log-group-name /ecs/das-blog-portfolio-service

# Register task definitions
aws ecs register-task-definition --cli-input-json file://task-definition-api-gateway.json
# ... repeat for other services

# Create ECS Services
aws ecs create-service \
  --cluster das-blog-cluster \
  --service-name api-gateway \
  --task-definition das-blog-api-gateway \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx,subnet-yyy],securityGroups=[$APP_SG],assignPublicIp=DISABLED}"
```

#### Option B: EC2 (Alternative)

```bash
# Launch EC2 instance
aws ec2 run-instances \
  --image-id ami-0c55b159cbfafe1f0 \
  --instance-type t2.micro \
  --key-name your-key-pair \
  --security-group-ids $APP_SG \
  --subnet-id subnet-xxx \
  --user-data file://user-data.sh \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=das-blog-app}]'
```

Create `user-data.sh`:

```bash
#!/bin/bash
yum update -y
yum install -y docker
service docker start
usermod -a -G docker ec2-user

# Install docker-compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Clone repository and start services
# ... (configure with release mode)
```

### 5. Setup Application Load Balancer

```bash
# Create ALB
aws elbv2 create-load-balancer \
  --name das-blog-alb \
  --subnets subnet-public-1a subnet-public-1b \
  --security-groups $ALB_SG \
  --scheme internet-facing

export ALB_ARN=$(aws elbv2 describe-load-balancers --names das-blog-alb --query "LoadBalancers[0].LoadBalancerArn" --output text)

# Create Target Group for API Gateway
aws elbv2 create-target-group \
  --name das-blog-api-gateway-tg \
  --protocol HTTP \
  --port 8000 \
  --vpc-id $VPC_ID \
  --target-type ip \
  --health-check-path /health

export TG_ARN=$(aws elbv2 describe-target-groups --names das-blog-api-gateway-tg --query "TargetGroups[0].TargetGroupArn" --output text)

# Create Listener
aws elbv2 create-listener \
  --load-balancer-arn $ALB_ARN \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=forward,TargetGroupArn=$TG_ARN
```

### 6. Deploy Frontend to S3 + CloudFront

```bash
# Create S3 bucket for frontend
aws s3 mb s3://das-blog-frontend --region us-east-1

# Configure bucket for static website hosting
aws s3 website s3://das-blog-frontend --index-document index.html --error-document index.html

# Build frontend
cd frontend
npm install
npm run build

# Upload to S3
aws s3 sync dist/ s3://das-blog-frontend --delete

# Create CloudFront distribution
aws cloudfront create-distribution \
  --origin-domain-name das-blog-frontend.s3.amazonaws.com \
  --default-root-object index.html
```

### 7. Update Environment Variables

Update all services with release mode configuration:

```env
APP_MODE=release
AWS_REGION=us-east-1
AWS_S3_BUCKET=das-blog-uploads
AWS_ACCESS_KEY_ID=<your-key>
AWS_SECRET_ACCESS_KEY=<your-secret>
REDIS_HOST=<elasticache-endpoint>
DB_HOST=<rds-postgres-endpoint>
MYSQL_HOST=<rds-mysql-endpoint>
MONGODB_URI=<mongodb-atlas-uri>
```

---

## Cost Estimation (After Free Tier)

| Service | Monthly Cost |
|---------|-------------|
| EC2 t2.micro (1 instance) | $8.50 |
| RDS db.t2.micro (2 instances) | $30 |
| ElastiCache cache.t2.micro | $12 |
| ALB | $16-22 |
| S3 (10GB + requests) | $1-5 |
| CloudFront (50GB transfer) | Free tier |
| **Total** | **~$67-77/month** |

**Free Tier Deployment (First 12 months)**: ~$16-22/month (ALB only)

---

## Monitoring & Maintenance

### CloudWatch Alarms

```bash
# CPU Utilization
aws cloudwatch put-metric-alarm \
  --alarm-name high-cpu-api-gateway \
  --alarm-description "Alert when CPU exceeds 80%" \
  --metric-name CPUUtilization \
  --namespace AWS/ECS \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2
```

### Logs

```bash
# View logs
aws logs tail /ecs/das-blog-api-gateway --follow
```

---

## Scaling Configuration

### Auto Scaling for ECS

```bash
# Register scalable target
aws application-autoscaling register-scalable-target \
  --service-namespace ecs \
  --resource-id service/das-blog-cluster/api-gateway \
  --scalable-dimension ecs:service:DesiredCount \
  --min-capacity 1 \
  --max-capacity 4

# Create scaling policy
aws application-autoscaling put-scaling-policy \
  --service-namespace ecs \
  --resource-id service/das-blog-cluster/api-gateway \
  --scalable-dimension ecs:service:DesiredCount \
  --policy-name cpu-scaling-policy \
  --policy-type TargetTrackingScaling \
  --target-tracking-scaling-policy-configuration file://scaling-policy.json
```

---

## Backup Strategy

### RDS Automated Backups
- Retention: 7 days (configured during creation)
- Point-in-time recovery enabled

### S3 Versioning
```bash
aws s3api put-bucket-versioning --bucket das-blog-uploads --versioning-configuration Status=Enabled
```

---

## Security Best Practices

1. **Use IAM Roles** instead of access keys where possible
2. **Enable SSL/TLS** - Add ACM certificate to ALB
3. **Enable WAF** - Protect against common web attacks
4. **Use Secrets Manager** for sensitive data
5. **Enable VPC Flow Logs** for network monitoring
6. **Regular security updates** for all services

---

## Rollback Procedure

```bash
# Rollback ECS service to previous task definition
aws ecs update-service \
  --cluster das-blog-cluster \
  --service api-gateway \
  --task-definition das-blog-api-gateway:PREVIOUS_REVISION
```

---

## Troubleshooting

### Service Unhealthy
1. Check ECS task logs: `aws logs tail /ecs/das-blog-api-gateway`
2. Verify security group rules
3. Check database connectivity
4. Verify environment variables

### High Latency
1. Check CloudWatch metrics
2. Verify database performance
3. Check Redis connectivity
4. Review ALB target group health

### Connection Timeout
1. Verify security groups allow traffic
2. Check route tables
3. Verify NAT gateway for private subnets
4. Check service discovery configuration

---

## Additional Resources

- [AWS Free Tier Details](https://aws.amazon.com/free/)
- [ECS Best Practices](https://docs.aws.amazon.com/AmazonECS/latest/bestpracticesguide/)
- [RDS Free Tier](https://aws.amazon.com/rds/free/)
- [MongoDB Atlas Free Tier](https://www.mongodb.com/pricing)
