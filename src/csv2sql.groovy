/**
 * Created by VErmilov on 18.09.2017.
 *
 * Form sql from csv as it.
 * Table name = csv file name
 *
 */
@GrabResolver(name = 'central', root = 'https://mvnrepository.com/')
@Grab('org.yaml:snakeyaml:1.17')
import org.yaml.snakeyaml.Yaml
import groovy.io.FileType

Yaml yaml = new Yaml()
Map config = (Map) yaml.load(("config\\config.yaml" as File).text)


def dir = new File(config.fileCsvDirIn)
String fldList

List<String> out
dir.eachFileRecurse (FileType.FILES) { file ->
    if (file.name.split("\\.")[1] == "csv") {
        out = new ArrayList<>()
        int i = 0
        def indexId = -1
        int j = 0
        file.text.eachLine {
            i++;
            List<String> lineList = it.split("\\" + config.delimeter)
            // Get header
            if (i == 1) {
                if (!config.useId) {
                    indexId = lineList.findIndexOf { it == config.idFld }
                    if(indexId > - 1) lineList.remove(indexId)
                    if (config.genId) {
                        indexId = lineList.findIndexOf { it == config.idFld }
                        if (indexId == -1) {
                            lineList.add(0, config.idFld)
                        }
                    }
                }
                fldList = lineList.join(",")
            }
            else {
                if (!config.useId) {
                    if(indexId > - 1) lineList.remove(indexId)
                    if (config.genId && indexId == -1) {
                        j++
                        lineList.add(0, String.valueOf(j))
                    }
                }
                valList = []
                lineList.each {el -> el.isNumber() ? valList.add(el) : valList.add("'" + el + "'")}
                valList = valList.join(",")
                String sql = "INSERT INTO " + config.schema + "." + file.name.split("\\.")[0] + " (" + fldList + ") VALUES(" + valList + ")" + config.delimeterSqlEnd
                out.add(sql)
            }
        }
        if (config.isCommit) {
            out.add("COMMIT" + config.delimeterSqlEnd)
        }

        if (!(new File(config.fileSqlDirOut).isDirectory())) {
            new File(config.fileSqlDirOut).mkdir()
        }
        flOutName = config.fileSqlDirOut + "\\" + file.name.split("\\.")[0] + ".sql"
        new File(flOutName).delete()
        new File(flOutName) << out.join(System.getProperty("line.separator"))
    }
}