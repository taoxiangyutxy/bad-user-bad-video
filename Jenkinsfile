node {
    def GIT_COMMIT
    def GIT_PREVIOUS_SUCCESSFUL_COMMIT
    def CURRENT_JOB
    stage('拉取代码') {
        dir("/var/lib/jenkins/workspace/tttOne") {
            def arr = "${JOB_NAME}".split('/')
            CURRENT_JOB = arr[arr.length - 1]
            sh "pwd"
            def res = checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '72182730-fa58-467a-889c-a224648310d7', url: 'https://gitee.com/yyyyxt/ttt-one.git']]])
            sh "echo ${res}"
            GIT_COMMIT = res.get("GIT_COMMIT")
            GIT_PREVIOUS_SUCCESSFUL_COMMIT = res.get("GIT_PREVIOUS_SUCCESSFUL_COMMIT")
        }
    }
    stage('停止当前模块，释放内存') {
        sh "supervisorctl stop ${CURRENT_JOB}"
    }
    stage('编译公共模块') {
        dir("/var/lib/jenkins/workspace/tttOne") {
            //如果当前下拉的和上次构建成功的commit id不同，install common
            sh "echo GIT_COMMIT:${GIT_COMMIT}"
            sh "echo GIT_PREVIOUS_SUCCESSFUL_COMMIT:${GIT_PREVIOUS_SUCCESSFUL_COMMIT}"
            sh "echo CURRENT_JOB:${CURRENT_JOB}"
            if ("${GIT_PREVIOUS_SUCCESSFUL_COMMIT}" != "${GIT_COMMIT}") {
                sh "mvn -f one-common clean install"
            }
        }
    }
    stage('构建当前模块') {
        dir("/var/lib/jenkins/workspace/tttOne") {
            sh "mvn -f ${CURRENT_JOB} clean compile install package  -Dmaven.test.skip=true " //-P dev  区分构建环境  -Dmaven.test.skip=true过滤测试用例
         //   sh "mvn  clean compile install package  "
        }
    }
    stage('移动至工作目录') {
        dir("/var/lib/jenkins/workspace/tttOne") {
            sh "cp -f ./${CURRENT_JOB}/target/${CURRENT_JOB}.jar /app/data/"
        }
    }
    stage('启动当前模块') { //怎么启动？
        sh "supervisorctl restart ${CURRENT_JOB}"
        sh "supervisorctl status ${CURRENT_JOB}"
    }
    stage('返回构建结果 ') {
        //updateGitlabCommitStatus name: 'build', state: 'success'
    }
}
