package components.task;


import components.Main;
import components.Matrix.Id_Matrix;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static components.Main.tasks;

public class TaskCoordinator extends Thread {

    private boolean running = true;

    @Override
    public void run() {

        while (running) {
            try {
                Task task = tasks.take();

                if (task.getType() == TaskType.CREATE) {


                    CreateTask createTask = (CreateTask) task;
                    if (createTask.isStop()) {
                        // Main.pool.shutdown();
                        stopThread();
                        System.out.println("Thread is stopped");
                        Main.matrixBrain.service.shutdown();
                        Main.extractorPool.shutdown();
                        Main.multiplyPool.shutdown();
                        Main.pool.shutdown();

                        break;
                    }

                    if (createTask.getFile().length() == 0) {
                        System.err.println("File is empty");
                        continue;
                    }
//                    Future<Map<Id_Matrix, BigInteger[][]>> result1 = task.initiateMatrice(
//                            new CreateTaskThread());
                    System.out.println("Creating matrix for " + createTask.getFile());
                    Path path = Paths.get(createTask.getFile().getPath());
                    Id_Matrix idMatrix = null;
                    BigInteger[][] matrix = new BigInteger[0][];
//                    try {
//                        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
//                        ByteBuffer buffer = ByteBuffer.allocate(1024);
//                        Future<Integer> result = fileChannel.read(buffer, 0);
//                        while (!result.isDone()) {
//                            // wait for the read operation to complete
//                        }
//                        buffer.flip();
//                        String firstLine = StandardCharsets.UTF_8.decode(buffer).toString().split("\n")[0];
//                        //   System.out.println(firstLine);
//                        String[] parts = firstLine.split(",\\s*");
//                        String matrixName = parts[0].split("=")[1];
//                        String r = parts[1].split("=")[1];
//                        int rows = Integer.parseInt(r);
//                        //        System.out.println(rows);
//                        String c = parts[2].split("=")[1];
//                        //           System.out.println(c);
//                        int cols = Integer.parseInt(c.split("\r")[0]);
//                        idMatrix = new Id_Matrix(matrixName.toLowerCase(), rows, cols, createTask.getFile());
//                        matrix = new BigInteger[rows][cols];
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                   // System.out.println("Ulazim "+ createTask.getFile().getName());
                    Future<Map<Id_Matrix, BigInteger[][]>> result1 = task.initiateMatrice(
                            new CreateTaskThread(createTask, createTask.getFile(), 0, (int) createTask.getFile().length()
                                    , createTask.getFile().getName(), new CopyOnWriteArrayList<>()));



                    Map<Id_Matrix, BigInteger[][]> result;
                    try {
                        result = result1.get();

                        String ime= createTask.getFile().getName().replace(".rix", "").toLowerCase().replace("_result","")+  createTask.getFile().getName().replace(".rix","").toLowerCase().replace("_result","");
                        String ime1=createTask.getFile().getName().replace(".rix","").toLowerCase().replace("_result","");
                        tasks.add(new MultiplyTask(ime1,
                                ime1, TaskType.MULTIPLY,false,ime));

                        Main.matrixBrain.addExtractedMatrix(result);

                    } catch (ExecutionException e) {
                        System.out.println(e.getMessage() + " for " + createTask.getFile().getName());
                        File file = createTask.getFile();

                        String ime1 = file.getName().replace(".rix", "").toLowerCase().replace("_result", "");
                        String ime = file.getName().replace(".rix", "").toLowerCase().replace("_result", "") + file.getName().replace(".rix", "").toLowerCase().replace("_result", "");
                        tasks.add(new CreateTask(TaskType.CREATE, file, false));
                        tasks.add(new MultiplyTask(ime1,
                                ime1, TaskType.MULTIPLY, false, ime));
                        System.out.println("Error while creating matrix for " + createTask.getFile());
                        //  throw new RuntimeException(e);
                    }

                }

                if (task.getType() == TaskType.MULTIPLY) {
                    MultiplyTask multiplyTask = null;
                    if (task instanceof MultiplyTask) {
                        multiplyTask = (MultiplyTask) task;
                    }

                    for (Id_Matrix idMatrix : Main.matrixBrain.getMatrixMap().keySet()) {
                        {
                            if (idMatrix.compareNames(multiplyTask.getFile1())) {

                                for (Id_Matrix idMatrix1 : Main.matrixBrain.getMatrixMap().keySet()) {
                                    if (idMatrix1.compareNames(multiplyTask.getfile2())) {
                                        BigInteger[][] A = Main.matrixBrain.getMatrixMap().get(idMatrix);

                                        BigInteger[][] B = Main.matrixBrain.getMatrixMap().get(idMatrix1);

                                        int numRowsA = A.length; // Number of rows
                                        int numColsA = (numRowsA > 0) ? A[0].length : 0;

                                        int numRowsB = B.length; // Number of rows
                                        int numColsB = (numRowsB > 0) ? B[0].length : 0;

                                        if (numColsA != numRowsB && numRowsA != numColsB) {
                                            System.err.println("Matrices " + multiplyTask.getFile1() + " and " + multiplyTask.getfile2() + " can't be multiplied.");

                                    //        System.out.println(idMatrix.row);
                                                continue;
                                        } else {
                                            String newName = multiplyTask.getNewName();
                                            BigInteger[][] resultMat = new BigInteger[numRowsA][numColsB];
                                            Future<Map<Id_Matrix, BigInteger[][]>> result = task.initiateMatrice(new MatrixMultiplier(multiplyTask,
                                                            multiplyTask.getFile1(),
                                                            multiplyTask.getfile2(),
                                                            A,
                                                            B,
                                                            resultMat,
                                                            0,
                                                            idMatrix.row,
                                                            newName
                                                    )
                                            );

                                            Map<Id_Matrix, BigInteger[][]> haha = null;
                                            try {
                                                haha = result.get();
                                                if (multiplyTask.isAsynch) {
                                                    Main.matrixBrain.addExtractedMatrixAsync(haha);
                                                } else {
                                                    Main.matrixBrain.addExtractedMatrix(haha);
                                                }
                                                Main.matrixBrain.addMultipliedMatrix(haha);



                                            } catch (ExecutionException e) {
                                                System.out.println("Error while multiplying matrices " + multiplyTask.getFile1() + " and " + multiplyTask.getfile2());
                                                //  throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println("lol");
    }


    public void stopThread() {
        System.err.println("Terminating thread");
        running = false;
    }

}
