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

            // Add extensions 'SparseCheckoutPaths' and 'PathRestriction'
            def nodeBuilder = NodeBuilder.newInstance()
            def sparseCheckout = nodeBuilder.createNode(
                    'hudson.plugins.git.extensions.impl.SparseCheckoutPaths')
            sparseCheckout
                    .appendNode('sparseCheckoutPaths')
                    .appendNode('hudson.plugins.git.extensions.impl.SparseCheckoutPath')
                    .appendNode('path', '/')
            def pathRestrictions = nodeBuilder.createNode(
                    'hudson.plugins.git.extensions.impl.PathRestriction')
            pathRestrictions.appendNode('includedRegions', '/.*')
            extensions {
                extensions << sparseCheckout
                extensions << pathRestrictions
            }
        }
    }

    configure {
        it / 'extensions' / 'hudson.plugins.git.extensions.impl.SparseCheckoutPaths' / 'sparseCheckoutPaths' {
            'hudson.plugins.git.extensions.impl.SparseCheckoutPath' {
                path '/'
            }
        }
        it / 'extensions' / 'hudson.plugins.git.extensions.impl.PathRestriction' {
            includedRegions '/.*'
        }
    }

    triggers {
        scm 'H/15 * * * *'
    }

    rootPOM 'pom.xml'
    goals 'clean install'

}