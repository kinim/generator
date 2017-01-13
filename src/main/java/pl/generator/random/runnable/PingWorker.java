package pl.generator.random.runnable;

import pl.generator.random.Main;
import pl.generator.random.util.CommonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class PingWorker implements Runnable {

    private static final String PING_RESPONSE_MATCHER = "[\\W\\w]*time=[\\d]*[\\W\\w]*";
    private static final int MAX_SEED_SIZE = 100;

    private String hostname;

    public PingWorker(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public void run() {
        try {
            Process exec = Runtime.getRuntime().exec("ping " + hostname + " -t");

            InputStream inputStream = exec.getInputStream();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(inputStream));
            String outputLine;
            StringBuilder binaryNumber = new StringBuilder();
            while ((outputLine = stdInput.readLine()) != null && !Main.stop) {
                if (outputLine.matches(PING_RESPONSE_MATCHER)) {
                    appendBinaryDigit(outputLine, binaryNumber);
                    if (binaryNumber.length() == 4) {
                        CommonUtils.addHexDigitToSeed(binaryNumber);
                        binaryNumber = new StringBuilder();
                    }
                    if (Main.SEED.size() == MAX_SEED_SIZE) {
                        pauseWork();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendBinaryDigit(String outputLine, StringBuilder binaryNumber) {
        int rtt = getRTTLastDigit(outputLine);
        if (rtt < 5) {
            binaryNumber.append("0");
        } else {
            binaryNumber.append("1");
        }
    }

    private void pauseWork() {
        try {
            Thread.sleep(10_000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int getRTTLastDigit(String s) {
        int indexOfRTTParameter = s.indexOf("time=");
        int endOfRTT = s.indexOf(" ", indexOfRTTParameter);
        String rTTString = s.substring(indexOfRTTParameter, endOfRTT);
        String rTTDigits = rTTString.replaceAll("\\D+", "");
        return Integer.parseInt(rTTDigits.substring(rTTDigits.length() - 1));
    }
}

