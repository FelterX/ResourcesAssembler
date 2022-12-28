package fr.eagleeyestudio.ra;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.DataFormatException;

public record ResourcesDisassembler(String resourcesFilePath, String outFolder) {


    public void disassemble() throws IOException, DataFormatException {
        long startTime = System.currentTimeMillis();
        System.out.println("Resources disassembling...");

        File resourcesFile = new File(resourcesFilePath);

        if (!resourcesFile.exists())
            throw new FileNotFoundException("Input resources folder not found !");


        try (FileInputStream fis = new FileInputStream(resourcesFile)) {
            byte[] bytes = fis.readAllBytes();
            byte[] decompressedBytes = Compressor.decompress(bytes, true);

            String lines = new String(decompressedBytes);
            String[] linesSplit = lines.split("#start");

            System.out.println(linesSplit.length - 1 + " files found.");

            for (int i = 1; i < linesSplit.length; i++) {
                long startP = System.currentTimeMillis();

                String filePath = linesSplit[i].substring(0, linesSplit[i].indexOf("#data"));
                File outFile = new File(outFolder, filePath);
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();

                System.out.println("Data processing: " + outFile.getAbsolutePath() + " (" + i + "/" + (linesSplit.length - 1) + ")");

                byte[] rBytes = linesSplit[i].substring(linesSplit[i].indexOf("#data") + 5).getBytes();

                System.out.println(Arrays.toString(rBytes));

                Files.write(outFile.toPath(), rBytes);

                System.out.println("Finished in: " + (System.currentTimeMillis() - startP) + "ms (" + rBytes.length + "b).");
            }
        }





        /*
        String[] linesSplit = lines.split("\n");

        System.out.println(linesSplit.length + " files found.");

        List<byte[]> datas = new ArrayList<>();

        for (int i = 0; i < linesSplit.length; i++)
            datas.add(linesSplit[i].substring(7, linesSplit[i].length() - 5).getBytes());


        int in = 0;
        for (byte[] data : datas) {
            in++;
            String str = new String(data);
            String[] split = str.split("#data");

            if (split != null && split.length > 1) {
                long startP = System.currentTimeMillis();
                String path = split[0];
                String[] rData = split[1].split(",");

                File outFile = new File(outFolder, path);
                outFile.getParentFile().mkdirs();
                outFile.createNewFile();

                System.out.println("Data processing: " + outFile.getAbsolutePath() + " (" + in + "/" + linesSplit.length + ")");

                byte[] rBytes = new byte[rData.length];

                for (int i = 0; i < rBytes.length; i++)
                    rBytes[i] = Byte.parseByte(rData[i]);

                Files.write(outFile.toPath(), rBytes);
                System.out.println("Finished in: " + (System.currentTimeMillis() - startP) + "ms.");
            }
        }
         */

        System.out.println("Disassembling finish in: " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
