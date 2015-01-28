import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;


public class Main {
	public static void main(String args[]) throws IOException{
		 
		  Main d= new Main();
		  d.run();

	}
	public void run() throws IOException{
		 LinkedList<Record> link=new LinkedList<Record>();
		  LinkedList<Record> temp=new LinkedList<Record>();
		 
		  temp=SetRecord();
		  link=StratifiedRecord(temp);

		  int TenF_set[]= TenF_set(link);
		  LinkedList<Record> TenFoldSet=new LinkedList<Record>();
		  LinkedList<Record> TrainDataSet=new LinkedList<Record>();
		  
		  double sum_accurcy=0.0;
		  for(int i=1;i<11;i++){
		  TenFoldSet=TenFold(TenF_set, i, link);
		  TrainDataSet=trainDataSet(TenF_set, i, link);
		  Attribute_Mean M=new Attribute_Mean(TenFoldSet.get(0).attributes.length);
		  M=SetMean(TrainDataSet);
		  Attribute_StandDeviation SD=new Attribute_StandDeviation(TenFoldSet.get(0).attributes.length);
		  SD=SetSD(M, TrainDataSet);
		  calculate_CV(TenFoldSet,M,SD);
		  int count=0;
		  for(int j=0;j<TenFoldSet.size();j++){
			  if(TenFoldSet.get(j).Cv.equals(TenFoldSet.get(j).Calculate_Cv)){
				  count++;
			  }
			
		  }
		  System.out.println("Accurcy for TenFold set "+i+ ": "+(double) count/TenFoldSet.size() *100 +"%");
		  sum_accurcy += (double) count/TenFoldSet.size() *100 ;
		  
		  }
		  System.out.println( "Average accurcy is :"+ sum_accurcy /10 +"%" );
	}
	public void calculate_CV(LinkedList<Record> TenFoldSet,Attribute_Mean M,Attribute_StandDeviation SD){
		int num_0=0;
		int num_1=0;
		for(int i=0; i<TenFoldSet.size();i++){
			if(TenFoldSet.get(i).Cv.equals("class0")){
				num_0++;
			}
			if(TenFoldSet.get(i).Cv.equals("class1")){
				num_1++;
			}
		}
		double p_class0=(double)num_0/TenFoldSet.size();
		double p_class1=(double)num_1/TenFoldSet.size();
		double f_class0[] = new double[TenFoldSet.get(0).attributes.length];
		double f_class1[] = new double[TenFoldSet.get(0).attributes.length];
		for(int i=0;i<TenFoldSet.size();i++){
			double P_E_class0=p_class0;	
			double P_E_class1=p_class1;
			
			for(int j=0;j<f_class0.length;j++){
			f_class0[j]=0;
			f_class0[j]=1/( SD.class0_attribute_sd[j]*Math.sqrt(2*Math.PI) )  * Math.exp(- Math.pow(TenFoldSet.get(i).attributes[j]-M.class0_attributes_mean[j], 2)/(2*Math.pow(SD.class0_attribute_sd[j],2)));
			P_E_class0= P_E_class0*f_class0[j];
			}
			
			for(int j=0;j<f_class1.length;j++){
			f_class1[j]=0;
			f_class1[j]=1/( SD.class1_attribute_sd[j]*Math.sqrt(2*Math.PI) )  * Math.exp(- Math.pow(TenFoldSet.get(i).attributes[j]-M.class1_attributes_mean[j], 2)/(2*Math.pow(SD.class1_attribute_sd[j],2)));
			P_E_class1= P_E_class1*f_class1[j];
			}
		
			if(P_E_class0 > P_E_class1 ){
				TenFoldSet.get(i).Calculate_Cv="class0";
			}else if(P_E_class0 < P_E_class1){
				TenFoldSet.get(i).Calculate_Cv="class1";
			}else if(P_E_class0 == P_E_class1){
				 Random generator = new Random();
				 int roll = generator.nextInt(100) ;
				 if(roll<50){
					 TenFoldSet.get(i).Calculate_Cv="class0";
				 }else{
					 TenFoldSet.get(i).Calculate_Cv="class1";
				 }
			}
		}
		
		
		
	}
	public Attribute_StandDeviation SetSD(Attribute_Mean M,LinkedList<Record> link){
		Attribute_StandDeviation SD=new Attribute_StandDeviation(link.get(0).attributes.length);
		double sum[]=new double[link.get(0).attributes.length];
		int count=0;
		for(int s=0;s<sum.length;s++){
			sum[s]=0;
			for(int i=0; i<link.size();i++){
				if(link.get(i).Cv.equals("class0")){
					sum[s]+=Math.pow(link.get(i).attributes[s]-M.class0_attributes_mean[s], 2);
					count++;
				}
			}
		}
		
		
		for(int i=0;i<SD.class0_attribute_sd.length;i++){
			SD.class0_attribute_sd[i]=Math.sqrt(sum[i]/( (count/sum.length)-1));
		}
		
		count=0;
		for(int s=0;s<sum.length;s++){
			sum[s]=0;
			for(int i=0; i<link.size();i++){
				if(link.get(i).Cv.equals("class1")){
					sum[s]+=Math.pow(link.get(i).attributes[s]-M.class1_attributes_mean[s], 2);
					count++;
				}
			}
		}
		for(int i=0;i<SD.class1_attribute_sd.length;i++){
			SD.class1_attribute_sd[i]=Math.sqrt(sum[i]/( (count/sum.length)-1));
		}
		
		return SD;
	}
	public Attribute_Mean SetMean(LinkedList<Record> link){
		Attribute_Mean M=new Attribute_Mean(link.get(0).attributes.length);
		double sum[]=new double[link.get(0).attributes.length];
		int count=0;
		for(int s=0;s<sum.length;s++){
				sum[s]=0;
			for(int i=0; i<link.size();i++){
				if(link.get(i).Cv.equals("class0")){
				sum[s] += link.get(i).attributes[s];
				count++;
				}
			}
		}
		
		for(int i=0; i<M.class0_attributes_mean.length;i++){
			M.class0_attributes_mean[i]=sum[i]/(count/sum.length);
		}
		count=0;
		for(int s=0;s<sum.length;s++){
			sum[s]=0;
			for(int i=0; i<link.size();i++){
				if(link.get(i).Cv.equals("class1")){
				sum[s] += link.get(i).attributes[s];
				count++;
				}
			}
		}
		
		for(int i=0; i<M.class1_attributes_mean.length;i++){
			M.class1_attributes_mean[i]=sum[i]/(count/sum.length);
		}
		
			
		return M;
		
	}
	public LinkedList<Record> trainDataSet(int TenF_set[],int num_set,LinkedList<Record> link){
		LinkedList<Record> result=new LinkedList<Record>();
		for(int i=1; i<11; i++){
			if( i != num_set){
				LinkedList<Record> temp=new LinkedList<Record>();
				temp=TenFold(TenF_set,i,link);
				result.addAll(temp);
				
			}
		}
		
	 //	System.out.println(result.size());
		return result;
	}
	public LinkedList<Record> TenFold(int TenF_set[],int num_set,LinkedList<Record> link){
		LinkedList<Record> result=new LinkedList<Record>();
				
			int total=0;
			int start=0;
			for(int i=0;i<num_set;i++){
			 total += TenF_set[i];
			}
			for(int i=0;i<num_set-1;i++){
			 start += TenF_set[i];	
			}
			for(int i=start; i<total;i++){
				result.add(link.get(i));
			}
		
			
		return result;
	}
	public int [] TenF_set(LinkedList<Record> link){
		
		 int TenF_set[]={0,0,0,0,0,0,0,0,0,0};
		 int s=link.size()%10;
		 int m=0;
		 while(s!=0){
			 
			 TenF_set[m]=(int)(link.size()/10)+1;
			 m++;
			 s--;
		 }
		 for(int i=0;i<TenF_set.length;i++){
			 if(TenF_set[i]==0) TenF_set[i]=(int)link.size()/10;
		 }
		
		
		return TenF_set;
		
	}
	public LinkedList<Record> StratifiedRecord( LinkedList<Record> link){
		int num_0=0;
		int num_1=0;
		int totle_record=link.size();
		for(int i=0; i<link.size();i++){
			if(link.get(i).Cv.equals("class0")){
				num_0++;
			}
			if(link.get(i).Cv.equals("class1")){
				num_1++;
			}
		}
	
		int counts=1;
		int check=1;
		LinkedList<Record> result=new LinkedList<Record>();
		
		if((double)num_0/num_1>1){
			int m=(int)	Math.round((double)num_0 / num_1);
			
			for(;;){
				if(link.size()==0)break;
				if(check==0)break;
				if(counts%(m+1)!=0){
					for(int i=0; i<link.size(); i++){
						if(link.get(i).Cv.equals("class0")){
						result.add(link.get(i));
						link.remove(i);
						break;
						}
						if(i==link.size()-1 && link.get(i).Cv.equals("class1")){
						check=0;
							
						}
					
					
					}
					counts++;
				}else{
					for(int i=0; i<link.size();i++){
			  			  
						  if(link.get(i).Cv.equals("class1")){
							result.add(link.get(i));
							link.remove(i);
							
						    break;
						  }
					}
					counts++;
				}
			}
			if(link.size()!=0) {
				int position= (int)Math.round((double)totle_record/10);
				int s=0;
			
				for(;;){
					if(link.size()==0)break;
					for(int i=0; i<link.size();i++){
						result.add(position*s,link.get(i));
						link.remove(i);
						break;
					}
					s++;
					if(s>9)s=0;
				}
			}
			
		
		}
		if((double)num_1/num_0>1){
			int m=(int)	Math.round((double)num_1 / num_0);
			
			for(;;){
				if(link.size()==0)break;
				if(check==0)break;
				if(counts%(m+1)!=0){
					for(int i=0; i<link.size(); i++){
						if(link.get(i).Cv.equals("class1")){
						result.add(link.get(i));
						link.remove(i);
						break;
						}
						if(i==link.size()-1 && link.get(i).Cv.equals("class0")){
						check=0;
							
						}
					
					
					}
					counts++;
				}else{
					for(int i=0; i<link.size();i++){
			  			  
						  if(link.get(i).Cv.equals("class0")){
							result.add(link.get(i));
							link.remove(i);
							
						    break;
						  }
					}
					counts++;
				}
			}
			if(link.size()!=0) {
				int position= (int)Math.round((double)totle_record/10);
				int s=0;
				System.out.println(position);
				for(;;){
					if(link.size()==0)break;
					for(int i=0; i<link.size();i++){
						result.add(position*s,link.get(i));
						link.remove(i);
						break;
					}
					s++;
					if(s>9)s=0;
				}
			}
			
		
		}
		
		return result;
	}
	
	
	public LinkedList<Record> SetRecord() throws IOException{
		LinkedList<Record> link=new LinkedList<Record>();
		
		FileInputStream fstream = new FileInputStream("pima1.csv");
		
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String data="";
		String strLine="";
		//Read File Line By Line
		while ( (strLine=br.readLine() )!= null)   {
		// Print the content on the console
			data=data+strLine+"\n";
				
		}
		
	//	System.out.println (data);
		String [] result;
		String delimiter ="\\r?\\n";
		result=data.split(delimiter);
		
		for(int i=0; i< result.length;i++){
			if(i>=1){
				String [] Re;
				String delimiters=",";
				Re=result[i].split(delimiters);
				Record r=new Record(Re.length-1);
				
				for(int j=0; j<Re.length;j++){
					if(j==Re.length-1){
						r.Cv=Re[j];
					}else{
					r.attributes[j]=Double.parseDouble(Re[j]);
					}
				}
				link.add(r);
			}
		}
		
		in.close();
		return link;
	}
}
