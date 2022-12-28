import fr.eagleeyestudio.ra.ResourcesAssembler;
import fr.eagleeyestudio.ra.ResourcesDisassembler;

import java.io.IOException;
import java.util.zip.DataFormatException;

public class Main {

    public static void main(String[] args) throws IOException, DataFormatException {

        ResourcesAssembler ra = new ResourcesAssembler(
                "D:/Java Projects/LoneFinalProject/assets",
                "assets\\",
                "C:/Users/kevin/Desktop/data.eedata");

        ra.assemble();


        ResourcesDisassembler rd = new ResourcesDisassembler(
                "C:/Users/kevin/Desktop/data.eedata",
                "C:/Users/kevin/Desktop/data_out/");


       rd.disassemble();
    }

}
