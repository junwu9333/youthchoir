import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.stream.Stream;

import org.json.*;

import org.apache.commons.io.IOUtils;

public class DataProcessor {
	String fileName = "";
	ArrayList<String> results = new ArrayList<String>();

	public DataProcessor() {

	}

	private ArrayList<String> splitRowToArrayList(String row) {
		String[] rowData = row.split(",");
		ArrayList<String> data = new ArrayList<String>();
		for (String str : rowData) {
			data.add(str.trim());
		}
		return data;

	}

	public ArrayList<String> readInFile(String fileName) {
		ArrayList<String> content = new ArrayList<String>();
		// read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			stream.forEach((ele) -> content.add(ele)); 

		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public ArrayList<String> listFilesFromPath(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> files = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
 
				files.add(listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		return files;
	}
	
	
	private String getNormalizeData(ArrayList<String> data, int index) {
		return  "?".equals(data.get(index))? "0": data.get(index);
	}

	/**
	 * @param fileName
	 */
	public void proccessData(ArrayList<String> fileContent, String fileName, float de, float pbMin, float pbMax,  float roa, float yrChange, int nGrowth) {
		String firstRow = fileContent.remove(0);
         ArrayList<String> headers = this.splitRowToArrayList( firstRow );
        int deIndex = headers.indexOf("debtToEquity");
    	int pbIndex = headers.indexOf("priceToBook");
    	int roaIndex = headers.indexOf("returnOnAssets");
    	int yrChangeIndex = headers.indexOf("52WeekChange");
    	int nGrowIndex = headers.indexOf("nGrowth");
     	
    	if(nGrowIndex == -1) return;
  		
        fileContent.forEach(action->{
        	ArrayList<String> dataRow = this.splitRowToArrayList(action);
        	Number number;
			try {				 
				float myDe  = NumberFormat.getInstance().parse(this.getNormalizeData(dataRow, deIndex)).floatValue();
				float myPb =  NumberFormat.getInstance().parse(this.getNormalizeData(dataRow, pbIndex)).floatValue();
				float myRoa  = NumberFormat.getInstance().parse(this.getNormalizeData(dataRow, roaIndex)).floatValue();
				float myYrChange =  NumberFormat.getInstance().parse(this.getNormalizeData(dataRow, yrChangeIndex)).floatValue();			 
				int nG = NumberFormat.getInstance().parse(this.getNormalizeData(dataRow, nGrowIndex)).intValue();
				if(myDe < de && myPb > pbMin && myPb < pbMax && myRoa > roa && myYrChange < yrChange && nG >= nGrowth) {
				//	System.out.println(fileName + " : " + action);
					this.results.add(fileName + " " + this.splitRowToArrayList(action).get(0));
				}
				
				
			} catch (ParseException | IndexOutOfBoundsException ae) {
				// TODO Auto-generated catch block
			//	System.out.println("first row=" + firstRow);
		//		System.out.println("exception in dataProcess: " + fileName + " action =" + action);
				ae.printStackTrace();
			} 
        });
        
        
                 
		
	}
	
	
	public void printResults() {
		this.results.forEach(System.out::println);
	}

	public static void main(String[] args) {
 
		DataProcessor transform = new DataProcessor();
		String path = "C:\\user.awu\\docs\\inv\\1718";
		ArrayList<String> files = transform.listFilesFromPath(path);
		// files = null;

		for (String fileName : files) {
			try {
 
				ArrayList<String> ctnt = transform.readInFile(path + "/" + fileName);
				transform.proccessData(ctnt, fileName, 30f, 0.2f, 2f, 0.1f, -0.49f, 3);
				transform.printResults();
			} catch (JSONException jexp) {
				jexp.printStackTrace();
			} catch (Exception exp) {
				System.out.println("exeption in main");
				exp.printStackTrace();
			}
		}
 
	}
}