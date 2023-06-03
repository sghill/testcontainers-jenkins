import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def j = jenkins.model.Jenkins.get()

j.securityRealm = new hudson.security.HudsonPrivateSecurityRealm(false, false, null)
j.save()

def inputFile = new File(j.rootDir, '.tc.in.json')
def jsonSlurper = new JsonSlurper()
def input = jsonSlurper.parse(inputFile)

def createdUsers = []

for (def userReq in input['users']) {
    def username = userReq['name']
    def password = UUID.randomUUID().toString()
    def user = j.securityRealm.createAccount(username, password)
    def token = new jenkins.security.ApiTokenProperty()
    user.addProperty(token)
    def createdTokens = []
    for (def tokenReq in userReq['apiTokens']) {
        def tokenName = tokenReq['name']
        def result = token.generateNewToken(tokenName)
        createdTokens.add([name: tokenName, token: result.plainValue])
    }
    createdUsers.add([name: username, password: password, apiTokens: createdTokens])
    user.save()
}

def createdAgents = []

for (def agentReq in input['agents']) {
    def agentName = agentReq['name']
    def workspaceDir = agentReq['workspaceDir']
    def launcher = new hudson.slaves.JNLPLauncher(true)
    def agent = new hudson.slaves.DumbSlave(agentName, workspaceDir, launcher)
    j.addNode(agent)
    def agentSecret = j.nodesObject.getNode(agentName)?.computer?.jnlpMac
    createdAgents.add([name: agentName, secret: agentSecret])
}

def generated = [
        agents: createdAgents,
        users : createdUsers,
]
def destination = new File(j.rootDir, '.tc.json')
destination.createNewFile()
destination.withWriter {
    it.write(JsonOutput.toJson(generated))
}
