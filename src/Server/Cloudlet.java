package Server;

import Distribution.Distribution;
import Request.ArrivalRequest;
import Request.CompletedRequest;
import Util.Configuration;
import Util.RequestQueue;

public class Cloudlet extends Server {

    private RequestQueue requestQueue = RequestQueue.getInstance();
    private Distribution distribution = Distribution.getInstance();
    private static double mu1 = Configuration.MU1CLET;
    private static double mu2 = Configuration.MU2CLET;
    private static double p_hyperexpo = Configuration.P_HYPEREXPO;
    private static boolean hyperexpo = Configuration.HYPEREXPO;
    private static Cloudlet cloudletInstance = null;
    public int allClass2JobsArrivedToCLoudlet = 0;

    private Cloudlet() {}

    public static Cloudlet getInstance(){
        if(cloudletInstance == null){
            cloudletInstance = new Cloudlet();
        }
        return cloudletInstance;
    }

    public void handleRequest(ArrivalRequest r){
        CompletedRequest cr;
        if(r.getJobType()==1){
            double serviceTimeMu1;
            // it handle class1 request
            // if the server is hyperexpo then we calculate the new service time
            if(hyperexpo){
                serviceTimeMu1 = serviceTimeMu1Hyper();
            }else{
                // otherwise we use the exponential value
                distribution.selectStream(5);
                serviceTimeMu1 = distribution.exponential(1.0/mu1);
            }
            r.getJob().setServiceTime(serviceTimeMu1);
            cr = new CompletedRequest(r.getJob());
            this.nJobsClass1+=1;
        }else{
            double serviceTimeMu2;
            allClass2JobsArrivedToCLoudlet++;
            // it handle class2 request
            // if the server is hyperexpo then we calculate the new service time
            if(hyperexpo){
                serviceTimeMu2 = serviceTimeMu2Hyper();
            }else{
                // otherwise we use the exponential value
                distribution.selectStream(6);
                serviceTimeMu2 = distribution.exponential(1.0/mu2);
            }
            r.getJob().setServiceTime(serviceTimeMu2);
            cr = new CompletedRequest(r.getJob());
            r.getJob().setCompletedRequest(cr);
            this.nJobsClass2+=1;
        }
        cr.setServer(this);
        requestQueue.add(cr);
    }

    // calculating the new service time with hyperexponential distribution
    private double serviceTimeMu1Hyper() {
        double serviceTimeMu1;
        double uniform;
        distribution.selectStream(2);
        uniform = distribution.uniform(0.0, 1.0);
        distribution.selectStream(5);
        if(uniform < p_hyperexpo){
            serviceTimeMu1 = distribution.exponential(1.0/(2*p_hyperexpo*mu1));
        }else{
            serviceTimeMu1 = distribution.exponential(1.0/(2*(1-p_hyperexpo)*mu1));
        }
        return serviceTimeMu1;
    }

    private double serviceTimeMu2Hyper() {
        double serviceTimeMu2;
        double uniform;
        distribution.selectStream(7);
        uniform = distribution.uniform(0.0, 1.0);
        distribution.selectStream(6);
        if(uniform < p_hyperexpo){
            serviceTimeMu2 = distribution.exponential(1.0/(2*p_hyperexpo*mu2));
        }else{
            serviceTimeMu2 = distribution.exponential(1.0/(2*(1-p_hyperexpo)*mu2));
        }
        return serviceTimeMu2;
    }
    ///////////////////////////////////
}
