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

        stage('Build & Dockerize Auth') {
            steps {
                dir("services/circleguard-auth-service") {
                    sh "../../gradlew clean build --no-daemon -Dorg.gradle.jvmargs='-Xmx512m'"
                    sh "docker build -t auth-service:latest ."
                }
            }
            post {
                always {
                    junit 'services/circleguard-auth-service/build/test-results/**/*.xml'
                }
            }
        }

        stage('Build & Dockerize Identity') {
            steps {
                dir("services/circleguard-identity-service") {
                    sh "../../gradlew clean build --no-daemon -Dorg.gradle.jvmargs='-Xmx512m'"
                    sh "docker build -t identity-service:latest ."
                }
            }
            post {
                always {
                    junit 'services/circleguard-identity-service/build/test-results/**/*.xml'
                }
            }
        }

        stage('Build & Dockerize Gateway') {
            steps {
                dir("services/circleguard-gateway-service") {
                    sh "../../gradlew clean build --no-daemon -Dorg.gradle.jvmargs='-Xmx512m'"
                    sh "docker build -t gateway-service:latest ."
                }
            }
            post {
                always {
                    junit 'services/circleguard-gateway-service/build/test-results/**/*.xml'
                }
            }
        }

        stage('Build & Dockerize Form') {
            steps {
                dir("services/circleguard-form-service") {
                    sh "../../gradlew clean build --no-daemon -Dorg.gradle.jvmargs='-Xmx512m'"
                    sh "docker build -t form-service:latest ."
                }
            }
            post {
                always {
                    junit 'services/circleguard-form-service/build/test-results/**/*.xml'
                }
            }
        }

        stage('Build & Dockerize Notification') {
            steps {
                dir("services/circleguard-notification-service") {
                    sh "../../gradlew clean build --no-daemon -Dorg.gradle.jvmargs='-Xmx512m'"
                    sh "docker build -t notification-service:latest ."
                }
            }
            post {
                always {
                    junit 'services/circleguard-notification-service/build/test-results/**/*.xml'
                }
            }
        }

        stage('Build & Dockerize Promotion') {
            steps {
                dir("services/circleguard-promotion-service") {
                    // Se saltan los tests porque usan Testcontainers (Neo4j) 
                    // que requieren acceso al Docker daemon, incompatible con muchos setups de Jenkins.
                    sh "../../gradlew clean build -x test --no-daemon -Dorg.gradle.jvmargs='-Xmx512m'"
                    sh "docker build -t promotion-service:latest ."
                }
            }
        }

        stage('Deploy Infrastructure') {
            steps {
                sh "cp /root/.kube/config ${KUBECONFIG_PATH}"
                sh "sed -i 's/127.0.0.1/host.docker.internal/g' ${KUBECONFIG_PATH}"
                
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
                    sh "kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f - --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/postgres.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl apply -f k8s/stage/infrastructure.yaml -n $NAMESPACE --insecure-skip-tls-verify"
                    
                    echo "Waiting for infrastructure (Postgres, Kafka, etc.) to stabilize..."
                    sh "sleep 60"
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
                    
                    // Asegurar que los despliegues terminaron correctamente
                    sh "kubectl rollout restart deployment/auth-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/auth-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"
                    
                    sh "kubectl rollout restart deployment/identity-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/identity-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/form-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/form-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/notification-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/notification-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/promotion-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/promotion-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/gateway-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/gateway-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"
                }
            }
        }

        stage('Smoke Tests') {
            steps {
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
                    sh '''
                        kubectl port-forward -n ${NAMESPACE} svc/auth-service         8180:8180 &
                        kubectl port-forward -n ${NAMESPACE} svc/identity-service     8083:8083 &
                        kubectl port-forward -n ${NAMESPACE} svc/form-service         8086:8086 &
                        kubectl port-forward -n ${NAMESPACE} svc/gateway-service      8087:8087 &
                        kubectl port-forward -n ${NAMESPACE} svc/promotion-service    8088:8088 &
                        kubectl port-forward -n ${NAMESPACE} svc/notification-service 8082:8082 &
                        sleep 15

                        FAILED=0
                        for PORT in 8180 8083 8086 8087 8088 8082; do
                            STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${PORT}/actuator/health)
                            if [ "$STATUS" = "200" ]; then
                                echo "SMOKE OK  - port ${PORT} -> HTTP ${STATUS}"
                            else
                                echo "SMOKE FAIL - port ${PORT} -> HTTP ${STATUS}"
                                FAILED=1
                            fi
                        done

                        pkill -f "kubectl port-forward" || true
                        exit $FAILED
                    '''
                }
            }
        }
    }
    
    post {
        success { echo "¡Build ${BUILD_NUMBER} desplegado y verificado exitosamente!" }
        failure { echo "El pipeline falló en el build ${BUILD_NUMBER}. Revisa los logs y resultados de JUnit." }
        always {
            sh "rm -f ${KUBECONFIG_PATH}"
            cleanWs()
        }
    }
}
