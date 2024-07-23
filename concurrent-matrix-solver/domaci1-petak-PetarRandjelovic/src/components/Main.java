package components;

import components.Matrix.Id_Matrix;
import components.Matrix.M_Matrix;
import components.Matrix.MatrixBrain;
import components.task.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class Main {

    public static final CopyOnWriteArrayList<String> dirList = new CopyOnWriteArrayList<>();
    public static final SystemExplorer systemExplorer = new SystemExplorer(dirList);
    public static Map<Id_Matrix, Map<BigInteger[][], List<M_Matrix>>> matrixMap = new HashMap<>();
    public static BigInteger[][] matrixResult;
    public static Map<BigInteger[][], List<M_Matrix>> unutarMatrixMape = new HashMap<>();
    public static MatrixBrain matrixBrain = new MatrixBrain();
    public static Map<Id_Matrix, BigInteger[][]> bigIntegerMap = new HashMap<>();
    public static CopyOnWriteArrayList<M_Matrix> matrixList = new CopyOnWriteArrayList<>();
    private static final TaskCoordinator taskCoordinator = new TaskCoordinator();
    public static BlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
    public static ForkJoinPool pool = new ForkJoinPool();
    public static ForkJoinPool extractorPool = new ForkJoinPool(16);
    public static ForkJoinPool multiplyPool = new ForkJoinPool(16);


    public static boolean running = true;

    public static void main(String[] args) {

        Config configuration = Config.getInstance();

        startThreads();
        startAndReadTerminal();


    }


    private static void startAndReadTerminal() {
        Scanner cli = new Scanner(System.in);
        String line;
        String[] tokens;
        String task;


        dirList.add(Config.getInstance().getStart_dir());
        while (running) {
            line = cli.nextLine();
            tokens = line.split(" ");
            task = tokens[0];

            switch (task) {
                case "dir" -> {

                    if (tokens.length > 2 || tokens.length == 1) {
                        System.err.println("Too many argument");
                        continue;
                    }


                    dirList.add(tokens[1]);
                    System.out.println("Added " + Config.getInstance().getStart_dir() + "/" + tokens[1] + " to the list");

                    break;
                }
                case "stop" -> {
                    stopThreads();
                    running = false;

                }
                case "check" -> {
                    if (tokens.length != 2) {
                        System.out.println("Invalid check");

                        continue;
                    }
                    String f1 = tokens[1];
                    System.out.println(f1);
                    BigInteger[][] ha = matrixBrain.getRegularMatrix(f1);
                    if (ha == null) {
                        System.out.println("Matrix is null");
                    } else {
                        System.out.println(ha.length);
                        System.out.println(ha[0].length);


                        for (int j = 0; j < ha[0].length; j++) {
                            for (int i = 0; i < ha.length; i++) {

                                BigInteger s = ha[i][j];
                                System.out.print(i + "," + j + " = " + ha[i][j].toString() + " ");
                                System.out.println();
                            }
                        }
                    }
                }
                case "info" -> {

                    if (tokens.length > 3) {
                        System.err.println("Too many arguments");
                        continue;
                    }

                    if (tokens[1].equals("-asc")) {
                        List<Id_Matrix> infoMatrixAsc = matrixBrain.getAllInfoOnMatrixAsc();
                        printMatrixInfo(infoMatrixAsc);
                    } else if (tokens[1].equals("-desc")) {
                        List<Id_Matrix> infoMatrixAsc = matrixBrain.getAllInfoOnMatrixDesc();
                        printMatrixInfo(infoMatrixAsc);
                    } else if (tokens[1].equals("-all")) {
                        List<Id_Matrix> infoMatrixAsc = matrixBrain.getAllInfoOnMatrix();
                        printMatrixInfo(infoMatrixAsc);
                    } else if (tokens[1].contains("-s")) {
                        List<Id_Matrix> infoMatrixAsc = matrixBrain.getFirstNMatrixes(Integer.parseInt(tokens[2]));
                        printMatrixInfo(infoMatrixAsc);
                    } else if (tokens[1].contains("-e")) {
                        List<Id_Matrix> infoMatrixAsc = matrixBrain.getLastNMatrixes(Integer.parseInt(tokens[2]));
                        printMatrixInfo(infoMatrixAsc);
                    } else {
                        Id_Matrix infoMatrix = matrixBrain.getInfoOnMatrix(tokens[1]);
                        if (infoMatrix != null) {
                            if (infoMatrix.file != null) {
                                System.out.println("Matrix name: " + infoMatrix.name + " Row " + infoMatrix.row + " Column " + infoMatrix.column + " Path " + infoMatrix.file.getAbsolutePath());
                            } else {
                                System.out.println("Matrix name: " + infoMatrix.name + " Row " + infoMatrix.row + " Column " + infoMatrix.column + " Path: File not found");
                            }
                        } else {
                            System.out.println("Input not valid or matrix not found");
                        }
                    }
                    break;
                }
                case "infomator" -> {


                    //dirList.add(tokens[1]);


                    break;
                }
                case "testic" -> {

                }
                case "clear" -> {
                    if (tokens.length == 2) {

                        if (tokens[1].endsWith(".rix")) {
                            matrixBrain.clear(tokens[1], true);
                        } else {
                            matrixBrain.clear(tokens[1], false);
                        }
                    } else {

                        System.err.println("Invalid clear");
                    }
                    continue;
                }
                case "mult" -> {
                    MultiplyTask multiplyTask;

                    if (tokens.length < 3 || tokens.length > 6) {
                        System.err.println("Not valid for multiply");
                        continue;
                    }

                    String fajl1 = tokens[1];
                    String fajl2 = tokens[2];

                    if (tokens.length == 5 && tokens[3].equals("-name")) {
                        System.out.println("Attemp to multiply with new name " + tokens[4]);
                        String newName = tokens[4];
                        multiplyTask = new MultiplyTask(tokens[1], tokens[2], TaskType.MULTIPLY, false, newName);
                        tasks.add(multiplyTask);
                        String f1 = multiplyTask.getFile1().replace(".rix", "").toLowerCase();
                        String f2 = multiplyTask.getfile2().replace(".rix", "").toLowerCase();
                        String name = f1 + f2;

                    } else if (tokens.length == 6 && tokens[3].equals("-async") && tokens[4].equals("-name")) {
                        String newName = tokens[5];
                        String f1 = fajl1.replace(".rix", "").toLowerCase();
                        String f2 = fajl2.replace(".rix", "").toLowerCase();
                        String name = f1 + f2;
                        multiplyTask = new MultiplyTask(tokens[1], tokens[2], TaskType.MULTIPLY, true, newName);
                        matrixBrain.addMultipliedMatrixAsync(newName, multiplyTask);

                    } else if (tokens.length == 4 && tokens[3].equals("-async")) {
                        String f1 = fajl1.replace(".rix", "").toLowerCase();
                        String f2 = fajl2.replace(".rix", "").toLowerCase();
                        String name = f1 + f2;
                        multiplyTask = new MultiplyTask(tokens[1], tokens[2], TaskType.MULTIPLY, true, name);
                        matrixBrain.addMultipliedMatrixAsync(name, multiplyTask);

                    } else if (tokens.length == 3) {
                        String f1 = fajl1.replace(".rix", "").toLowerCase();
                        String f2 = fajl2.replace(".rix", "").toLowerCase();
                        String name = f1 + f2;
                        multiplyTask = new MultiplyTask(tokens[1], tokens[2], TaskType.MULTIPLY, false, name);


                        tasks.add(multiplyTask);


                    } else {
                        System.err.println("Not valid for multiply or not valid argument");
                        continue;
                    }

                }
                case "save" -> {

                    if (tokens.length == 5) {
                        try {
                            matrixBrain.saveMatrix(tokens[2], tokens[4]);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        System.err.println("Not valid for save");
                    }
                }

            }
        }
        cli.close();
        System.out.println("Main finished");
    }

    public static void startThreads() {
        systemExplorer.start();
        taskCoordinator.start();


    }

    private static void printMatrixInfo(List<Id_Matrix> matrixList) {
        for (Id_Matrix id_Matrix : matrixList) {
            if (id_Matrix != null) {
                if (id_Matrix.file != null) {
                    System.out.println("Matrix name: " + id_Matrix.name + " Row " + id_Matrix.row + " Column " + id_Matrix.column + " Path " + id_Matrix.file.getAbsolutePath());
                } else {
                    System.out.println("Matrix name: " + id_Matrix.name + " Row " + id_Matrix.row + " Column " + id_Matrix.column + " Path: Path file not found");
                }
            } else {
                System.out.println("Matrix not found");
            }
        }
    }

    public static void stopThreads() {

        //tasks.clear();

        tasks.add(new CreateTask(TaskType.CREATE, null, true));

        while (!tasks.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        systemExplorer.stopThread();


    }

}