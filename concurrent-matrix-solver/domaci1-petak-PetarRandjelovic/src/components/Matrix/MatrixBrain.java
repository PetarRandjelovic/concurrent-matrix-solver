package components.Matrix;

import components.Main;
import components.task.MultiplyTask;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MatrixBrain {

    public ExecutorService service = Executors.newCachedThreadPool();

    private Map<Id_Matrix, BigInteger[][]> matrixMap = new ConcurrentHashMap<>();


    private static Map<Id_Matrix, BigInteger[][]> multipliedMatrixMap = new ConcurrentHashMap<>();

    public static BigInteger[][] getMultipliedMatrice(String name) {
        Map<Id_Matrix, BigInteger[][]> result = getMultipliedMatrixMap();

        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : result.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            if (idMatrix.compareNames(name)) {
                BigInteger[][] matrix = entry.getValue();


                return matrix;
            }
        }

        return null;
    }

    public void addExtractedMatrix(Map<Id_Matrix, BigInteger[][]> result) {
        boolean found = true;
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : result.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            for (Map.Entry<Id_Matrix, BigInteger[][]> postojecMatrica : matrixMap.entrySet()) {
                Id_Matrix idMatrixPostojeca = postojecMatrica.getKey();
                if (idMatrix.name.equals(idMatrixPostojeca.name)) {
                    found = false;
                    matrixMap.putAll(result);
                    matrixMap.remove(idMatrixPostojeca);
                    System.out.println("Matrix " + idMatrix.name + " updated!");
                    break;
                }
            }

        }

        if (found) {
            System.out.println("Created new matrix");
            matrixMap.putAll(result);
        }


    }

    public void addMultipliedMatrixAsync(String name, MultiplyTask multiplyTask) {

        boolean found = false;

        if (multipliedMatrixMap.isEmpty()) {
            Main.tasks.add(multiplyTask);
            System.out.println("Matrix " + name + "started multiplying! empty");
            return;
        }

        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : multipliedMatrixMap.entrySet()) {

            Id_Matrix idMatrix = entry.getKey();
            if (idMatrix.compareNames(name)) {
                found = true;
                System.out.println("Matrix " + name + " already finished!");
                break;
            }

        }
        if (!found) {
            Main.tasks.add(multiplyTask);
            System.out.println("Matrix " + name + "started multiplying!");
        }

    }

    public void addMultipliedMatrix(Map<Id_Matrix, BigInteger[][]> result) {

        multipliedMatrixMap.putAll(result);

    }

    public Map<Id_Matrix, BigInteger[][]> getMultipliedMatrix(Map<Id_Matrix, BigInteger[][]> result) {


        return result;
    }

    public Id_Matrix getInfoOnMatrix(String name) {

        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            if (idMatrix.compareNames(name)) {

                BigInteger[][] matrix = entry.getValue();
                for (int i = 0; i < matrix.length; i++) {
                    for (int j = 0; j < matrix[i].length; j++) {
                        System.out.print(matrix[i][j]);
                        System.out.println();
                    }
                }
                return idMatrix;
            }
        }
        return null;
    }

    public List<Id_Matrix> getAllInfoOnMatrix() {
        CopyOnWriteArrayList<Id_Matrix> s = new CopyOnWriteArrayList<>();
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            s.add(idMatrix);

        }
        return s;
    }

    public List<Id_Matrix> getAllInfoOnMatrixAsc() {
        CopyOnWriteArrayList<Id_Matrix> s = new CopyOnWriteArrayList<>();
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            String name = idMatrix.name;
            s.add(idMatrix);
        }

        Collections.sort(s, new Comparator<Id_Matrix>() {
            @Override
            public int compare(Id_Matrix matrix1, Id_Matrix matrix2) {
                int rowComparison = Integer.compare(matrix1.row, matrix2.row);
                if (rowComparison != 0) {
                    // If rows are different, return the comparison result
                    return rowComparison;
                } else {
                    // If rows are equal, compare columns
                    return Integer.compare(matrix1.column, matrix2.column);
                }
            }
        });

        return s;
    }


    public List<Id_Matrix> getAllInfoOnMatrixDesc() {
        CopyOnWriteArrayList<Id_Matrix> s = new CopyOnWriteArrayList<>();
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            String name = idMatrix.name;
            s.add(idMatrix);
        }

        Collections.sort(s, new Comparator<Id_Matrix>() {
            @Override
            public int compare(Id_Matrix matrix1, Id_Matrix matrix2) {
                int rowComparison = Integer.compare(matrix2.row, matrix1.row); // Compare rows in descending order
                if (rowComparison != 0) {
                    // If rows are different, return the comparison result
                    return rowComparison;
                } else {
                    // If rows are equal, compare columns
                    return Integer.compare(matrix2.column, matrix1.column); // Compare columns in descending order
                }
            }
        });

        return s;
    }


    public List<Id_Matrix> getFirstNMatrixes(int n) {
        CopyOnWriteArrayList<Id_Matrix> s = new CopyOnWriteArrayList<>();
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            s.add(idMatrix);
        }
        if (n >= s.size()) {
            return s;
        } else {
            return s.subList(0, n);
        }
    }

    public List<Id_Matrix> getLastNMatrixes(int n) {
        CopyOnWriteArrayList<Id_Matrix> s = new CopyOnWriteArrayList<>();
        // Iterating through matrixMap and adding Id_Matrix objects to the list
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            s.add(idMatrix);
        }
        // Checking if the requested number of matrices (n) is greater than or equal to the size of the list
        if (n >= s.size()) {
            // If n is greater than or equal to the size of the list, return the entire list
            return s;
        } else {
            // If n is smaller than the size of the list, return the sublist containing the last n elements
            return s.subList(s.size() - n, s.size());
        }
    }

    public static Map<Id_Matrix, BigInteger[][]> getMultipliedMatrixMap() {
        return multipliedMatrixMap;
    }

    public void clear(String input, boolean isfile) {
        if (isfile) {
            //    String inputs=input.replace(".rix", "").toLowerCase();
            String inputs = input;
            for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
                Id_Matrix idMatrix = entry.getKey();
                if (idMatrix.file == null) {
                    continue;
                }
                //       System.out.println(idMatrix.file.getName()+" "+inputs);
                if (idMatrix.file.getName().equalsIgnoreCase(inputs)) {
                    matrixMap.remove(idMatrix);
                    Main.systemExplorer.getLastModifiedMap().remove(idMatrix.file.getName());
                    System.out.println("Matrix " + idMatrix.name + " removed!");
                    break;
                }
            }
        } else {
            for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
                Id_Matrix idMatrix = entry.getKey();
                if (idMatrix.name.equals(input)) {
                    matrixMap.remove(idMatrix);
                    System.out.println("Matrix " + idMatrix.name + " removed!");

                }
            }
        }
    }

    public void saveMatrix(String name, String file) throws IOException {


        File file1 = new File(file);

        //  System.out.println(file1.getAbsolutePath());

        //  BufferedWriter  writer = new BufferedWriter(new FileWriter(file1));


        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : matrixMap.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            if (idMatrix.compareNames(name)) {
                BigInteger[][] matrix = entry.getValue();

                service.submit(new SaveMatriceThread(name, file, matrix, idMatrix));
            }
        }
    }

    public Map<Id_Matrix, BigInteger[][]> getMatrixMap() {
        return matrixMap;
    }

    public void addExtractedMatrixAsync(Map<Id_Matrix, BigInteger[][]> result) {


        Future<Map<Id_Matrix, BigInteger[][]>> rezult = service.submit(new MultMatriceThread(result, matrixMap));
//
        try {
            matrixMap = rezult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


    }

    public BigInteger[][] getRegularMatrix(String f1) {
        Map<Id_Matrix, BigInteger[][]> result = getMatrixMap();

        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : result.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            if (idMatrix.compareNames(f1)) {
                BigInteger[][] matrix = entry.getValue();


                return matrix;
            }
        }

        return null;

    }
}
