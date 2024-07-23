package components;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Config instance = null; //singleton
    private final Properties properties;
    private int sys_explorer_sleep_time;
    private int maximum_file_chunk_size;
    private int maximum_rows_size;
    private String start_dir;

    public static Config getInstance(){
        if (instance == null)
            instance = new Config();
        return instance;
    }
    private Config(){
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("resources/application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        load();
    }
    private void load(){
        sys_explorer_sleep_time = Integer.parseInt(read_line("sys_explorer_sleep_time"));
        maximum_file_chunk_size = Integer.parseInt(read_line("maximum_file_chunk_size"));
        maximum_rows_size = Integer.parseInt(read_line("maximum_rows_size"));
        start_dir = read_line("start_dir");
    }
    private String read_line(String name){
        //System.out.println("READING: "+properties.getProperty(name));
        return properties.getProperty(name);
    }

    public Properties getProperties() {
        return properties;
    }

    public int getSys_explorer_sleep_time() {
        return sys_explorer_sleep_time;
    }

    public int getMaximum_file_chunk_size() {
        return maximum_file_chunk_size;
    }

    public int getMaximum_rows_size() {
        return maximum_rows_size;
    }

    public String getStart_dir() {
        return start_dir;
    }
}