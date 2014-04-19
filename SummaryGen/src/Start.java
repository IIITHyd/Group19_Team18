import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;


public class Start {
    
    public TreeMap<String, String> tsTypeMap;
    public TreeMap<String, String> tsTypeMapSorted;
    public TreeMap<String, Integer> tsTfIdfMap;
    public HashMap<String, ArrayList<Integer>> entityTweetNum;
    static List<String> ne_max;
    
    private final String PATH_SEPERATOR = System.getProperty("file.separator");
    
    public TreeSet<Results> ts;
    public int max=1;
    public int max2=1;
    
    //private static String[] Aritter_Entities={"PERSON", "GEO-LOC", "FACILITY", "COMPANY", "PRODUCT", "FOOD", "MOVIE", "BAND", "SPORTSTEAM"};
    
    private String gCurrentNE;
    private String gCurrentClusterName, gOriginalFileName, gCurrentFilePath, gCurrentFileName;
    
    private String gOrigClustersDir, gNEClustersDir, gNEOutDir, gSummaryOutDir;
    
    Start (String [] args) 
    {
        tsTypeMap = new TreeMap<String, String>();
        tsTypeMapSorted = new TreeMap<String, String>();
        tsTfIdfMap = new TreeMap<String, Integer>();
         ne_max= new ArrayList<String>();
        entityTweetNum = new HashMap<String, ArrayList<Integer>>();
        
        if(args.length != 4)
        {
            System.err.println("Not enough arguments:");
            System.err.println("Usage: Start.class <OriginalClustersDir> <NE Clusters Dir> <Named Entities Out Dir> <Summary Out Dir>");
        }
        gOrigClustersDir = args[0];
        gNEClustersDir = args[1];
        gNEOutDir = args[2];
        gSummaryOutDir = args[3];
        
    }
    public static void main(String args[])throws Exception
    {
        if(args.length == 4)
        {
            new Start(args).run();
        }
        else
        {
            System.err.println("Not enough arguments:");
            System.err.println("Usage: Start.class <OriginalClustersDir> <NE Clusters Dir> <Named Entities Out Dir> <Summary Out Dir>");
        }

    }
        
    
    static Comparator<Results> comparator = new Comparator<Results>() {
         
    @Override
        public int compare(Results r1, Results r2) {
            // TODO Auto-generated method stub
            int x;
            
            x=r1.record[1].compareTo(r2.record[1]);
            
            if(x==0 )
            {
                int y;
                y = new Integer(Integer.parseInt(r1.record[2])).compareTo(new Integer(Integer.parseInt(r2.record[2])));
                        
                return y==0?-1:y;
            }
            else 
                return x==0?-1:x;
        }
    };//end comparator
    
    private String getTweetsFromFile(ArrayList<Integer> tweetNos, String fileName) throws Exception
    {
        BufferedReader br1 = null;
        String sCurrentLine2;
        long count1 = 0;
        String summary="";
        int maxTweets = 3;
        
        if (tweetNos == null || tweetNos.size() ==0)
            return "";
        
        br1 = new BufferedReader(new FileReader(fileName));
        
        try
        {
            while ((sCurrentLine2 = br1.readLine()) != null) 
            {
                String sCurrentLines[]=null;
                sCurrentLines=sCurrentLine2.split("http");
                sCurrentLine2=sCurrentLines[0];
                         
                count1++;
                for(int f : tweetNos)
                {
                    if(count1 == f && sCurrentLine2!=null) {
                        //System.out.println("sCurrentLine2");
                        //System.out.println(sCurrentLine2);
                        summary+="\n    "+sCurrentLine2;
                        maxTweets--;
                        break;
                        //System.out.println("sCurrentLine2");
                    }
                }
                if(maxTweets <= 0)
                    break;
            }//end while

            br1.close();
        }//end try
        catch(Exception e){
            e.printStackTrace();
        }
        return summary;
    }
    
    private void clearAll()
    {
        tsTypeMap.clear();
        tsTypeMapSorted.clear();
        tsTfIdfMap.clear();
        ne_max.clear();
        entityTweetNum.clear();
    }
    
