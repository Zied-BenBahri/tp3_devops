pipeline {
  agent any
  parameters {
    booleanParam(name: 'BUILD_AND_PUSH', defaultValue: false, description: 'Build & push Docker image')
  }
  environment {
    DOCKER_IMAGE = 'ziedbenbahri/tp3-devops-hello'
  }
  stages {
    stage('Cloner le dépôt') {
      steps { git branch: 'main', url: 'https://github.com/Zied-BenBahri/tp3_devops.git' }
    }

    stage('Construire l\'image Docker') {
      when { expression { params.BUILD_AND_PUSH } }
      steps { sh 'docker build -t $DOCKER_IMAGE .' }
    }

    stage('Pousser l\'image Docker') {
      when { expression { params.BUILD_AND_PUSH } }
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
          sh '''
            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
            docker push "$DOCKER_IMAGE"
          '''
        }
      }
    }

    stage('Déployer sur Kubernetes') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig-dev', variable: 'KCFG')]) {
          sh '''
            export KUBECONFIG="$KCFG"
            kubectl apply -f deployment.yaml
            kubectl apply -f service.yaml
            kubectl rollout status deployment/mon-app-deployment --timeout=180s || true
          '''
        }
      }
    }
  }
}
