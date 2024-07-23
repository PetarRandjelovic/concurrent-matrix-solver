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


        BufferedWriter writer = new BufferedWriter(new FileWriter(file1));

        writer.write("matrix_name= " + idMatrix.name.replace(".rix", "") + ", rows=" + idMatrix.row + ", cols=" + idMatrix.column);
        writer.write("\n");
        // Write the content of the matrix to the file
        for (int j = 0; j < matrix[0].length; j++) {
            for (int i = 0; i < matrix.length; i++) {

                writer.write(i + "," + j + " = " + matrix[i][j].toString() + " ");
                writer.write("\n");
            }

        }


        writer.close();

        System.out.println("Matrix saved successfully!");

        return null;
    }
}

