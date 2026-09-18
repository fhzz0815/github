// 智慧社区AI系统 - Jenkins Pipeline
pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'registry.example.com'
        APP_NAME = 'smart-community'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Lint & Type Check') {
            steps {
                sh '''
                    python -m pip install -r requirements/dev.txt
                    ruff check app/ tests/
                    mypy app/
                '''
            }
        }

        stage('Test') {
            steps {
                sh 'pytest tests/ -q --cov=app --cov-report=term'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${DOCKER_REGISTRY}/${APP_NAME}:${BUILD_NUMBER} .
                    docker tag ${DOCKER_REGISTRY}/${APP_NAME}:${BUILD_NUMBER} ${DOCKER_REGISTRY}/${APP_NAME}:latest
                """
            }
        }

        stage('Push Image') {
            steps {
                sh """
                    docker push ${DOCKER_REGISTRY}/${APP_NAME}:${BUILD_NUMBER}
                    docker push ${DOCKER_REGISTRY}/${APP_NAME}:latest
                """
            }
        }
    }

    post {
        failure {
            emailext(
                subject: "构建失败: ${APP_NAME} #${BUILD_NUMBER}",
                body: "项目 ${APP_NAME} 构建失败，请检查 Jenkins 控制台输出。",
                to: 'dev@example.com'
            )
        }
    }
}
