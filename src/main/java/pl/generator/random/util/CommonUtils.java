package pl.generator.random.util;

import static pl.generator.random.Main.SEED;

public class CommonUtils {

    public static void addHexDigitToSeed(StringBuilder binaryNumber) {
        int decimal = Integer.parseInt(binaryNumber.toString(), 2);
        String hexStr = Integer.toString(decimal, 16);
        SEED.add(hexStr.toCharArray()[0]);
    }

}
