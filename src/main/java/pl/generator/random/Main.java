package pl.generator.random;

import org.apache.commons.lang3.StringUtils;
import pl.generator.random.runnable.PingWorker;
import pl.generator.random.runnable.SonarWorker;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static final Queue<Character> SEED = new ConcurrentLinkedQueue<>();
    public static volatile boolean stop;
    private static int outputLength;

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        int nThreads = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

        Runnable runnableSonar = new SonarWorker();
        executorService.execute(runnableSonar);
        for (Host host : Host.values()) {
            Runnable runnablePing = new PingWorker(host.getName());
            executorService.execute(runnablePing);
        }
        executorService.shutdown();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (!stop) {
            requestInput(br);
        }
    }

    private static void requestInput(BufferedReader br) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        if (outputLength == 0) {
            initOutputLength(br);
        }
        System.out.print("Numbers to generate:");
        String sCount = br.readLine();
        if (StringUtils.isNumeric(sCount)) {
            handleInput(sCount);
        } else {
            if ("exit".equals(sCount)) {
                stop = true;
                return;
            } else if ("seed".equals(sCount)) {
                SEED.forEach(System.out::print);
                System.out.println();
            }
            requestInput(br);
        }
    }

    private static void initOutputLength(BufferedReader br) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        System.out.print("Length of generated numbers (in bytes):");
        String sLength = br.readLine();
        if (StringUtils.isNumeric(sLength)) {
            outputLength = Integer.parseInt(sLength);
        } else {
            requestInput(br);
        }
    }

    private static void handleInput(String s) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        int nToGenerate = Integer.parseInt(s);
        Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        byte[] newSeed = reseed();
        c.init(Cipher.ENCRYPT_MODE, kg.generateKey(), new SecureRandom(newSeed));
        generateNumbers(nToGenerate, c);
    }

    private static byte[] reseed() throws InterruptedException {
        int seedLength = 16;
        byte[] seed = new byte[seedLength];
        while (SEED.size() < seedLength * 2) {
            //
        }
        for (int i = 0; i < seedLength; i++) {
            int c1 = Integer.parseInt(String.valueOf(new char[]{SEED.poll(), SEED.poll()}), 16);
            seed[i] = (byte) c1;
        }
        return seed;
    }

    private static void generateNumbers(int nToGenerate, Cipher c) {
        for (int i = 0; i < nToGenerate; i++) {
            byte[] update = c.update(new byte[outputLength]);
            long value = 0;
            for (byte anUpdate : update) {
                value = (value << 8) + (anUpdate & 0xff);
            }
            System.out.println(value);
        }
    }


}