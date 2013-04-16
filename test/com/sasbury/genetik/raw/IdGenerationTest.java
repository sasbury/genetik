package test.com.sasbury.genetik.raw;

import com.sasbury.genetik.population.*;

public class IdGenerationTest
{
    public static void main(String args[])
    {
        for(int i=0,max=100;i<max;i++)
        {
            System.out.println(Individual.generateId());
            try{Thread.sleep(20);}catch(Exception exp){}
        }
    }
}
