/*prop.key_path
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

/**
 *
 * @author miguel
 */
public class CryptoSchemeFactory {

    public static CryptoScheme scheme = null;
    public CryptoScheme scheme2 = null;

    public CryptoScheme getNewCryptoScheme(String mode) {
        switch (mode) {
            case "one":
                scheme2 = new CryptoSchemeOne();
                return scheme2;
            case "two":
                scheme2 = new CryptoSchemeTwo();
                return scheme2;
            case "three":
                scheme2 = new CryptoSchemeThree();
                return scheme2;
            case "four":
                scheme2 = new CryptoSchemeFour();
                return scheme2;
            case "five":
                scheme2 = new CryptoSchemeFive();
                return scheme2;
            default:
                scheme2 = new CrytpoSchemeEmpty();
                return scheme2;
        }

    }

    public static CryptoScheme getCryptoScheme(String mode) {
        if (scheme != null) {
            return scheme;
        } else {
            switch (mode) {
                case "one":
                    scheme = new CryptoSchemeOne();
                    return scheme;
                case "two":
                    scheme = new CryptoSchemeTwo();
                    return scheme;
                case "three":
                    scheme = new CryptoSchemeThree();
                    return scheme;
                case "four":
                    scheme = new CryptoSchemeFour();
                    return scheme;
                case "five":
                    scheme = new CryptoSchemeFive();
                    return scheme;
                
                default:
                    scheme = new CrytpoSchemeEmpty();
                    return scheme;
            }
        }
    }
}
