pipeline {
    agent none               //全局必须带有agent表明此pipeline执行节点
    options {
        timeout(time: 1, unit: 'HOURS')
    }
    parameters {            //声明构建所需变量
        choice(name: 'env', choices: ['uat', 'prod'], description: '请选择需要构建环境')
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'develop', name: 'BRANCH', type: 'PT_BRANCH'
    }
    stages{
        stage("选择构建节点和构建"){
            agent { label 'master'}
            steps{
                echo "这是第一个步骤"
                git branch: "${params.BRANCH}", credentialsId: '85205ecb-ef06-4dba-ad74-23983ea3de19', url: 'https://github.com/yeji-python/pipeline_test.git'
                
            }
        }
        stage("maven打包和发布"){
            agent { label 'master'}
            steps{
               sh "mvn clean package toolkit:deploy -DskipTests=true -Dedas_config=${env}-edas-config.yaml"  
            }
        }
    }
}
