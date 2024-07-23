package components.Matrix;

import java.io.File;

public class Id_Matrix {

    public String name;
    public int row;
    public int column;
    public Boolean isAsynch;
    public File file;

    public Id_Matrix(String name) {
        this.name = name;
    }
    public Id_Matrix(String name, int row, int column) {
        this.name = name;
        this.row = row;
        this.column = column;
    }

    public Id_Matrix(String name, int row, int column,File file) {
        this.name = name;
        this.row = row;
        this.column = column;
        this.file=file;
    }


    public boolean compareNames( String inputName){
   return this.name.equals(inputName);
  }

}
