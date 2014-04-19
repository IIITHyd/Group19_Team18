
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;


public class Util {

	public static void dumpInFile(TreeSet<Results> ts, String string) {
		// TODO Auto-generated method stub
		//System.out.println(string+" to write "+ts.size());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(string));
			for(Results r : ts){
				for(String s : r.record){
					bw.write(s+",");
				}
				bw.write("\n");
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
   public static void dumpInFile(HashMap<String, String>content, String fileName) {
        // TODO Auto-generated method stub
        //System.out.println(string+" to write "+ts.size());
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            for(Map.Entry<String,String> entry : content.entrySet()) 
            {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

