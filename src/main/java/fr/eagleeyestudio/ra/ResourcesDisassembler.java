package fr.eagleeyestudio.ra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public record ResourcesDisassembler(String resourcesFilePath, String outFolder) {


    public void disassemble() throws IOException, DataFormatException {
        long startTime = System.currentTimeMillis();
        System.out.println("Resources disassembling...");

        File resourcesFile = new File(resourcesFilePath);

        if (!resourcesFile.exists())
            throw new FileNotFoundException("Input resources folder not found !");

        byte[] bytes = Compressor.decompress(Files.readAllBytes(resourcesFile.toPath()), true);

        String lines = new String(bytes);
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

        System.out.println("Disassembling finish in: " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
