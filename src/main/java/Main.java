import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Function;

public class Main {
  private final static ObjectMapper mapper = new ObjectMapper();

  public static void main(String[] args) throws IOException {
    Function<String, byte[]> getDataByUrl = url -> {
      HttpGet request = new HttpGet(url);
      request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
      try (CloseableHttpClient httpClient = HttpClients.createDefault();
           CloseableHttpResponse response = httpClient.execute(request)) {
        return response.getEntity().getContent().readAllBytes();
      } catch (IOException e) {
        System.out.println("Произошла ошибка: " + e.getMessage());
        return null;
      }
    };

    byte[] data = getDataByUrl
            .apply("https://api.nasa.gov/planetary/apod?api_key=UR9fSe8mGSf9exriuBK9v2rXXidb6IJltD8WC2Fr");
    Entity entity = mapper.readValue(data, new TypeReference<>() {
    });

    String url = entity.getUrl();
    String filename = url.split("/")[url.split("/").length - 1];
    data = getDataByUrl.apply(url);
    try (FileOutputStream out = new FileOutputStream(filename);
         BufferedOutputStream bos = new BufferedOutputStream(out)) {
      bos.write(data, 0, data.length);
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }
}