    //to sort
    //TODO: No need to dump to file "temp"
    public void sort_all_types_tfidf()
    {
        String details[];
        TreeSet<Results> ts = new TreeSet<Results>(comparator);
        for(Map.Entry<String,String> entry : tsTypeMap.entrySet()) {
              String ne = entry.getKey();
              String type = entry.getValue();
              Integer tfidf=tsTfIdfMap.get(ne);
              String to_split=ne+","+type+","+tfidf;
              details=to_split.trim().split(",|\n");
              ts.add(new Results(details));
              Util.dumpInFile(ts,"temp");
        }

        BufferedReader br = null;
        String sCurrentLine1;
        try
        {
            br = new BufferedReader(new FileReader("temp"));
          
            while ((sCurrentLine1 = br.readLine()) != null) 
            {
                String sCurrentLine[]=null;
                sCurrentLine=sCurrentLine1.split(",");
                if (tsTypeMapSorted.containsKey(sCurrentLine[1]))
                    tsTypeMapSorted.put(sCurrentLine[1], tsTypeMapSorted.get(sCurrentLine[1])+","+sCurrentLine[0]);
                else
                    tsTypeMapSorted.put(sCurrentLine[1], sCurrentLine[0]);
            }//end while
        }//end try
        catch(Exception e){
            e.printStackTrace();
        }
    }
   
