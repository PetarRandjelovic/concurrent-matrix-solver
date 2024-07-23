package components.task;

import components.Main;
import components.Matrix.Id_Matrix;
import components.Matrix.M_Matrix;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class MultiplyTask  implements Task{

    private final TaskType taskType;
    Future<M_Matrix> future;

    private String file1;
    private String file2;
    private String newName;

    Future<Map<Id_Matrix, BigInteger[][]>> futureMatrice;

    public Boolean isAsynch;

    public MultiplyTask(String file1,String file2, TaskType taskType, Boolean isAsynch,String newName){
        this.taskType = taskType;
        this.file1=file1;
        this.file2=file2;
        this.isAsynch=isAsynch;
        this.newName=newName;

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
        this.futureMatrice= (Future<Map<Id_Matrix, BigInteger[][]>>) Main.multiplyPool.submit(task);
        return futureMatrice;
    }


    public String getFile1() {
        return file1;
    }
    public String getfile2(){
        return file2;
    }

    public String getNewName(){
        return newName;
    }

}
