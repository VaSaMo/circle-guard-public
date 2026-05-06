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
                    
                    // Asegurar que los despliegues terminaron correctamente
                    sh "kubectl rollout restart deployment/auth-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/auth-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"
                    
                    sh "kubectl rollout restart deployment/identity-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/identity-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/gateway-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/gateway-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/form-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/form-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/notification-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/notification-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"

                    sh "kubectl rollout restart deployment/promotion-service -n $NAMESPACE --insecure-skip-tls-verify"
                    sh "kubectl rollout status deployment/promotion-service -n $NAMESPACE --timeout=300s --insecure-skip-tls-verify"
                }
            }
        }

        stage('Verify & Smoke Tests') {
            steps {
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
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

        stage('Functional Tests (E2E)') {
            steps {
                withEnv(["KUBECONFIG=${KUBECONFIG_PATH}"]) {
                    echo "Running basic functional checks against deployed services..."
                    sh "kubectl exec -n $NAMESPACE deployment/gateway-service -- curl -s http://localhost:8087/actuator/info"
                    sh "kubectl exec -n $NAMESPACE deployment/auth-service -- curl -s http://localhost:8180/actuator/info"
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
