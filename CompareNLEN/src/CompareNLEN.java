import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

public class CompareNLEN extends Configured implements Tool {

  private static final Logger LOG = Logger.getLogger(CompareNLEN.class);

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new CompareNLEN(), args);
    System.exit(res);
  }

  public int run(String[] args) throws Exception {
    Job job = Job.getInstance(getConf(), "compare NL EN");
    job.setJarByClass(this.getClass());
    // Use TextInputFormat, the default unless job.setInputFormatClass is used
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.setMapperClass(Mapp.class);
    job.setReducerClass(Reduce.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    return job.waitForCompletion(true) ? 0 : 1;
  }

  public static class Mapp extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1); 
    private static final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");

    public void map(LongWritable offset, Text lineText, Context context)
        throws IOException, InterruptedException {
      String line = lineText.toString();
      Text currentWord = new Text();
      
      // Per woord worden de letter combinaties en frequenties opgeslagen in een HashMap
      HashMap<String, Integer> combinations = new HashMap<String, Integer>();
      for (String word : WORD_BOUNDARY.split(line)) {
        if (word.isEmpty()) {
            continue;
        }
        	String wordLowerCase = word.toLowerCase();
            for (int i = 0; i < wordLowerCase.length()-1; i++) {
                String twoLetters;
                StringBuilder sb = new StringBuilder();
                sb.append(wordLowerCase.charAt(i));
                sb.append(wordLowerCase.charAt(i+1));
                twoLetters = sb.toString();
                
                if (combinations.containsKey(twoLetters)) {
                    combinations.put(twoLetters, combinations.get(twoLetters) + 1);
                } else {
                    combinations.put(twoLetters, 1);
                }
            }
        }
      
      Map<String, Integer> sortedCombinations = new TreeMap<>(combinations);
      HashMap<String, String> engels = fileReader("/engels/engels.txt");
      HashMap<String, String> nederlands = fileReader("/nederlands/nederlands.txt");
      
      // Optelling van de lettercombinatie frequentie vermenigvuldigt met de kans hoe vaak het voorkomt in het Engels
      double sumEngels = 0;
      for (Map.Entry<String, Integer> entryLettersCombination : sortedCombinations.entrySet()) {
    	  
          for (Entry<String, String> entryEngels : engels.entrySet()) {
        	  
        	  if (entryLettersCombination.getKey().equals(entryEngels.getKey())) {
        		  sumEngels += Double.parseDouble(entryEngels.getValue()) * entryLettersCombination.getValue();
        	  }
        	  
          }
      }
      
      // Optelling van de lettercombinatie frequentie vermenigvuldigt met de kans hoe vaak het voorkomt in het Nederlands
      double sumNederlands = 0;
      for (Map.Entry<String, Integer> entryLettersCombination : sortedCombinations.entrySet()) {
    	  
          for (Entry<String, String> entryNederlanders : nederlands.entrySet()) {
        	  
        	  if (entryLettersCombination.getKey().equals(entryNederlanders.getKey())) {
        		  sumNederlands += Double.parseDouble(entryNederlanders.getValue()) * entryLettersCombination.getValue();
        	  }
        	  
          }
      }
      
      if ( sumNederlands < sumEngels) {
		  currentWord = new Text("Engels");
    	  context.write(currentWord, one);
      } else {
		  currentWord = new Text("Nederlands");
    	  context.write(currentWord, one);
      }
    }
    
    // File reader voor het verkrijgen van de Engelse en Nederlandse matrix in een HashMap.
    public HashMap<String, String> fileReader(String filepath) throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        
        String uri = "hdfs://quickstart.cloudera:8020/" + filepath;
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(URI.create(uri), conf);
		InputStream in = null;
		in = fs.open(new Path(uri));
        
        
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine()) != null)
        {
            String[] parts = line.split(":", 2);
            if (parts.length >= 2)
            {
                String key = parts[0];
                String value = parts[1];
                map.put(key, value);
            }
        }

        reader.close();
        return map;
    }
  }

  public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    public void reduce(Text word, Iterable<IntWritable> counts, Context context)
        throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable count : counts) {
        sum += count.get();
      }
      context.write(word, new IntWritable(sum));
    }
  }
}
