def username = 'ted'
def password = java.util.UUID.randomUUID().toString()
def j = jenkins.model.Jenkins.get()
def user = j.securityRealm.createAccount(username, password)
def token = new jenkins.security.ApiTokenProperty()
user.addProperty(token)
def result = token.generateNewToken('test1')

def generated = [
        users: [
                [username: username, password: password, apiToken: result.plainValue]
        ]
]
def destination = new File(j.rootDir, '.tc.json')
destination.createNewFile()
destination.withWriter {
    it.write(groovy.json.JsonOutput.toJson(generated))
}
user.save()
