import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;
public class CustomPartitioner implements Partitioner<Text, Text> {
     private int num = 0;
     public void configure(JobConf job) {}
     public int getPartition(Text key, Text value, int numOfReducer) {
         return (num++)% numOfReducer;
     }
}
