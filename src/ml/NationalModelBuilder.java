package ml;

import utility.DBConnection;
import dao.SVMParameterDao;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import org.apache.commons.io.FileUtils;
import utility.SqlQueryResource;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

public class NationalModelBuilder {
    // to train/create the model and save to database
    // Database table svm.TermMatrix must
    // have an updated term_matrix for the term_matrix_type
    // svm_save_model copied from source and modified to write to database and file


    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();

    public svm_model svmTrain(String TrainingSet) {
        //optimized
        SVMParameterDao svp = new SVMParameterDao();
        svm_parameter svmParameter = svp.getSVMParameterDao();
        NationalTrainingSetBuilder nationalTrainingSetBuilder = new NationalTrainingSetBuilder();
       // svm_model model = svm.svm_train(svmProblem, svmParameter);
        svm_model model = svm.svm_train(nationalTrainingSetBuilder.getProblem(TrainingSet), svmParameter);
        //    double[] target = new double[svmProblem.l];
        //    svm.svm_cross_validation(svmProblem, svmParameter, 10, target );
        return model;
    }

     final String svm_type_table[] =
            {
                    "c_svc","nu_svc","one_class","epsilon_svr","nu_svr",
            };

     final String kernel_type_table[]=
            {
                    "linear","polynomial","rbf","sigmoid","precomputed"
            };

    public void svm_save_model(svm_model model,String modelOrgan, String nameOfsvmModel) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      //  FileOutputStream fileOutputStream = new FileOutputStream(nameOfsvmModel);
     //   DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        svm_parameter param = model.param;

        dataOutputStream.writeBytes("svm_type "+svm_type_table[param.svm_type]+"\n");
        dataOutputStream.writeBytes("kernel_type "+kernel_type_table[param.kernel_type]+"\n");

        if(param.kernel_type == svm_parameter.POLY)
            dataOutputStream.writeBytes("degree "+param.degree+"\n");

        if(param.kernel_type == svm_parameter.POLY ||
                param.kernel_type == svm_parameter.RBF ||
                param.kernel_type == svm_parameter.SIGMOID)
            dataOutputStream.writeBytes("gamma "+param.gamma+"\n");

        if(param.kernel_type == svm_parameter.POLY ||
                param.kernel_type == svm_parameter.SIGMOID)
            dataOutputStream.writeBytes("coef0 "+param.coef0+"\n");

        int nr_class = model.nr_class;
        int l = model.l;
        dataOutputStream.writeBytes("nr_class "+nr_class+"\n");
        dataOutputStream.writeBytes("total_sv "+l+"\n");

        {
            dataOutputStream.writeBytes("rho");
            for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                dataOutputStream.writeBytes(" "+model.rho[i]);
            dataOutputStream.writeBytes("\n");
        }

        if(model.label != null)
        {
            dataOutputStream.writeBytes("label");
            for(int i=0;i<nr_class;i++)
                dataOutputStream.writeBytes(" "+model.label[i]);
            dataOutputStream.writeBytes("\n");
        }

        if(model.probA != null) // regression has probA only
        {
            dataOutputStream.writeBytes("probA");
            for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                dataOutputStream.writeBytes(" "+model.probA[i]);
            dataOutputStream.writeBytes("\n");
        }
        if(model.probB != null)
        {
            dataOutputStream.writeBytes("probB");
            for(int i=0;i<nr_class*(nr_class-1)/2;i++)
                dataOutputStream.writeBytes(" "+model.probB[i]);
            dataOutputStream.writeBytes("\n");
        }

        if(model.nSV != null)
        {
            dataOutputStream.writeBytes("nr_sv");
            for(int i=0;i<nr_class;i++)
                dataOutputStream.writeBytes(" "+model.nSV[i]);
            dataOutputStream.writeBytes("\n");
        }

        dataOutputStream.writeBytes("SV\n");
        double[][] sv_coef = model.sv_coef;
        svm_node[][] SV = model.SV;

        for(int i=0;i<l;i++)
        {
            for(int j=0;j<nr_class-1;j++)
                dataOutputStream.writeBytes(sv_coef[j][i]+" ");

            svm_node[] p = SV[i];
            if(param.kernel_type == svm_parameter.PRECOMPUTED)
                dataOutputStream.writeBytes("0:"+(int)(p[0].value));
            else
                for(int j=0;j<p.length;j++)
                    dataOutputStream.writeBytes(p[j].index+":"+p[j].value+" ");
            dataOutputStream.writeBytes("\n");
        }

        //String modelAsString = dataOutputStream.toString();
       // System.out.println(modelAsString);

        String modelAsString = byteArrayOutputStream.toString("UTF-8");
        FileUtils.writeStringToFile(new File(nameOfsvmModel), modelAsString,"UTF-8");
        dataOutputStream.close();
        try {

            String insertModel = sqlQueryResource.getSqlQuery("national-update-insert-model");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(insertModel);
            ps.setString(1,modelOrgan );
            ps.setString(2, modelAsString);
            ps.setTimestamp(3, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            ps.setTimestamp(4, new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NationalModelBuilder nationalModelBuilder = new NationalModelBuilder();
        NationalTrainingSetBuilder nationalTrainingSetBuilder = new NationalTrainingSetBuilder();
        String term_matrix_type = "liver";
        String trainingSet = nationalTrainingSetBuilder.getTrainingSet(term_matrix_type);
        String nameOfsvmModel = "libsvm" + term_matrix_type + ".model";

        try {
          //  svm.svm_save_model(nameOfsvmModel, nationalModelBuilder.svmTrain(trainingSet));
            nationalModelBuilder.svm_save_model(nationalModelBuilder.svmTrain(trainingSet),term_matrix_type,nameOfsvmModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Model Complete");
    }
}