    public void get_final_tweets() throws Exception
    {
        int reached=0;
        boolean geo_all=false;
        boolean geo_there=false;
        ArrayList<String> locations = new ArrayList<String>();
        ArrayList<String> others = new ArrayList<String>();
        String finalSummary = "";
        String finalNamedEntities="";
        ne_max.clear();
        
        for(Map.Entry<String,Integer> entry : tsTfIdfMap.entrySet())
        {
            if(entry.getValue()==max)
            {
                ne_max.add(entry.getKey());
            }
        }
        
        int size=ne_max.size();

        for (String temp : ne_max)
        {
            for(Map.Entry<String,String> entry : tsTypeMap.entrySet())
            {
                if(entry.getKey()==temp)
                {
                    //System.out.println("entity is "+temp);
                    //System.out.println("type is "+entry.getValue());
                    if(entry.getValue().equals(gCurrentNE))
                    {
                        geo_there=true;//if atleast one geo-loc exists
                        reached++;
                        locations.add(entry.getKey());
                        /*for(String loc:locations)
                        {
                            System.out.println("locations "+loc);
                        }*/
                    }//end if
                    else
                    {
                        others.add(entry.getKey());
                        /*for(String otr:others)
                        {
                            System.out.println("others "+otr);
                        }*/
                    }//end else
                }// entry.getkey
            }//tsTypeMap.entrySet())
        }//end ne_max

        //if all the ne with max tfidf were geo-locs
        if(reached==size)
          geo_all=true;
      
        if(geo_all)
        {
            for(String tt : ne_max)
            {
                finalNamedEntities += tt +", ";
                finalSummary += "    " + getTweetsFromFile(entityTweetNum.get(tt), gOriginalFileName) + "\n";
            }
        }
        else if(!geo_there)
        {
            List<Integer>[] tweeRows= new ArrayList[others.size()];
            for(int i=0;i<others.size();i++)
                tweeRows[i]= new ArrayList<Integer> ();
            for(int i=0;i<others.size();i++)
            {
                for(String otr : others)
                    tweeRows[i]=entityTweetNum.get(otr);
            }
            TreeSet<Integer>[] treeset = new TreeSet [others.size()];
            for (int i = 0; i<others.size(); i++)
            { 
                treeset[i] = new TreeSet<Integer> (tweeRows[i]);
            } 
            //UNION AMONGST TYPE with max tfidf non geoloc
            TreeSet<Integer> treesetUnion = new TreeSet();
            for(int w=0;w<others.size();w++)
                treesetUnion.addAll(treeset[w]);
        
            //INTERSECTION AMONGST TYPE with max tfidf non geoloc
            TreeSet<Integer> treesetIntersect = new TreeSet();
            for(int w=0;w<others.size();w++)
                treeset[0].retainAll(treeset[w]);

            treesetIntersect=treeset[0];
            /*//PRINT UNION OUTPUT of max tfidf others
            System.out.println("union");
            for(int e : treesetUnion)
                System.out.println(e);
            //PRINT INTERSECT OUTPUT of max tfidf others
            System.out.println("intersection");
            for(int e : treesetIntersect)
                System.out.println(e);*/
            //////////////////////////////
        
            if(max >= max2*2)
            {
                try
                {
                    //    System.out.println(treesetIntersect.size());
                    if(treesetIntersect != null && treesetIntersect.size()<=2)
                        finalSummary = getTweetsFromFile(new ArrayList<Integer>(treesetIntersect), gOriginalFileName);
                    else if(treesetUnion != null && treesetUnion.size()<=2)
                        finalSummary = getTweetsFromFile(new ArrayList<Integer>(treesetUnion), gOriginalFileName);

                   //same as lower loop ie. wen max not >= 2*max2
                   else
                   {
                       //System.out.println("IMP BUT OF NO USE");
                       ArrayList<Integer> mainInt = new ArrayList<Integer>();
                       ArrayList<Integer> mainUni = new ArrayList<Integer>();
                       ArrayList<Integer> localInt = new ArrayList<Integer>();
                       ArrayList<Integer> localUni = new ArrayList<Integer>();

                       for(Map.Entry<String,String> entry : tsTypeMapSorted.entrySet()) 
                       {
                           String arr[];
                           arr=entry.getValue().split(",");
                           for(int i=0;i<arr.length;i++)
                           {
                               if(localInt.size()==0)
                                   localInt.addAll(entityTweetNum.get(arr[i]));
                               else
                                   localInt.retainAll(entityTweetNum.get(arr[i]));
 
                               localUni.addAll(entityTweetNum.get(arr[i]));
                           }
                                           
                           if(mainInt.size()==0)
                               mainInt.addAll(localInt);
                           else
                               mainInt.retainAll(localInt);
 
                           mainUni.addAll(localUni); 
                           localInt.clear();
                           localUni.clear();
                       }//for loop ends
                       
                       
                       if(mainInt != null && mainInt.size() != 0)
                           finalSummary = getTweetsFromFile(mainInt, gOriginalFileName);
                       else
                           finalSummary = getTweetsFromFile(mainUni, gOriginalFileName);
                   }
               }//try ends
               catch(Exception e){
                    e.printStackTrace();
                }
    
        }//end outer if
        else
        {
            //System.out.println("IMP BUT OF NO USE");
            ArrayList<Integer> mainInt = new ArrayList<Integer>();
            ArrayList<Integer> mainUni = new ArrayList<Integer>();
            ArrayList<Integer> localInt = new ArrayList<Integer>();
            ArrayList<Integer> localUni = new ArrayList<Integer>();
       
            for(Map.Entry<String,String> entry : tsTypeMapSorted.entrySet()) 
            {
                String arr[];
                arr=entry.getValue().split(",");
                for(int i=0;i<arr.length;i++)
                {
                    if(localInt.size()==0)
                        localInt.addAll(entityTweetNum.get(arr[i]));
                    else
                        localInt.retainAll(entityTweetNum.get(arr[i]));
 
                    localUni.addAll(entityTweetNum.get(arr[i]));
                }
                      
                if(mainInt.size()==0)
                    mainInt.addAll(localInt);
                else
                    mainInt.retainAll(localInt);
 
                mainUni.addAll(localUni); 
                localInt.clear();
                localUni.clear();
            }//for loop ends

            try
            {
                if(mainInt != null && mainInt.size()!=0)
                    finalSummary = getTweetsFromFile(mainInt, gOriginalFileName);
                else
                    finalSummary = getTweetsFromFile(mainUni, gOriginalFileName);
            }
            catch(Exception e)
            {
                System.out.println("Exception occurred: " + e.getMessage());
            }
            
        }
    }//when no imp geo loc 
    else{
        List<Integer>[] tweeRows= new ArrayList[others.size()];
        for(int i=0;i<others.size();i++)
            tweeRows[i]= new ArrayList<Integer> ();
        for(int i=0;i<others.size();i++)
        {
            for(String otr : others)
                tweeRows[i]=entityTweetNum.get(otr);
        }
        TreeSet<Integer>[] treeset = new TreeSet [others.size()];
        for (int i = 0; i<others.size(); i++)
        { 
            treeset[i] = new TreeSet<Integer> (tweeRows[i]);
        } 
        //UNION AMONGST TYPE with max tfidf non geoloc
        TreeSet<Integer> treesetUnion = new TreeSet();
        for(int w=0;w<others.size();w++)
            treesetUnion.addAll(treeset[w]);
    
        //INTERSECTION AMONGST TYPE with max tfidf non geoloc
        TreeSet<Integer> treesetIntersect = new TreeSet();
        for(int w=1;w<others.size();w++)
            treeset[0].retainAll(treeset[w]);

        treesetIntersect=treeset[0];
        /*//PRINT UNION OUTPUT of max tfidf others
        System.out.println("union");
        for(int e : treesetUnion)
            System.out.println(e);
        //PRINT INTERSECT OUTPUT of max tfidf others
        System.out.println("intersection");
        for(int e : treesetIntersect)
            System.out.println(e);*/
        //////////////////////////////
    
        if(max >= max2*2)
        {
            try
            {
                //    System.out.println(treesetIntersect.size());
                if(treesetIntersect != null && treesetIntersect.size()<=2)
                    finalSummary = getTweetsFromFile(new ArrayList<Integer>(treesetIntersect), gOriginalFileName);
                else if(treesetUnion != null && treesetUnion.size()<=2)
                {
                    finalSummary = getTweetsFromFile(new ArrayList<Integer>(treesetUnion), gOriginalFileName);
                }
                //same as lower loop ie. wen max not >= 2*max2
                else
                {
                   //System.out.println("IMP BUT OF NO USE");
                   ArrayList<Integer> mainInt = new ArrayList<Integer>();
                   ArrayList<Integer> mainUni = new ArrayList<Integer>();
                   ArrayList<Integer> localInt = new ArrayList<Integer>();
                   ArrayList<Integer> localUni = new ArrayList<Integer>();

                   for(Map.Entry<String,String> entry : tsTypeMapSorted.entrySet()) 
                   {
                       String arr[];
                       arr=entry.getValue().split(",");
                       for(int i=0;i<arr.length;i++)
                       {
                           if(localInt.size()==0)
                               localInt.addAll(entityTweetNum.get(arr[i]));
                           else
                               localInt.retainAll(entityTweetNum.get(arr[i]));

                           localUni.addAll(entityTweetNum.get(arr[i]));
                       }
                                       
                       if(mainInt.size()==0)
                           mainInt.addAll(localInt);
                       else
                           mainInt.retainAll(localInt);

                       mainUni.addAll(localUni); 
                       localInt.clear();
                       localUni.clear();
                   }//for loop ends
                   
                   if(mainInt!= null && mainInt.size() !=0)
                       finalSummary = getTweetsFromFile(mainInt, gOriginalFileName);
                   else
                       finalSummary = getTweetsFromFile(mainUni, gOriginalFileName);
               }
           }//try ends
           catch(Exception e){
                e.printStackTrace();
            }
        }//end outer if
        else
            {
            //System.out.println("IMP BUT OF NO USE");
            ArrayList<Integer> mainInt = new ArrayList<Integer>();
            ArrayList<Integer> mainUni = new ArrayList<Integer>();
            ArrayList<Integer> localInt = new ArrayList<Integer>();
            ArrayList<Integer> localUni = new ArrayList<Integer>();
       
            for(Map.Entry<String,String> entry : tsTypeMapSorted.entrySet()) 
            {
                String arr[];
                arr=entry.getValue().split(",");
                for(int i=0;i<arr.length;i++)
                {
                    if(localInt.size()==0)
                        localInt.addAll(entityTweetNum.get(arr[i]));
                    else
                        localInt.retainAll(entityTweetNum.get(arr[i]));
    
                    localUni.addAll(entityTweetNum.get(arr[i]));
                }
                      
                if(mainInt.size()==0)
                    mainInt.addAll(localInt);
                else
                    mainInt.retainAll(localInt);
    
                mainUni.addAll(localUni); 
                localInt.clear();
                localUni.clear();
            }//for loop ends
    
            try
            {
                if(mainInt!= null && mainInt.size() !=0)
                    finalSummary = getTweetsFromFile(mainInt, gOriginalFileName);
                else
                    finalSummary = getTweetsFromFile(mainUni, gOriginalFileName);
            }
            catch(Exception e)
            {
                System.out.println("Exception Occurred: " + e.getMessage());
            }
        }
    } //end  geo loc there as max tfidf but others are also there , geoall=false , geothere=true
        
    BufferedWriter bw = new BufferedWriter(new FileWriter(gSummaryOutDir+ PATH_SEPERATOR + gCurrentFileName + "_" + gCurrentNE));
    bw.write("Tweet Cluster: " + gCurrentClusterName + "\n");
    bw.write("Named Entity: " + gCurrentNE.toUpperCase() + "\n");
    if(finalNamedEntities.length() > 0)
    {
        bw.write("Named Entity(" + gCurrentNE.toUpperCase()+ ") Values: " + finalNamedEntities.substring(0, finalNamedEntities.length()-2)+"\n");
    }

    bw.write("Summary:\n");
    bw.write(finalSummary);
    bw.close();
    
}//end of function
   
   
   public void run() throws IOException
   {
       try{
           
           File oClusters = new File(gOrigClustersDir);

           if(!oClusters.exists())
           {
               System.out.println("Nothing to do: Clusters direcoty doesn't exists");
               return;
           }
           
           File [] listOfFiles = oClusters.listFiles();
           if(listOfFiles == null || listOfFiles.length <= 0)
           {
               System.out.println("Nothing to do: Clusters direcoty empty");
               return;
           }
               
           for (int i = 0; i < listOfFiles.length; i++)
           {
               if(listOfFiles[i].isFile())
               {
                   gCurrentFileName = listOfFiles[i].getName();
                   gCurrentClusterName = gCurrentFileName.replaceAll("_", " ");
                   gOriginalFileName = listOfFiles[i].getAbsolutePath();
                   gCurrentFilePath = gNEClustersDir + PATH_SEPERATOR + gCurrentFileName;

                   File tempFile = new File(gNEClustersDir + PATH_SEPERATOR + gCurrentFileName);
                   if(!tempFile.exists())
                   {
                       System.out.println("Named entity classification for file " + gOriginalFileName + "doesn't exist, Not generating summary for this file.");
                       continue;
                   }
                   
                   // Check for NE out file, if exists delete it
                   tempFile = new File(gNEOutDir + PATH_SEPERATOR + gCurrentFileName);
                   if(tempFile.exists())
                       tempFile.delete();
                   
                // Check for summary out file, if exists delete it
                   tempFile = new File(gSummaryOutDir + PATH_SEPERATOR + gCurrentFileName);
                   if(tempFile.exists())
                       tempFile.delete();
                   
                   processClusterAndGenerateSummary(gCurrentFilePath);
               }
           }
       }
       catch (Exception e)
       {
           System.out.println("Exception : " + e.getMessage());
       }
   }
   
