mavenJob('AWS-DynamoDB-DSL-Job') {
    description 'DSL Job for snap shot build'


    logRotator {
        numToKeep 5
    }

    parameters {
        gitParam('Branch') {
            description 'The Git branch to checkout'
            type 'BRANCH'
            defaultValue 'origin/master'
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