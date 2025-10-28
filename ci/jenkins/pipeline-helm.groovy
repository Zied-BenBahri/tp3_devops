pipeline {
  agent any
  parameters {
    booleanParam(name: 'BUILD_AND_PUSH', defaultValue: false, description: 'Build & push Docker image before Helm deploy')
  }
  environment {
    DOCKER_IMAGE    = 'ziedbenbahri/tp3-devops-hello'
    HELM_CHART_PATH = './infra/helm/mon-app'
  }
  stages {
    stage('Cloner le dépôt') {
      steps {
        git branch: 'main', url: 'https://github.com/Zied-BenBahri/tp3_devops.git'
      }
    }

    stage('Check tools') {
      steps {
        sh 'which helm && helm version || true'
        sh 'which docker && docker version || true'
      }
    }

    stage('Construire l' + "'" + 'image Docker') {
      when { expression { params.BUILD_AND_PUSH } }
      steps {
        sh 'docker build -f infra/docker/Dockerfile -t $DOCKER_IMAGE .'
      }
    }

    stage('Pousser l' + "'" + 'image Docker') {
      when { expression { params.BUILD_AND_PUSH } }
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
          sh '''
            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
            docker push "$DOCKER_IMAGE"
          '''
        }
      }
    }

    stage('Déployer avec Helm') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig-dev', variable: 'KCFG')]) {
          sh '''
            set -e
            export KUBECONFIG="$KCFG"
            helm upgrade --install mon-app $HELM_CHART_PATH \
              --set image.repository=$DOCKER_IMAGE \
              --set image.tag=latest
            helm status mon-app
          '''
        }
      }
    }
  }
  post {
    success { echo "✅ Helm deploy done" }
    failure { echo "❌ Helm deploy failed" }
  }
}