   public void processClusterAndGenerateSummary(String clusterFile) throws Exception
   {
       HashMap<String, String> lEntityMap = new HashMap<String, String>();
       
       try
       {
           BufferedReader br = null;
           String sCurrentLine;
           String tokenStr, neStr="", neStrToken, nEntity="";
           boolean isNeUnderParsing=false;
           int lineNo=0;
    
           br = new BufferedReader(new FileReader(clusterFile));
           while ((sCurrentLine = br.readLine()) != null)
           {
               lineNo++;
               StringTokenizer tokenizer = new StringTokenizer(sCurrentLine, " ");
               while (tokenizer.hasMoreTokens())
               {
                   tokenStr = tokenizer.nextToken();
                   int ind = tokenStr.lastIndexOf('/');
                   neStrToken = tokenStr.substring(ind+1);
                   if (neStrToken.startsWith("I-"))
                   {
                       neStr += " " + tokenStr.substring(0, ind).replaceAll(","," ");
                   }
                    else
                    {
                       // Either this is a new token or token of other type
                        if (isNeUnderParsing && !neStrToken.startsWith("I-"))
                        {
                            if(lEntityMap.containsKey(nEntity))
                            {
                                String str = lEntityMap.get(nEntity);
                                if(str.indexOf(", "+neStr +",") < 0 && 
                                        !str.startsWith(neStr+",") &&
                                        !str.equals(neStr) &&
                                        !str.endsWith(", "+neStr))
                                    lEntityMap.put(nEntity, lEntityMap.get(nEntity)+", " + neStr);
                            }
                            else
                                lEntityMap.put(nEntity, neStr);
                            
                            if (tsTypeMap.containsKey(neStr))
                            {
                                if ((tsTfIdfMap.get(neStr)+1) > max)
                                    max2=max;
                                    max=(tsTfIdfMap.get(neStr)+1);
                                // If any such string already encountered
                                tsTfIdfMap.put(neStr, tsTfIdfMap.get(neStr)+1);
                                entityTweetNum.get(neStr).add(lineNo);
                            }
                            else
                            {
                                tsTypeMap.put(neStr, nEntity);
                                tsTfIdfMap.put(neStr, 1);
                                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                                arrayList.add(lineNo);
                                String temp = neStr.replaceAll(",", " ");
                                entityTweetNum.put(temp, arrayList);
                            }
                            
                            isNeUnderParsing = false;
                        }
                        // TODO: insert token into treemap
                        if (neStrToken == "O")
                            continue;
                        
                        if (neStrToken.startsWith("B-"))
                        {
                            neStr = tokenStr.substring(0, ind).replaceAll(",", " ");
                            nEntity = neStrToken.substring(2);
                            isNeUnderParsing = true;
                        }
                            
                    }
                }
            }
                
           if(!lEntityMap.isEmpty())
             Util.dumpInFile(lEntityMap, gNEOutDir+PATH_SEPERATOR+gCurrentFileName);
           
            /*System.out.println("tsTypeMap");
            for(Map.Entry<String,String> entry : tsTypeMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                System.out.println(key + " => " + value);
            }*/
                
            /*System.out.println("tsTfIdfMap");
            for(Map.Entry<String,Integer> entry : tsTfIdfMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                System.out.println(key + " => " + value);
            }*/
                
            br.close();
        }//try ends
        catch(Exception e){
            e.printStackTrace();
        }
        
       
       // Write entities to file
       
       sort_all_types_tfidf();
       
       for(Map.Entry<String,String> entry : lEntityMap.entrySet()) {
           gCurrentNE = entry.getKey().toLowerCase();
           ne_max.clear();
       
           get_final_tweets();
       }

       /*System.out.println("Entity Tweet Num: ");
       for(String tn: entityTweetNum.keySet())
        {    
            System.out.print(tn+ " ");
            for(Integer t: entityTweetNum.get(tn))
            {
               
               System.out.print(t+",");
                
            }//end for inner
            System.out.print("\n" );
        }*/
       
        clearAll();
        max = max2 = 1;
        
        System.out.println();
        
        /* System.out.println("tsTypeMapSorted in sort");
         for(Map.Entry<String,String> entry1 : tsTypeMapSorted.entrySet()) {
             String key = entry1.getKey();
             String value = entry1.getValue();

             System.out.println(key + " => " + value);
         }*/
     }
}
