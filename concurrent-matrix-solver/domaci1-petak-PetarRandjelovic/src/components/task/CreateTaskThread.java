package components.task;

import components.Config;
import components.Main;
import components.Matrix.Id_Matrix;
import components.Matrix.M_Matrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateTaskThread extends RecursiveTask<Object> {
    private static long currentPosition = 0;
    CreateTask createTask;
    private List<File> files;
    private File file;
    private final int LIMIT = 1024;
    private int start;
    private int end;
    private M_Matrix matrix;
    private String nameFile;

    private CopyOnWriteArrayList<M_Matrix> matrices;


    private boolean isDone;


    private BigInteger[][] mainMatrix;

    private Id_Matrix mainIdMatrix;

    private static AtomicInteger count = new AtomicInteger(0);

    public CreateTaskThread(CreateTask createTask, File file, int start, int end, String nameFile, CopyOnWriteArrayList<M_Matrix> matrices) {

        Path path = Paths.get(createTask.getFile().getPath());
        Id_Matrix idMatrix = null;
        BigInteger[][] matrix = new BigInteger[0][];
        try {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Future<Integer> result = fileChannel.read(buffer, 0);
            while (!result.isDone()) {
            }
            buffer.flip();
            String firstLine = StandardCharsets.UTF_8.decode(buffer).toString().split("\n")[0];
            String[] parts = firstLine.split(",\\s*");
            String matrixName = parts[0].split("=")[1];
            String r = parts[1].split("=")[1];
            int rows = Integer.parseInt(r);
            String c = parts[2].split("=")[1];
            int cols = Integer.parseInt(c.split("\r")[0]);
            idMatrix = new Id_Matrix(matrixName.toLowerCase(), rows, cols, createTask.getFile());
            matrix = new BigInteger[rows][cols];


        } catch (Exception e) {
            e.printStackTrace();
        }


        this.createTask = createTask;
        this.file = file;
        this.start = start;
        this.end = end;
        this.nameFile = nameFile;
        this.matrices = matrices;
        isDone = true;
        this.mainMatrix = matrix;
        this.mainIdMatrix = idMatrix;
    }

    public CreateTaskThread(File file, int start, int end, String nameFile, CopyOnWriteArrayList<M_Matrix> matrices, BigInteger[][] mainMatrix, Id_Matrix mainIdMatrix) {
        this.file = file;
        this.start = start;
        this.end = end;
        this.nameFile = nameFile;
        isDone = false;
        this.matrices = matrices;
        this.mainMatrix = mainMatrix;
        this.mainIdMatrix = mainIdMatrix;
    }


    @Override
    protected Object compute() {

        if (end - start < Config.getInstance().getMaximum_file_chunk_size()) {
            readFileAsync(file, nameFile, matrices, mainMatrix);
        } else {


            File[] splitFiles = new File[0];
            try {
                splitFiles = splitFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int mid = (end - start) / 2 + start;

            CreateTaskThread left = new CreateTaskThread(splitFiles[0], start, mid, nameFile, matrices, mainMatrix, mainIdMatrix);
            CreateTaskThread right = new CreateTaskThread(splitFiles[1], mid, end, nameFile, matrices, mainMatrix, mainIdMatrix);

            left.fork();
            Object rightResult = right.compute();
            Object leftResult = left.join();

            if (isDone) {

                BigInteger[][] bigIntegerLista;
                int maxRow = 0;
                int maxColumn = 0;
                String mainName = "";

                bigIntegerLista = new BigInteger[maxRow][maxColumn];


                for (int i = 0; i < mainIdMatrix.row; i++) {
                    for (int j = 0; j < mainIdMatrix.column; j++) {
                        if (mainMatrix[i][j] != null) {
                            continue;
                        }
                        mainMatrix[i][j] = new BigInteger("0");
                    }
                }
                Id_Matrix id_matrix = new Id_Matrix(mainName, maxRow, maxColumn, file);
                BigInteger[][] bigInteger = bigIntegerLista;
                Main.unutarMatrixMape.put(bigIntegerLista, Main.matrixList);
                Main.matrixMap.put(id_matrix, Main.unutarMatrixMape);
                Main.bigIntegerMap.put(id_matrix, bigInteger);

                Map<Id_Matrix, BigInteger[][]> rezultat = new HashMap<>();

                rezultat.put(mainIdMatrix, mainMatrix);


                return rezultat;
            }

        }


        return null;
    }

    public static File[] splitFile(File inputFile) throws IOException {
        if (!inputFile.exists()) {
            throw new IOException("Input file does not exist");
        }

        byte[] fileContent = Files.readAllBytes(inputFile.toPath());
        String fileContentString = new String(fileContent, StandardCharsets.UTF_8);

        int splitPoint = findSplitPoint(fileContentString);
        byte[] firstHalf = Arrays.copyOfRange(fileContent, 0, splitPoint);
        byte[] secondHalf = Arrays.copyOfRange(fileContent, splitPoint, fileContent.length);
        File[] splitFiles = new File[2];
        splitFiles[0] = createTempFileS("split1_", inputFile.getName(), firstHalf);
        splitFiles[1] = createTempFileS("split2_", inputFile.getName(), secondHalf);

        return splitFiles;
    }

    private static int findSplitPoint(String content) {
        int splitPoint = content.length() / 2;
        int index = content.indexOf('\n', splitPoint);
        if (index == -1) {
            index = content.lastIndexOf('\n', splitPoint);
            if (index == -1) {
                index = splitPoint;
            }
        }
        return index;
    }

    private static File createTempFileS(String prefix, String originalName, byte[] data) throws IOException {
        File tempFile = File.createTempFile(prefix, "_" + originalName);
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), data);
        return tempFile;
    }


    private static File createTempFile(String name) throws IOException {

        return File.createTempFile(name, ".rix");
    }

    private static byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }


    public static void readFileAsync(File file, String name, List<M_Matrix> matrices, BigInteger[][] mainMatrixs) {
        Path path = file.toPath();
        AsynchronousFileChannel channel = null;
        try {
            channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());

            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    attachment.flip();
                    String content = StandardCharsets.UTF_8.decode(attachment).toString();
                    String[] lines = content.split("\\r?\\n");

                    List<M_Matrix> matrixList = new ArrayList<>();
                    for (String line : lines) {
                        if (line.isEmpty())
                            continue;

                        M_Matrix matrix = parseLine(line, name);
                        if (matrix == null) {
                        } else {
                            if (!(matrix.value.equals("MAX"))) {
                                mainMatrixs[Integer.parseInt(matrix.row)][Integer.parseInt(matrix.column)] = new BigInteger(matrix.value);
                            }

                            matrixList.add(matrix);
                            matrices.add(matrix);
                        }

                    }

                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    System.out.println(" SUP");

                }
            });
        } catch (IOException e) {
            System.out.println("Error closing channel1");
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    System.out.println("Error closing channel");
                    e.printStackTrace();
                }
            }
        }

    }

    private static M_Matrix parseLine(String line, String name) {

        String patternString = "matrix_name=(\\w+), rows=(\\d+), cols=(\\d+)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String matrixName = matcher.group(1);
            String rows = matcher.group(2);
            String columns = matcher.group(3);
            return new M_Matrix(rows, columns, "MAX", matrixName);
        }

        String[] parts = line.split(" = ");
        String[] coordinates = parts[0].split(",");
        String row = coordinates[0];
        String column = coordinates[1];
        String value = parts[1].trim();

        return new M_Matrix(row, column, value, name);
    }


}
