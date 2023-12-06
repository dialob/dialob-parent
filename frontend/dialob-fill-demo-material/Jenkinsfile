pipeline {
  agent {
    kubernetes {
      label "dialob-fill-demo-material"
      inheritFrom 'build-node'
    }
  }
  options {
    gitLabConnection('git.resys.io')
    gitlabCommitStatus(name: 'jenkins')
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '5'))
    disableConcurrentBuilds()
    skipStagesAfterUnstable()
  }
  environment {
    CI = true
    PUBLIC_URL = "//cdn.resys.io/${env.JOB_NAME}/"
  }
  triggers {
    gitlab(triggerOnPush: true, triggerOnMergeRequest: false, branchFilterType: 'All')
  }
  stages {
    stage('init') {
      steps {
        container('build-node') {
          configFileProvider([configFile(fileId: 'opValoNpmrcConfig', variable: 'NPMRC_FILE')]) {
            sh 'mv $NPMRC_FILE .npmrc'
            sh 'yarn install'
          }
        }
      }
    }
    stage('test') {
      steps {
        container('build-node') {
          sh 'yarn test --passWithNoTests'
        }
      }
    }
    stage('build') {
      steps {
        container('build-node') {
          sh 'yarn build'
        }
      }
    }
    stage('Publish to cdn') {
      steps {
        container('build-node') {
          withCredentials([[
            $class: 'AmazonWebServicesCredentialsBinding',
            credentialsId: 'aws-cdn-update',
            accessKeyVariable: 'AWS_ACCESS_KEY_ID',
            secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
          ]]) {
            withAWS(region:'eu-central-1') {
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.html', contentType: 'text/html', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.js', contentType: 'application/javascript', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.css', contentType: 'text/css', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.json', contentType: 'application/json', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.ico', contentType: 'image/x-icon', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.woff2', contentType: 'application/font-woff2', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.png', contentType: 'image/png', cacheControl:'public,max-age=3600',acl:'PublicRead')
              s3Upload(bucket:'cdn.resys.io', path:"${env.JOB_NAME}", workingDir:'build', includePathPattern:'**/*.gif', contentType: 'image/gif', cacheControl:'public,max-age=3600',acl:'PublicRead')
            }
          }
        }
      }
    }
  }
  post {
    failure {
      updateGitlabCommitStatus(state: 'failed')
      withCredentials([string(credentialsId: 'google-chat-jenkins-channel', variable: 'chat_url')]) {
        httpRequest httpMode:'POST', contentType: 'APPLICATION_JSON', url: "${chat_url}&thread_key=${env.JOB_NAME}", requestBody: "{\"sender\":{\"avatarUrl\":\"https://ci.resys.io/images/48x48/red.png\",\"displayName\":\"Jenkins job ${env.JOB_NAME}\"},\"text\": \"${env.JOB_NAME} - ${env.BUILD_DISPLAY_NAME} Failure (<${env.BUILD_URL}|Open>)\"}"
      }
    }
    unstable {
      updateGitlabCommitStatus(state: 'failed')
      withCredentials([string(credentialsId: 'google-chat-jenkins-channel', variable: 'chat_url')]) {
        httpRequest httpMode:'POST', contentType: 'APPLICATION_JSON', url: "${chat_url}&thread_key=${env.JOB_NAME}", requestBody: "{\"sender\":{\"avatarUrl\":\"https://ci.resys.io/images/48x48/yellow.png\",\"displayName\":\"Jenkins job ${env.JOB_NAME}\"},\"text\": \"${env.JOB_NAME} - ${env.BUILD_DISPLAY_NAME} Unstable (<${env.BUILD_URL}|Open>)\"}"
      }
    }
    success {
      updateGitlabCommitStatus(state: 'success')
      withCredentials([string(credentialsId: 'google-chat-jenkins-channel', variable: 'chat_url')]) {
        httpRequest httpMode:'POST', contentType: 'APPLICATION_JSON', url: "${chat_url}&thread_key=${env.JOB_NAME}", requestBody: "{\"sender\":{\"avatarUrl\":\"https://ci.resys.io/images/48x48/blue.png\",\"displayName\":\"Jenkins job ${env.JOB_NAME}\"},\"text\": \"${env.JOB_NAME} - ${env.BUILD_DISPLAY_NAME} Success (<${env.BUILD_URL}|Open>)\"}"
      }
    }
  }
}