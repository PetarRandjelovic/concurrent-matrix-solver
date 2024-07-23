package components.Matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.concurrent.Callable;

public class SaveMatriceThread implements Callable {

    private String name;
    private String file;
    private BigInteger[][] matrix;
    private Id_Matrix idMatrix;

    public SaveMatriceThread(String name, String file, BigInteger[][] matrix, Id_Matrix idMatrix) {
        this.name = name;
        this.file = file;
        this.matrix = matrix;
        this.idMatrix = idMatrix;
    }


    @Override
    public Object call() throws Exception {

        File file1 = new File(file);

        //  System.out.println(file1.getAbsolutePath());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file1));

        writer.write("matrix_name= " + idMatrix.name.replace(".rix", "") + ", rows=" + idMatrix.row + ", cols=" + idMatrix.column);
        writer.write("\n");
        // Write the content of the matrix to the file
        for (int j = 0; j < matrix[0].length; j++) {
            for (int i = 0; i < matrix.length; i++) {

                writer.write(i + "," + j + " = " + matrix[i][j].toString() + " "); // Write BigInteger value to file
                writer.write("\n"); // Separate values by space
            }
      //      writer.newLine(); // Move to the next row in the file
        }
        //save -name a1c1_result.rix -file zdravo.rix


        writer.close();

        System.out.println("Matrix saved successfully!");

        return null;
    }
}

