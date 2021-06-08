package ml;

import utility.DBConnection;
import libsvm.svm_node;
import libsvm.svm_problem;
import utility.SqlQueryResource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by VHACONMICHEG on 6/4/2017.
 */
public class NationalTrainingSetBuilder {

    DBConnection dbConnection = new DBConnection();
    SqlQueryResource sqlQueryResource = new SqlQueryResource();

    public svm_problem getProblem(String TrainingSet)
    {
        String[] splitTrainingSet = TrainingSet.split("\\s{2,}");
        List<String> listTrainingSet = Arrays.asList(splitTrainingSet);
        LinkedList<String> trainingSetAsLinkedList = new LinkedList<String>(listTrainingSet);

        int trainingSetSize=trainingSetAsLinkedList.size();

        double node_values[][] = new double[trainingSetSize][];
        int node_indexes[][] = new int[trainingSetSize][];
        double node_class_labels [] = new double[trainingSetSize];

        //Now store data values
        for(int i=0;i<trainingSetSize;i++)
        {
            try
            {
                String [] data1 = trainingSetAsLinkedList.get(i).split("\\s");
                node_class_labels[i] = Integer.parseInt(data1[0].trim());

                LinkedList<Integer> list_indx = new LinkedList<Integer>();
                LinkedList<Double> list_val = new LinkedList<Double>();

                for(int k=0;k<data1.length;k++)
                {
                    String [] tmp_data = data1[k].trim().split(":");
                    if(tmp_data.length==2)
                    {
                        list_indx.add(Integer.parseInt(tmp_data[0].trim()));
                        list_val.add(Double.parseDouble(tmp_data[1].trim()));
                    }
                }
                if(list_val.size()>0)
                {
                    node_values[i] = new double[list_val.size()];
                    node_indexes[i] = new int[list_indx.size()];
                }
                for(int m=0;m<list_val.size();m++)
                {
                    node_indexes[i][m] = list_indx.get(m);
                    node_values[i][m] = list_val.get(m);
                }
            }
            catch(Exception e)
            {
                System.err.println(TrainingSet);
                e.printStackTrace();
            }
        }

        svm_problem svmProblem = new svm_problem();
        svmProblem.y = new double[trainingSetSize];
        svmProblem.l = trainingSetSize;
        svmProblem.x = new svm_node[trainingSetSize][];

        for (int i = 0; i < trainingSetSize; i++) {
            svmProblem.y[i] = node_class_labels[i];
            double[] values = node_values[i];
            int[] indexes = node_indexes[i];
            svmProblem.x[i] = new svm_node[values.length];
            for (int j = 0; j < values.length; j++) {
                svm_node node = new svm_node();
                node.index = indexes[j];
                node.value = values[j];
                svmProblem.x[i][j] = node;
            }
        }
        return svmProblem;
    }

    //returns single result of term matrix from term_matrix table for given term_matrix_type
    public String getTrainingSet(String term_matrix_type) {
        try {
            //String select = "select term_matrix from svm.TermMatrix where term_matrix_type = (?)";
            String select = sqlQueryResource.getSqlQuery("national-select-term-matrix");
            PreparedStatement ps = dbConnection.getDataBaseConnection().prepareStatement(select);

            ps.setString(1, term_matrix_type);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String termMat = rs.getString("term_matrix");
                if (termMat != null) {
                    return termMat;
                }
            }
            ps.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No term matrix found";
    }



}
