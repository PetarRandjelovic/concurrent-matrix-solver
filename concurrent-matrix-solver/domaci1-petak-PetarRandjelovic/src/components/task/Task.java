package components.task;

import components.Matrix.Id_Matrix;
import components.Matrix.M_Matrix;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public interface Task {

    TaskType getType();
    Future<M_Matrix> initiate(RecursiveTask<?> task);

    Future<Map<Id_Matrix, BigInteger[][]>> initiateMatrice(RecursiveTask<?> task);

}
