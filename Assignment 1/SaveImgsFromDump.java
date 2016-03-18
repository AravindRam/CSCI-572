import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveImgsFromDump {

	//Dump File Path : This paths needs to me modified to point to Nutch installation folder
	private static final String DUMP_FILE_PATH = "/home/sandeep/csci572/nutch/runtime/local/bin/dump/dump";
	//Nutch Bin path : This paths needs to me modified to point to Nutch installation folder
	private static final String OUTPUT_IMG_DIR_PATH = "/home/sandeep/csci572/nutch/runtime/local/bin/";
	
	private static String IMG_FOLDER = "";

	public static void main(String[] args) throws Exception {
		System.out.println("======================================================================");
		System.out.println("************************* Start Reading Crawl Dump URLs ********************");
		System.out.println("======================================================================");

		if(args.length > 0){
			IMG_FOLDER = args[0] + "/";
		}else{
			IMG_FOLDER = "imgdump/";
		}
		String sCurrentLine;
		int successCount = 1;
		int errorCount = 1;
		BufferedReader br = new BufferedReader(new FileReader(DUMP_FILE_PATH));
		Set<String> successUrls = new LinkedHashSet<String>();
		Set<String> errorUrls = new LinkedHashSet<String>();

		String imageUrl = "";
		while ((sCurrentLine = br.readLine()) != null) {

			Pattern p = Pattern.compile("(URL|url)::.*");
			Matcher m = p.matcher(sCurrentLine);

			try{				

				if (m.find()) {
					imageUrl = m.group().substring(6);
					// Check if URL is a valid image url by checking extension. If its a valid url return image name.
					String  imageName = checkUrlAndGetImageName(imageUrl);

					if(imageName != null) {
						//Save Image to directory
						saveImage(imageUrl, imageName, successCount);
						successUrls.add(imageUrl);
						successCount ++;
					}
				}	
			}catch(SocketTimeoutException ste){
				errorCount++;
				errorUrls.add(imageUrl);
				System.out.println("SocketTimeoutException occured for url : "+ imageUrl);
				continue;
			}catch (UnknownHostException uhe) {
				errorCount++;
				errorUrls.add(imageUrl);
				System.out.println("UnknownHostException occured for url : "+ imageUrl);
				continue;
			}catch(IOException ioe){
				errorCount++;
				errorUrls.add(imageUrl);
				System.out.println("IOException occured for url : "+ imageUrl);
				continue;				
			}catch (Exception e) {
				e.printStackTrace();
			}

		}

		br.close();

		//Write a summary file with the success and error url count
		writeSummaryFile(successUrls, errorUrls, successCount, errorCount);
		System.out.println("======================================================================");
		System.out.println("************************* END : Crawl SUMMARY ********************");
		System.out.println("# Success count : " + (successCount-1));
		System.out.println("# Error count : " + errorCount);
		System.out.println("Please check 'summary.txt' for url details");
		System.out.println("======================================================================");
	}

	private static void writeSummaryFile(Set<String> successUrls, Set<String> errorUrls, int successCount, int errorCount){
		try {
			FileWriter writer = new FileWriter(OUTPUT_IMG_DIR_PATH + "summary.txt"); 

			writer.write("======================================================================"+ "\n");
			writer.write("Success count : " + successCount + "\n");
			writer.write("Success URLS : " + "\n");
			for(String str: successUrls) {			  
				writer.write(str + "\n");			
			}
			writer.write("======================================================================"+ "\n");
			writer.write("Error count : " + errorCount + "\n");
			writer.write("Error URLS : " + "\n");
			for(String str: errorUrls) {			  
				writer.write(str + "\n");			
			}

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check if URL is a valid image url by checking extension. If its a valid url return image name.
	 * @param url
	 * @return
	 */
	private static String checkUrlAndGetImageName(String url){
		String imageName = null;
		Pattern p = Pattern.compile("[^/]+\\.(jpg|jpeg|png|gif|JPG|JPEG|PNG|GIF)");
		Matcher m = p.matcher(url);

		if (m.find()) {
			imageName = m.group();
		}
		return imageName;
	}

/**
 * Main method which makes a connection request and downloads the images from the urls dumped by nutch
 * @param imageUrl
 * @param imageName
 * @param count
 * @throws IOException
 */
	private static void saveImage(String imageUrl, String imageName, int count) throws IOException {		

		URL url = new URL(imageUrl);
		HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
		httpcon.addRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		httpcon.setRequestMethod("GET");
		httpcon.setConnectTimeout(2000);
		httpcon.connect();

		BufferedInputStream is = null;
		OutputStream os = null;


		int responseCode = httpcon.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_MOVED_TEMP &&
				responseCode != HttpURLConnection.HTTP_NOT_FOUND &&
				responseCode != HttpURLConnection.HTTP_FORBIDDEN) {

			is = new BufferedInputStream(httpcon.getInputStream());

			File file = new File(OUTPUT_IMG_DIR_PATH + IMG_FOLDER  + imageName);
			file.getParentFile().mkdirs();
			os = new FileOutputStream(file);

			byte[] b = new byte[8192];
			int length;
			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			System.out.println(count + " : Saving the Url : (" +responseCode+ ") " + imageUrl + " : Name : " + imageName);		
		} else {
			System.out.println("      Unable to Save : (" + responseCode + ") " + imageUrl);
		}



	}

}