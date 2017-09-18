/**
 * Created by VErmilov on 18.09.2017.
 */
@GrabResolver(name = 'central', root = 'https://mvnrepository.com/')
@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml
import groovy.io.FileType

Yaml yaml = new Yaml()
Map config = (Map) yaml.load(("config\\config.yaml" as File).text)


def dir = new File(config.fileCsvDirIn)
String fldList

dir.eachFileRecurse (FileType.FILES) { file ->
    if (file.name.split("\\.")[1] == "csv") {
        List<String> out = new ArrayList<>()
        int i = 0
        file.text.eachLine {
            i++;
            println it
            List<String> lineList = it.split("\\" + config.delimeter)
            // Get header
            if (i == 1) {
                fldList = lineList.join(",")
            }
            else {
                valList = []
                lineList.forEach {el -> el.isNumber() ? valList.add(el) : valList.add("'" + el + "'")}
                valList = valList.join(",")
                String sql = "INSERT INTO " + config.schema + "." + file.name.split("\\.")[0] + " (" + fldList + ") VALUES(" + valList + ")"
                out.add(sql)
            }
        }
        flOutName = config.fileSqlDirOut + "\\" + file.name.split("\\.")[0] + ".sql"
        new File(flOutName).delete()
        new File(flOutName) << out.join(System.lineSeparator())
    }
}