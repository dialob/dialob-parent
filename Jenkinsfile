#!groovy
pipeline {
  agent any
  tools {
    nodejs 'nodejs-12'
  }
  options {
    gitLabConnection('git.resys.io')
    gitlabCommitStatus(name: 'jenkins')
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '5'))
    disableConcurrentBuilds()
    skipStagesAfterUnstable()
  }
  triggers {
    gitlab(triggerOnPush: true, triggerOnMergeRequest: false, branchFilterType: 'All')
  }
  environment {
    DOCKER_TAG = "$BRANCH_NAME"
    PROJECT_NAME = "dialob-composer-generic-app"
  }

  stages {
    stage('Test') {
      steps {
      //  sh 'yarn install'
        sh 'npm install' // Yarn 1.6 doesn't support private repos
      //  sh 'yarn test'
      }
    }

    stage('Build') {
      steps {
        sh 'npm run build'
      }
    }

    stage('Publish to cdn') {
      steps {
        withCredentials([[
          $class: 'AmazonWebServicesCredentialsBinding',
          credentialsId: 'aws-cdn-update',
          accessKeyVariable: 'AWS_ACCESS_KEY_ID',
          secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
        ]]) {
          withAWS(region:'eu-central-1') {
            s3Upload(bucket:'cdn.resys.io', path:"$PROJECT_NAME/$BRANCH_NAME/", workingDir:'dist', includePathPattern:'**/*.js', contentType: 'application/javascript', cacheControl:'public,max-age=3600',acl:'PublicRead')
            s3Upload(bucket:'cdn.resys.io', path:"$PROJECT_NAME/$BRANCH_NAME/", workingDir:'dist', includePathPattern:'**/*.css', contentType: 'text/css', cacheControl:'public,max-age=3600',acl:'PublicRead')
            s3Upload(bucket:'cdn.resys.io', path:"$PROJECT_NAME/$BRANCH_NAME/", workingDir:'dist', includePathPattern:'**/*.html', contentType: 'text/html', cacheControl:'public,max-age=3600',acl:'PublicRead')
          }
        }
      }
    }
  }

}