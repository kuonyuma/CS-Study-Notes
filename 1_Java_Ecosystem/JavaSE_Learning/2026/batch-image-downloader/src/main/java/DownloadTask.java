import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class DownloadTask implements Callable<Boolean> {

    private static final HttpClient httpClient = HttpClient
        .newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();


    private final String imageUrl;

    private final Integer taskId;

    private final String saveDirectory;

    public DownloadTask(String imageUrl,String saveDirectory,Integer taskId){
        this.imageUrl = imageUrl;
        this.taskId = taskId;
        this.saveDirectory = saveDirectory;
    }

    @Override
    public Boolean call(){
        try{

            //构建http请求
            HttpRequest request =HttpRequest
                .newBuilder()
                .uri(URI.create(imageUrl))
                .GET()
                .build();

            //发送http请求
            HttpResponse<byte[]> httpResponse = httpClient.send(request,
                HttpResponse
                    .BodyHandlers
                    .ofByteArray());
            if(httpResponse.statusCode() != 200){
                System.err.printf("任务执行失败，状态码:%d",httpResponse.statusCode());
                return false;
            }

            String imageName = "image_" + taskId + ".jpg";
            Path savePath = Paths.get(saveDirectory,imageName);
            // 确保保存目录存在
            Files.createDirectories(savePath.getParent());
            // 写入文件
            Files.write(savePath, httpResponse.body());

            System.out.println("写入成功");
            return true;

        } catch (IOException e) {
            System.err.printf("IO错误: %s",e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("任务被中断%d", taskId);
            return false;
        }
    }
}
