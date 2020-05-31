package com.sbcodess.swinglayout;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
public class Main {



    public static void main(String args [])


    {

        ArgumentParser parser = ArgumentParsers.newFor("Checksum").build()
                .defaultHelp(true)
                .description("Display SwingLayout.");
        parser.addArgument("c", "--Column")
                .help("Specify Columns");
        parser.addArgument("r","--Row")
                .help("Specifies Rows");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(ns.getString("type"));
        } catch (NoSuchAlgorithmException e) {
            System.err.printf("Could not get instance of algorithm %s: %s",
                    ns.getString("type"), e.getMessage());
            System.exit(1);
        }
        for (String name : ns.<String> getList("file")) {
            Path path = Paths.get(name);
            try (ByteChannel channel = Files.newByteChannel(path,
                    StandardOpenOption.READ);) {
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while (channel.read(buffer) > 0) {
                    buffer.flip();
                    digest.update(buffer);
                    buffer.clear();
                }
            } catch (IOException e) {
                System.err
                        .printf("%s: failed to read data: %s", e.getMessage());
                continue;
            }
            byte md[] = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0, len = md.length; i < len; ++i) {
                String x = Integer.toHexString(0xff & md[i]);
                if (x.length() == 1) {
                    sb.append("0");
                }
                sb.append(x);
            }
            System.out.printf("%s  %s\n", sb.toString(), name);
        }
    }



    }




}
