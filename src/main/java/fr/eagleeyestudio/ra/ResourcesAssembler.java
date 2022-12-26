package fr.eagleeyestudio.ra;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;
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

            System.out.println(result.size() +  " files found.");

            File outFile = new File(outFilePath);
            if (!outFile.exists()) outFile.createNewFile();


            List<String> outLines = new ArrayList<>();

            int i = 0;
            for (String resource : result) {
                i++;

                long startProcessing = System.currentTimeMillis();
                System.out.println("File processing: " + resource + " (" + i + "/" + result.size() + ")...");

                File rscFile = new File(resource);
                byte[] bytes = Files.readAllBytes(new File(resource).toPath());

                String startingPath = rscFile.getAbsolutePath().substring(rscFile.getAbsolutePath().indexOf(startPathIndex));

                String str = Arrays.toString(bytes).replace("[", "").replace("]", "").replace(" ", "");

                String currLine = "#start:" + startingPath + "#data" + str + "#end";
                outLines.add(currLine);

                System.out.println("finished in: " + (System.currentTimeMillis() - startProcessing) + "ms") ;
            }
            Files.write(outFile.toPath(), outLines, StandardCharsets.UTF_8);

            System.out.println("Compressing file...");
            byte[] bytes = Files.readAllBytes(outFile.toPath());
            System.out.println("Current file byte: " + bytes.length + "b.");
            byte[] compressed = Compressor.compress(bytes, Deflater.BEST_COMPRESSION, true);
            Files.write(outFile.toPath(), compressed);
            System.out.println("File compressed, new size: " + compressed.length + "b.");

        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Assembling finish in: " + (System.currentTimeMillis() - start) + "ms.");
    }
}
