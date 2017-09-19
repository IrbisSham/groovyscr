@GrabResolver(name = 'central', root = 'https://mvnrepository.com/')
@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml

DELIMETER_WORD = ";"
FLD_login = "loginAd"
FLD_fioComp = "fioComp"
FLD_emailComp = "emailComp"

Yaml yaml = new Yaml()
Map config = (Map) yaml.load(("config\\config.yaml" as File).text)

List<String> out = new ArrayList<>()


// Create group script part
out.add(config.template.addGroup)

int i = 0
def LoginAD_index
def Name_index
def Email_index

// Create users script part
new File(config.filein).eachLine {
    def lineList = it.split(DELIMETER_WORD)
    i++
    // Get header
    if (i == 1) {
        LoginAD_index = lineList.findIndexOf { it == FLD_login }
        Name_index = lineList.findIndexOf { it == FLD_fioComp }
        Email_index = lineList.findIndexOf { it == FLD_emailComp }
    }
    else {
        out.add(config.template
                .addUser
                .replaceAll(getExpr(FLD_login), roundByQuotes(lineList[LoginAD_index]))
                .replaceAll(getExpr(FLD_fioComp), roundByQuotes(lineList[Name_index]))
                .replaceAll(getExpr(FLD_emailComp), roundByQuotes(lineList[Email_index]))
        )
    }
}
new File(config.fileout).delete()
new File(config.fileout) << out.join(System.lineSeparator())
                                .replaceAll("@group@", roundByQuotes(config.group))
                                .replaceAll("@login@", config.server.login)
                                .replaceAll("@password@", config.server.password)
                                .replaceAll("@host@", config.server.host)

def getExpr(val) {
    "<<" + val + ">>"
}


def roundByQuotes(val) {
    "\"" + val + "\""
}



