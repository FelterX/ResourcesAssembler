package fr.eagleeyestudio.ra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;

public record ResourcesAssembler(String resourcesPath, String startPathIndex, String outFilePath) {

    public void assemble() throws FileNotFoundException {
        long start = System.currentTimeMillis();
        System.out.println("Resources assembling...");

        File resourcesFile = new File(resourcesPath);

        if (!resourcesFile.exists()) throw new FileNotFoundException("Input resources folder not found !");

        try (Stream<Path> walk = Files.walk(resourcesFile.toPath())) {

            List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

            System.out.println(result.size() + " files found.");

            File outFile = new File(outFilePath);
            if (!outFile.exists()) outFile.createNewFile();


            List<Byte> outByte = new ArrayList<>();

            int i = 0;
            for (String resource : result) {
                i++;

                long startProcessing = System.currentTimeMillis();
                System.out.println("File processing: " + resource + " (" + i + "/" + result.size() + ")...");

                File rscFile = new File(resource);
                byte[] bytes = Files.readAllBytes(new File(resource).toPath());

                System.out.println(Arrays.toString(bytes));

                String startingPath = rscFile.getAbsolutePath().substring(rscFile.getAbsolutePath().indexOf(startPathIndex));

                String startLine = "#start" + startingPath + "#data";

                for (int b = 0; b < startLine.getBytes().length; b++)
                    outByte.add(startLine.getBytes()[b]);

                for (int b = 0; b < bytes.length; b++)
                    outByte.add(bytes[b]);

                System.out.println("finished in: " + (System.currentTimeMillis() - startProcessing) +
                        "ms (" + (startLine.getBytes().length + bytes.length) + "b).");
            }


            FileOutputStream fos = new FileOutputStream(outFile);

            byte[] outArray = new byte[outByte.size()];
            for (int b = 0; b < outByte.size(); b++)
                outArray[b] = outByte.get(b);

            System.out.println("Compressing file...");
            byte[] compressed = Compressor.compress(outArray, Deflater.BEST_COMPRESSION, true);
            System.out.println("File compressed, size: " + outArray.length + "b -> " + compressed.length + "b.");

            fos.write(compressed);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Assembling finish in: " + (System.currentTimeMillis() - start) + "ms.");
    }
}
