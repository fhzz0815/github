pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = 'registry.example.com'
        BACKEND_IMAGE = "${DOCKER_REGISTRY}/smart-restaurant/backend"
        PROJECT_DIR = 'sec-backend'
    }
    stages {
        stage('拉取代码') {
            steps {
                checkout scm
            }
        }
        stage('Maven构建') {
            steps {
                sh 'cd ${PROJECT_DIR} && mvn -B clean package -DskipTests'
            }
        }
        stage('单元测试') {
            steps {
                sh 'cd ${PROJECT_DIR} && mvn -B test'
            }
            post {
                always {
                    junit '${PROJECT_DIR}/target/surefire-reports/*.xml'
                }
            }
        }
        stage('构建镜像') {
            steps {
                sh '''
                    docker build -t ${BACKEND_IMAGE}:${BUILD_NUMBER} ${PROJECT_DIR}
                    docker tag ${BACKEND_IMAGE}:${BUILD_NUMBER} ${BACKEND_IMAGE}:latest
                '''
            }
        }
        stage('推送镜像') {
            steps {
                sh '''
                    docker push ${BACKEND_IMAGE}:${BUILD_NUMBER}
                    docker push ${BACKEND_IMAGE}:latest
                '''
            }
        }
        stage('远程部署') {
            steps {
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: 'prod-server',
                            transfers: [
                                sshTransfer(
                                    execCommand: '''
                                        sh /opt/sr/scripts/pre-clean.sh
                                        docker pull ${BACKEND_IMAGE}:latest
                                        cd /opt/sr && docker-compose up -d backend1 backend2
                                        docker system prune -f
                                    '''
                                )
                            ]
                        )
                    ]
                )
            }
        }
    }
    post {
        failure {
            emailext(
                subject: "构建失败: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "构建失败，请检查: ${env.BUILD_URL}",
                to: 'dev-team@example.com'
            )
        }
        success {
            emailext(
                subject: "构建成功: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "部署完成: ${env.BUILD_URL}",
                to: 'dev-team@example.com'
            )
        }
    }
}