package utility;

import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by VHACONMICHEG on 6/29/2017.
 */
public class SVMModel {


    static final String svm_type_table[] =
            {
                    "c_svc","nu_svc","one_class","epsilon_svr","nu_svr",
            };

    static final String kernel_type_table[]=
            {
                    "linear","polynomial","rbf","sigmoid","precomputed"
            };

    private static double atof(String s)
    {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s)
    {
        return Integer.parseInt(s);
    }



    private static boolean read_model_header(BufferedReader fp, svm_model model)
    {
        svm_parameter param = new svm_parameter();
        model.param = param;
        // parameters for training only won't be assigned, but arrays are assigned as NULL for safety
        param.nr_weight = 0;
        param.weight_label = null;
        param.weight = null;

        try
        {
            while(true)
            {
                String cmd = fp.readLine();
                String arg = cmd.substring(cmd.indexOf(' ')+1);

                if(cmd.startsWith("svm_type"))
                {
                    int i;
                    for(i=0;i<svm_type_table.length;i++)
                    {
                        if(arg.indexOf(svm_type_table[i])!=-1)
                        {
                            param.svm_type=i;
                            break;
                        }
                    }
                    if(i == svm_type_table.length)
                    {
                        System.err.print("unknown svm type.\n");
                        return false;
                    }
                }
                else if(cmd.startsWith("kernel_type"))
                {
                    int i;
                    for(i=0;i<kernel_type_table.length;i++)
                    {
                        if(arg.indexOf(kernel_type_table[i])!=-1)
                        {
                            param.kernel_type=i;
                            break;
                        }
                    }
                    if(i == kernel_type_table.length)
                    {
                        System.err.print("unknown kernel function.\n");
                        return false;
                    }
                }
                else if(cmd.startsWith("degree"))
                    param.degree = atoi(arg);
                else if(cmd.startsWith("gamma"))
                    param.gamma = atof(arg);
                else if(cmd.startsWith("coef0"))
                    param.coef0 = atof(arg);
                else if(cmd.startsWith("nr_class"))
                    model.nr_class = atoi(arg);
                else if(cmd.startsWith("total_sv"))
                    model.l = atoi(arg);
                else if(cmd.startsWith("rho"))
                {
                    int n = model.nr_class * (model.nr_class-1)/2;
                    model.rho = new double[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.rho[i] = atof(st.nextToken());
                }
                else if(cmd.startsWith("label"))
                {
                    int n = model.nr_class;
                    model.label = new int[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.label[i] = atoi(st.nextToken());
                }
                else if(cmd.startsWith("probA"))
                {
                    int n = model.nr_class*(model.nr_class-1)/2;
                    model.probA = new double[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.probA[i] = atof(st.nextToken());
                }
                else if(cmd.startsWith("probB"))
                {
                    int n = model.nr_class*(model.nr_class-1)/2;
                    model.probB = new double[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.probB[i] = atof(st.nextToken());
                }
                else if(cmd.startsWith("nr_sv"))
                {
                    int n = model.nr_class;
                    model.nSV = new int[n];
                    StringTokenizer st = new StringTokenizer(arg);
                    for(int i=0;i<n;i++)
                        model.nSV[i] = atoi(st.nextToken());
                }
                else if(cmd.startsWith("SV"))
                {
                    break;
                }
                else
                {
                    System.err.print("unknown text in model file: ["+cmd+"]\n");
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }

    public  svm_model svm_load_model(BufferedReader fp) throws IOException
    {
        // read parameters

        svm_model model = new svm_model();
        model.rho = null;
        model.probA = null;
        model.probB = null;
        model.label = null;
        model.nSV = null;

        if (read_model_header(fp, model) == false) {
            System.err.print("ERROR: failed to read model\n");
            return null;
        }

        // read sv_coef and SV

        int m = model.nr_class - 1;
        int l = model.l;
        model.sv_coef = new double[m][l];
        model.SV = new svm_node[l][];

        for (int i = 0; i < l; i++) {
            String line = fp.readLine();
            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

            for (int k = 0; k < m; k++)
                model.sv_coef[k][i] = atof(st.nextToken());
            int n = st.countTokens() / 2;
            model.SV[i] = new svm_node[n];
            for (int j = 0; j < n; j++) {
                model.SV[i][j] = new svm_node();
                model.SV[i][j].index = atoi(st.nextToken());
                model.SV[i][j].value = atof(st.nextToken());
            }
        }

        fp.close();
        return model;
    }



}
