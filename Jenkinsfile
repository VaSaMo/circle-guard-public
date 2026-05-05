pipeline {
    agent any

    environment {
        NAMESPACE = "taller2-stage"
        DOCKER_HOST = "unix:///var/run/docker.sock"
        KUBECONFIG_PATH = "/tmp/kubeconfig"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Dockerize Microservices') {
            parallel {
                stage('Auth Service') {
                    steps {
                        dir("services/circleguard-auth-service") {
                            sh "../../gradlew clean build -x test"
                            sh "docker build -t auth-service:latest ."
                        }
                    }
                }
                stage('Identity Service') {
                    steps {
                        dir("services/circleguard-identity-service") {
                            sh "../../gradlew clean build -x test"
                            sh "docker build -t identity-service:latest ."
                        }
                    }
                }
                stage('Gateway Service') {
                    steps {
                        dir("services/circleguard-gateway-service") {
                            sh "../../gradlew clean build -x test"
                            sh "docker build -t gateway-service:latest ."
                        }
                    }
                }
                stage('Form Service') {
                    steps {
                        dir("services/circleguard-form-service") {
                            sh "../../gradlew clean build -x test"
                            sh "docker build -t form-service:latest ."
                        }
                    }
                }
                stage('Notification Service') {
                    steps {
                        dir("services/circleguard-notification-service") {
                            sh "../../gradlew clean build -x test"
                            sh "docker build -t notification-service:latest ."
                        }
                    }
                }
                stage('Promotion Service') {
                    steps {
                        dir("services/circleguard-promotion-service") {
                            sh "../../gradlew clean build -x test"
                            sh "docker build -t promotion-service:latest ."
                        }
                    }
                }
            }
        }

        stage('Deploy Infrastructure') {
            steps {
                sh "cp /root/.kube/config ${KUBECONFIG_PATH}"
                sh "sed -i 's/127.0.0.1/host.docker.internal/g' ${KUBECONFIG_PATH}"
                
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
                    sh "kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -"
                    sh "kubectl apply -f k8s/stage/postgres.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/infrastructure.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                }
            }
        }

        stage('Deploy Microservices') {
            steps {
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
                    sh "kubectl apply -f k8s/stage/auth-service.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/identity-service.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/gateway-service.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/form-service.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/notification-service.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/promotion-service.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    
                    // Force restart to ensure the latest built images are used
                    sh "kubectl rollout restart deployment/auth-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout restart deployment/identity-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout restart deployment/gateway-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout restart deployment/form-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout restart deployment/notification-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout restart deployment/promotion-service -n $NAMESPACE --insecure-skip-tls-verify"
                }
            }
        }

        stage('Verify & Smoke Tests') {
            steps {
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
                    echo "Waiting for microservices to be ready..."
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/auth-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/identity-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/gateway-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/form-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/notification-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl wait --for=condition=available --timeout=300s deployment/promotion-service -n $NAMESPACE --insecure-skip-tls-verify"
                    
                    script {
                        def services = [
                            [name: 'auth-service', port: 8180],
                            [name: 'identity-service', port: 8083],
                            [name: 'gateway-service', port: 8087],
                            [name: 'form-service', port: 8086],
                            [name: 'notification-service', port: 8082],
                            [name: 'promotion-service', port: 8088]
                        ]
                        
                        services.each { svc ->
                            echo "Checking health for ${svc.name}..."
                            sh "kubectl exec -n $NAMESPACE deployment/${svc.name} -- curl -s http://localhost:${svc.port}/actuator/health | grep UP"
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            sh "rm -f ${KUBECONFIG_PATH}"
        }
    }
}
