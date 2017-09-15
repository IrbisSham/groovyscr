@GrabResolver(name='central', root='https://mvnrepository.com/')
@Grab('org.yaml:snakeyaml:1.17')

import org.yaml.snakeyaml.Yaml

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

DELIMETER_WORD = ";"
FLD_LoginAD = "LoginAD"
FLD_Name = "Name"

EMAIL_DOMAIN = "@test.qu"

Yaml yaml = new Yaml()
Map config = (Map) yaml.load(("config\\config.yaml" as File).text)

List<String> out = new ArrayList<>()


// Create group script part
out.add(config.template.addGroup)

int i = 0
def LoginAD_index
def Name_index

Key key = CryptoUtil.generateKey();
new File(config.filekeyout) << key

// Create users script part
new File(config.filein).eachLine {
    def lineList = it.split(DELIMETER_WORD)
    i++
    // Get header
    if (i == 1) {
        LoginAD_index = lineList.findIndexOf { it == FLD_LoginAD }
        Name_index = lineList.findIndexOf { it == FLD_Name }
    }
    else {
        out.add(config.template
                .addUser
                .replaceAll(getExpr(FLD_LoginAD), roundByQuotes(lineList[LoginAD_index]))
                .replaceAll(getExpr(FLD_Name), roundByQuotes(encode(lineList[Name_index], key)))
                .replaceAll("@userEmail@", roundByQuotes(getMail(lineList[LoginAD_index])))
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

def getMail(val) {
    val + EMAIL_DOMAIN
}

def roundByQuotes(val) {
    "\"" + val + "\""
}

def encode(data, key) {
    new String(CryptoUtil.encode(data, key), "UTF-8")
}

def decode(data, key) {
//    def key = new File(config.fileout).getText('UTF-8')
    new String(CryptoUtil.decode(data, key), "UTF-8")
}


class CryptoUtil {

    /**
     * Алгоритм шифрования. Возможны варианты:
     * DES: Digital Encryption Standard
     * DESede: Triple DES Encryption
     * Blowfish: The block cipher designed by Bruce Schneier
     * и другие
     */
    private static final String ALGORITHM_KEY = "Blowfish";

    /**
     * Режим работы алгоритма. Режимов много, например:
     * DES/CBC/PKCS5Padding
     * DES/PCBC/PKCS5Padding
     * DESede/ECB/PKCS5Padding
     * DESede/ECB/PKCS5Padding
     * Blowfish/CBC/PKCS5Padding
     * и много других
     */
    private static final String ALGORITHM = "Blowfish/PCBC/PKCS5Padding";

    /**
     * Параметр инициализации алгоритма шифрования. Значения байтов могут
     * быть разными, но одинаковыми для шифрации и дешифрации одних и тех
     * же данных
     */
    private static final byte[] SALT = [(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
                                        (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99]

    /**
     * Кодирование заданного текста алгоритмом ALGORITHM с использованием
     * сгенерированного ключа
     *
     * @param cleartext текст для шифрования
     * @param key       ключ, используемый при шифровании
     * @return байтовый массив
     * @see CryptoUtil#generateKey
     */
    public static byte[] encode(String cleartext, Key key) {
        try {
            return crypt(cleartext.getBytes("UTF-8"), key, Cipher.ENCRYPT_MODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод для дешифрования текста.
     *
     * @param ciphertext зашифрованный текст в виде байтового массива
     * @param key        ключ, использованный при шифровании текста
     * @return дешифрованный текст
     * @see CryptoUtil#generateKey
     */
    public static String decode(byte[] ciphertext, Key key) {
        try {
            return new String(crypt(ciphertext, key, Cipher.DECRYPT_MODE), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод генерации ключа, используемого в алгоритме шифрования
     *
     * @return ключ для шифрования
     */
    static SecretKey generateKey() {
        try {
            return KeyGenerator.getInstance(ALGORITHM_KEY).generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод шифрования/дешифрования текста в зависимости от параметра mode
     *
     * @param input входной байтовый массив для шифрования
     * @param key   секретный ключ, используемый при шифровании
     * @param mode  режим работы алгоритма (шифрование/дешифрование)
     * @return зашифрованный или дешифрованный текст в виде байтового массива
     */
    private static byte[] crypt(byte[] input, Key key, int mode) {
        IvParameterSpec iv = new IvParameterSpec(SALT);
        Cipher c;
        try {
            c = Cipher.getInstance(ALGORITHM);
            c.init(mode, key, iv);
            return c.doFinal(input);
        } catch (Exception e) {

            // вообще-то может возникать несколько типов исключений и их нужно
            // обрабатывать соотв. образом. Но в примере это не важно
            e.printStackTrace();
        }
        return null;
    }

}


