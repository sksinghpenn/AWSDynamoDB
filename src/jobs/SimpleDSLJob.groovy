mavenJob('AWS DynamoDB- Simple DSL Job') {
    description 'A very simple demo for the Jenkins Job DSL'


    logRotator {
        numToKeep 5
    }

    parameters {
        gitParam('Branch') {
            description 'The Git branch to checkout'
            type 'BRANCH'
            defaultValue 'origin/jenkins-job-dsl'
        }
    }

    scm {
        git {
            remote {
                url 'git@github.com:sksinghpenn/AWSDynamoDB.git'
            }

            branch '$Branch'


        }
    }



    triggers {
        scm 'H/15 * * * *'
    }

    rootPOM './pom.xml'
    goals 'clean package'

}