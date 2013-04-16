package com.sasbury.genetik.driver.cluster.pbs;

import java.io.*;
import java.util.*;

import com.sasbury.genetik.*;
import com.sasbury.genetik.driver.*;
import com.sasbury.util.*;

public class PBSBigJobGenetik implements GenetikConstants
{
    /**
     * The estimated time for a scoring job
     */
    public static final String EST_TIME="estimated_time";

    /**
     * The class path for the cluster jobs, generally a colon-separated list of jars,
     * should include the genetik jar as well as any for custom fitness functions.
     */
    public static final String CLASS_PATH="class_path";
    
    /**
     * A property containing the email address to notify when either max jobs are run, or 
     * the experiment finishes.
     */
    public static final String EMAIL="email";
    
    /**
     * The id for the cluster that will appear on job ids and can be stripped off.
     */
    public static final String CLUSTER_ID="cluster_id";
    
    public static final String PROCS="procs";

    public static final String JOB_QUEUE="job_queue";
    public static final String JOB_QOS="job_qos";
    
    public static final String ACCOUNT="account";
    
    public static void main(String args[])
    {
        try
        {
            if(args.length<1) usage();
            
            File propsFile = new File(args[0]);
            File runDir = propsFile.getParentFile();
            String runName = runDir.getName();
            
            if(!propsFile.exists())
            {
                System.out.println("Missing Properties File: "+propsFile.getAbsolutePath());
                usage();
            }
            
            propsFile = new File(propsFile.getAbsolutePath());
            
            Properties props = Run.createDefaultProperties();
            BufferedInputStream propsIn = new BufferedInputStream(new FileInputStream(propsFile));
            props.load(propsIn);
            propsIn.close();
            
            PBSValidator validator = new PBSValidator();
            String[] issues = validator.validate(props);
            
            if(issues.length>0)
            {
                for(String s : issues)
                {
                    System.out.println(s);
                }
                
                System.exit(1);
            }
            
            //build script file
            String acct = props.getProperty(ACCOUNT);
            HashMap<String,String> templateValues = new HashMap<String,String>();
            
            templateValues.put(PBSJobInfo.RUN_NAME,runName);

            String email = props.getProperty(EMAIL);
            
            if(email!=null)
            {
                templateValues.put(PBSJobInfo.MAIL, "PBS -M "+email);
                templateValues.put(PBSJobInfo.MAIL_FLAG, "PBS -m ae");//add flag to get mail
            }
            else
            {
                templateValues.put(PBSJobInfo.MAIL_FLAG, "PBS -m n");//default to no mail
            }

            templateValues.put(PBSJobInfo.RUN_DIR,runDir.getAbsolutePath());
            templateValues.put(PBSJobInfo.PROCS,props.getProperty(PROCS));

            String queue = props.getProperty(JOB_QUEUE);
            if(queue==null) queue = "route";
            templateValues.put(PBSJobInfo.QUEUE, queue);
            
            String qos = props.getProperty(JOB_QOS);
            if(qos==null) qos = acct;
            templateValues.put(PBSJobInfo.QOS,qos);
            
            if(acct!=null) templateValues.put(PBSJobInfo.ACCOUNT,"PBS -A "+acct);

            templateValues.put(PBSJobInfo.CLASS_PATH,props.getProperty(CLASS_PATH));
            templateValues.put(PBSJobInfo.TIME,props.getProperty(EST_TIME));
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(PBSJobInfo.class.getResourceAsStream("/com/sasbury/genetik/driver/cluster/pbs/genetik_pbs_big.sh")));
            StringBuilder builder = new StringBuilder();
            String line = null;
            
            while((line = reader.readLine())!=null)
            {
                builder.append(line);
                builder.append("\n");
            }
            
            reader.close();
            
            File templateFile = new File(runDir,"pbs_big_job.sh");
            String template = builder.toString();
            String text = Templating.templatize(template, templateValues);
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(templateFile));
            writer.write(text);
            writer.close();
            
            //call qsub
            
            File scriptFile = new File(runDir,"pbs_big_job.sh");
            String outFile = (new File(runDir,"pbs.out")).getAbsolutePath();
            String errFile = (new File(runDir,"pbs.err")).getAbsolutePath();

            String cmdLine = "qsub -e "+errFile+" -o "+outFile+" "+scriptFile.getAbsolutePath();
        
            Process p = Runtime.getRuntime().exec(cmdLine);
            StringBuilder buff = new StringBuilder();
            String s;
            String jobId;
            
            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((s = stdOutput.readLine()) != null)
            {
                buff.append(s);
            }
            
            jobId = buff.toString();
            
            System.out.println("Scheduled "+jobId);
        }
        catch(Exception exp)
        {
            exp.printStackTrace(System.out);
            System.out.println();
            usage();
        }
    }
    
    public static void usage()
    {
        System.out.println("PBSBigJobGenetik takes a single command line argument: the properties file to run.");
        System.out.println("It is assumed that the properties file is in the folder that should be used to store run files.");
        System.exit(0);
    }
}
