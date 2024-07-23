package components.task;

import components.Config;
import components.Main;
import components.Matrix.Id_Matrix;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class MatrixMultiplier extends RecursiveTask<Map<Id_Matrix, BigInteger[][]>> {


    private int endRowB;
    private int endColumnB;
    private int endRowA;
    private int endColumnA;
    private String file1;
    private String file2;

    private int startRow;
    private int startColumn;
    private int endRow;
    private int endColumn;

    private int start;

    private int end;

    private BigInteger[][] matrix1;
    private BigInteger[][] matrix2;
    private BigInteger[][] matrixRes;
    private int startRowA, startColumnA, startRowB, startColumnB, size, colsA, colsB;
    private BigInteger[][] A, B, result;
    private String newName;
    private static final int LIMIT = 1024;
    private MultiplyTask multiplyTask;

    public MatrixMultiplier(MultiplyTask multiplyTask, String file1, String file2, BigInteger[][] A, BigInteger[][] B, BigInteger[][] resultMat,
                            int start, int end, String newName) {
        this.multiplyTask = multiplyTask;
        this.file1 = file1;
        this.file2 = file2;
        this.A = A;
        this.B = B;
        this.result = resultMat;
        this.start = start;
        this.end = end;
        this.newName = newName;

    }

    public MatrixMultiplier(String file1, String file2, BigInteger[][] A, BigInteger[][] B, BigInteger[][] resultMat,
                            int start, int end, String newName) {
        this.file1 = file1;
        this.file2 = file2;
        this.A = A;
        this.B = B;
        this.result = resultMat;
        this.start = start;
        this.end = end;
        this.newName = newName;

    }

    @Override
    protected Map<Id_Matrix, BigInteger[][]> compute() {


        // System.out.println(end-start);
        if ((end - start < Config.getInstance().getMaximum_rows_size())) {
            BigInteger[][] finalResult = multiplyMatrices(A, B, result, start, end);
        } else {
            //    System.out.println("Splitting the task into subtasks ...");
            int mid = (end - start) / 2 + start;

            MatrixMultiplier left = new MatrixMultiplier(file1, file2, A, B, result, start, mid, newName);
            MatrixMultiplier right = new MatrixMultiplier(file1, file2, A, B, result, mid, end, newName);

            left.fork();
            Object rightResult = right.compute();
            Object leftResult = left.join();
        }

        Id_Matrix idMatrix = new Id_Matrix(newName, A.length, B[0].length);
        Map<Id_Matrix, BigInteger[][]> haha = new ConcurrentHashMap<>();
        haha.put(idMatrix, result);

        return haha;
    }


    private BigInteger[][] multiplyMatrices(BigInteger[][] a, BigInteger[][] b, BigInteger[][] result,
                                            int start, int end) {

        int colA = a[0].length;
        int colB = b[0].length;

        for (int i = start; i < end; i++) {
            for (int j = 0; j < colB; j++) {
                BigInteger sum = BigInteger.ZERO;
                for (int k = 0; k < colA; k++) {
                    sum = sum.add(a[i][k].multiply(b[k][j]));
                }
                result[i][j] = sum;
            }
        }

        Main.matrixResult = result;

        return result;
    }


}
