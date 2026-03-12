
package controller;

import static spark.Spark.*;

public class VideoFileController {

    public VideoFileController() {
    }
        
    public void activarServer(){
        staticFiles.location("/public");
    }
    
}
