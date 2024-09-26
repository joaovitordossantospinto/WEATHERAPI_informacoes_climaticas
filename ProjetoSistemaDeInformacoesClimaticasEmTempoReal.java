import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjetoSistemaDeInformacoesClimaticasEmTempoReal{

	public static void main (String[] args){
	Scanner scanner = new Scanner(System.in);
	System.out.print("Digite o nome da cidade: ");
	String cidade = scanner.nextLine();

	try{
		String dadosClimaticos = getDadosClimaticos(cidade);

		if (dadosClimaticos.contains("\"code\":1006")){
			System.out.println("Localização não encontrada! tente novamente.");
		} else {
			imprimirDadosClimaticos(dadosClimaticos);
		}
	} catch (Exception e){
		System.out.println(e.getMessage());
	}
}

public static String getDadosClimaticos(String cidade) throws Exception{
	String apiKey = Files.readString(Paths.get("API_KEY.txt")).trim();

	String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);

	String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + formataNomeCidade;

	HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create(apiUrl))
		.build();

	HttpClient client = HttpClient.newHttpClient();
	
	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	return response.body();	
}

public static void imprimirDadosClimaticos(String dados){
	JSONObject dadosJson = new JSONObject(dados);
	JSONObject informacoesMetereologicas = dadosJson.getJSONObject("current");

	String cidade = dadosJson.getJSONObject("location").getString("name");
	String pais = dadosJson.getJSONObject("location").getString("country");
	String condicaoTempo = informacoesMetereologicas.getJSONObject("condition").getString("text");
	int umidade = informacoesMetereologicas.getInt("humidity");
	float velocidadeVento = informacoesMetereologicas.getFloat("wind_kph");
	float pressaoAtmosferica = informacoesMetereologicas.getFloat("pressure_mb");
	float sensacaoTermica = informacoesMetereologicas.getFloat("feelslike_c");
	float temperaturaAtual = informacoesMetereologicas.getFloat("temp_c");
	String dataHoraString = informacoesMetereologicas.getString("last_updated");

	System.out.println("Informações Metereológicas para " + cidade + ", " + pais);
	System.out.println("Data e Hora: " + dataHoraString);
	System.out.println("Temperatura Atual: " + temperaturaAtual + "°C");
	System.out.println("Sensação térmica: " + sensacaoTermica + "°C");
	System.out.println("Condição do tempo: " + condicaoTempo);
	System.out.println("Umidade: " + umidade + "%");
	System.out.println("Velocidade do Vento: " + velocidadeVento + "km/h");
	System.out.println("Pressão Atmosférica: " + pressaoAtmosferica + "mb");
}
}