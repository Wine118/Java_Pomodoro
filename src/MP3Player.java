import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class MP3Player {
    public static void play(String filePath){
        Thread thread = new Thread(() -> {
            try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
                Player player = new Player(fileInputStream);
                player.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
