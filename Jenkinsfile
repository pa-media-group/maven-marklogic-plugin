def tag = 'unknown'
def buildArgs = [:]
def latest = true
def current = false

pipeline {
  agent {
    label 'AWS_Jenkins-slave'
  }

  libraries {
    lib 'jenkins-ci-tad'
  }

  // Add the Environment variable required for the Maven Git versioning extension
  environment {
    // Override the Git author/committer name and e-mail address for Semantic Release commits. Utilised in conjunction with the 'Ignore Committer Strategy' Jenkins plugin.
    GIT_AUTHOR_EMAIL = 'semantic-release@pamediagroup.com'
    GIT_AUTHOR_NAME = 'semantic-release'
    GIT_COMMITTER_EMAIL = 'semantic-release@pamediagroup.com'
    GIT_COMMITTER_NAME = 'semantic-release'

    registry = 'registry.pressassociation.io'
    registryCredential = '13efcab7-d472-4c8e-836e-d60b26dc754c'
    VERSIONING_GIT_BRANCH = sh(script: 'echo $GIT_BRANCH', , returnStdout: true).trim()
    GH_TOKEN = credentials('GithubPAT')
    GIT_CREDENTIALS = credentials('github_semantic_release')
  }

  options {
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '5'))
    timeout(time: 15, unit: 'MINUTES')
  }

  parameters {
    string(name: 'outputContainerName', defaultValue: 'maven-marklogic-plugin', description: '')
    string(name: 'ciChannel', defaultValue: 'banacek-jenkins', description: 'Slack Notification Channel')
  }

  tools {
    maven 'M3'
    jdk 'JDK6'
    nodejs 'Node10.18.0'
  }

  stages {
    stage('Notify Started') {
      steps {
        notifyStarted(params.ciChannel)
      }
    }

    stage('Build and Test') {
      steps {
        script {
          mvn "clean package"
        }
      }
    }

    stage('Semantic Release') {
      when {
        anyOf {
          branch 'master'
        }
      }
      steps {
        withNPM(npmrcConfig: 'basenpmrc') {
          sh 'npm ci'
          sh 'npm run semantic-release:ci'
        }
      }
    }

    stage ('Deploy Maven Artifacts') {
      when {
        anyOf {
          branch 'bugfix/**'
          branch 'feature/**'
          branch 'hotfix/**'
          branch 'master'
        }
      }
      steps {
        mvn "deploy -DskipTests"
      }
    }
  }

  post {
    success {
      script {
        currentBuild.result = "SUCCESS"
      }
    }
    failure {
      script {
        currentBuild.result = "FAILURE"
      }
    }
    cleanup {
      notifyResults(params.ciChannel)
      cleanWs()
    }
  }
}
