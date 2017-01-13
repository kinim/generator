package pl.generator.random.runnable;

import com.fazecast.jSerialComm.SerialPort;
import pl.generator.random.Main;
import pl.generator.random.util.CommonUtils;


public class SonarWorker implements Runnable {

    @Override
    public void run() {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while (!Main.stop) {
                while (comPort.bytesAvailable() == 0) {
                    Thread.sleep(300);
                }
                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                comPort.readBytes(readBuffer, readBuffer.length);
                String s = getSonarString(readBuffer);
                if (!s.isEmpty()) {
                    appendBinaryDigit(s, stringBuilder);
                    if (stringBuilder.length() == 4) {
                        CommonUtils.addHexDigitToSeed(stringBuilder);
                        stringBuilder = new StringBuilder();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        comPort.closePort();
    }

    private String getSonarString(byte[] readBuffer) {
        StringBuilder sb = new StringBuilder();
        for (byte b : readBuffer) {
            sb.append((char) b);
        }
        return sb.toString().replaceAll("0\r\n", "").replace("\r\n", "");
    }

    private void appendBinaryDigit(String s, final StringBuilder stringBuilder) {
        try {
            int lastDigit = Integer.parseInt(s.substring(s.length() - 1));
            if (lastDigit < 5) {
                stringBuilder.append("0");
            } else {
                stringBuilder.append("1");
            }
        } catch (NumberFormatException e) {
            return;
        }
    }
}
