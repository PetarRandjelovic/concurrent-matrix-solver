package components.task;

import components.Main;
import components.Matrix.Id_Matrix;
import components.Matrix.M_Matrix;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;



public class CreateTask implements Task{


    private final TaskType taskType;
    Future<M_Matrix> future;
    boolean stop;
    Future<Map<Id_Matrix, BigInteger[][]>> futureMatrice;

    private File file;

    public CreateTask( TaskType taskType,File file, boolean stop) {
        this.taskType = taskType;
        this.file=file;
        this.stop=stop;

    }

    @Override
    public TaskType getType() {
        return taskType;
    }

    @Override
    public Future<M_Matrix> initiate(RecursiveTask<?> task) {


        return null;
    }

    @Override
    public Future<Map<Id_Matrix, BigInteger[][]>> initiateMatrice(RecursiveTask<?> task) {
        this.futureMatrice= (Future<Map<Id_Matrix, BigInteger[][]>>) Main.extractorPool.submit(task);
        return futureMatrice;

    }

    public File getFile() {
        return file;
    }


    public boolean isStop() {
        return stop;
    }
}
