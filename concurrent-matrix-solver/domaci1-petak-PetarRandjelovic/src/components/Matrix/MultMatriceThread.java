package components.Matrix;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.Callable;

public class MultMatriceThread implements Callable {


    private Map<Id_Matrix, BigInteger[][]> result;
    private Map<Id_Matrix, BigInteger[][]> matrixMap;
    public MultMatriceThread( Map<Id_Matrix, BigInteger[][]> result, Map<Id_Matrix, BigInteger[][]> matrixMap) {
        this.result = result;
        this.matrixMap = matrixMap;
    }
    @Override
    public Object call() throws Exception {


        boolean found=true;
        for (Map.Entry<Id_Matrix, BigInteger[][]> entry : result.entrySet()) {
            Id_Matrix idMatrix = entry.getKey();
            for (Map.Entry<Id_Matrix, BigInteger[][]> postojecMatrica : matrixMap.entrySet()) {
                Id_Matrix idMatrixPostojeca = postojecMatrica.getKey();
                if (idMatrix.name.equals(idMatrixPostojeca.name)) {
                    found=false;
                    matrixMap.putAll(result);
                    matrixMap.remove(idMatrixPostojeca);
                    break;
                }
            }

        }

        if(found){
            matrixMap.putAll(result);
            System.out.println("Matrix saved successfully! -async");
        }


        return matrixMap;
    }
}
