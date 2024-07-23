package components;


import components.task.CreateTask;
import components.task.MultiplyTask;
import components.task.TaskType;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static components.Main.tasks;


public class SystemExplorer extends Thread{

    private volatile boolean running = true;
    private final CopyOnWriteArrayList<String> lista;
    private final HashMap<String, Long> lastModifiedMap;

    private final HashMap<String, Boolean> scannedDir;
    public SystemExplorer(CopyOnWriteArrayList<String> lista) {
        this.lista = lista;
        lastModifiedMap = new HashMap<>();
        scannedDir = new HashMap<>();
    }

    @Override
    public void run() {
     //   C:\Users\petar\Desktop\domaci1-petak-PetarRandjelovic_Final\domaci1-petak-PetarRandjelovic\src\components\SystemExplorer.java

        while (running) {
            for (String path : lista) {


                searchDir(new File(path), path);
            }

            try {
                Thread.sleep(Config.getInstance().getSys_explorer_sleep_time());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void searchDir(File file, String path) {
     //   dir /../test2
    //    System.out.println(file.getAbsolutePath());

        File[] listFiles = file.listFiles();
     //   System.out.println(listFiles.length);
        if (listFiles == null) {
            System.err.println("File can't open");
            lista.remove(path);
            return;
        }
        for (File files : listFiles) {
            if (files.isDirectory()) {
              //  System.out.println("dir " + files.getName());
                addTaskToQueue(files);
                searchDir(files, path);
            } else if(files.isFile() && files.getName().endsWith(".rix")){
                addTaskToQueue(files);
            }

        }
    }

    private void addTaskToQueue(File file) {
        String dirPath = file.getAbsolutePath();
        long lastModified;
        scannedDir.clear();
      //  for (File files : file.listFiles()) {
            lastModified = file.lastModified();
            if (lastModifiedMap.getOrDefault(file.getName(), 0L) != lastModified || lastModifiedMap.get(file.getName()) != lastModified) {
                lastModifiedMap.put(file.getName(), lastModified);
                if (!scannedDir.containsKey(file.getName())){
                    scannedDir.put(file.getName(), true);
                    TaskType type = TaskType.CREATE;
               //     System.out.println("USAO1");

                    if(file.getName().endsWith(".rix")){
                        tasks.add(new CreateTask(type,file,false));

                        String ime= file.getName().replace(".rix", "").toLowerCase().replace("_result","")+  file.getName().replace(".rix","").toLowerCase().replace("_result","");
                        String ime1=file.getName().replace(".rix","").toLowerCase().replace("_result","");
//                        tasks.add(new MultiplyTask(ime1,
//                                ime1, TaskType.MULTIPLY,false,ime));
                    }
                 //   System.out.println("ubaceno u task "+ file.getName() +" "+ dirPath);
                }else {
                    TaskType type = TaskType.CREATE;
                    if(file.getName().endsWith(".rix")){
                  //      System.out.println("USAO2");
                        String ime1=file.getName().replace(".rix","").toLowerCase().replace("_result","");
                        String ime= file.getName().replace(".rix", "").toLowerCase().replace("_result","")+  file.getName().replace(".rix","").toLowerCase().replace("_result","");
                        tasks.add(new CreateTask( type,file, false));
//                        tasks.add(new MultiplyTask(ime1,
//                                ime1, TaskType.MULTIPLY,false,ime));

                    }
                  //  System.out.println("updajtovano u task " + file.getName() + " " + dirPath);
                }


            }

    }

    public void stopThread() {
        running = false;
    }

    public HashMap<String, Long> getLastModifiedMap() {
        return lastModifiedMap;
    }
}
