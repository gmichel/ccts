package ml;

import libsvm.svm;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by VHACONMICHEG on 4/28/2017.
 */
public class CrossValidator {

    private svm_parameter getParameter()
    {
//for adjusting the cross validation
        svm_parameter svmParameter = new svm_parameter();
        svmParameter.probability = 1;
        svmParameter.gamma = 0.0078125;
        svmParameter.nu = 0.5;
        svmParameter.C = 2.0;
        svmParameter.svm_type = svm_parameter.C_SVC;
        svmParameter.kernel_type = svm_parameter.POLY;
        svmParameter.cache_size = 20000;
        svmParameter.eps = 0.001;
        svmParameter.coef0 = 2;
        svmParameter.degree = 2;
        return svmParameter;
    }

    private void do_cross_validation(String term_matrix_type) {
        int i;
        int total_correct = 0;
        NationalTrainingSetBuilder nationalTrainingSetBuilder = new NationalTrainingSetBuilder();
        String termMatrixType = term_matrix_type;
        String tSet = nationalTrainingSetBuilder.getTrainingSet(termMatrixType);
        String tSetMod = tSet.replaceAll("\\s{2,}", System.getProperty("line.separator"));
        svm_problem svmProblem = nationalTrainingSetBuilder.getProblem(tSet);
        String outputFileName =
                new SimpleDateFormat("yyyyMMddHHmm'.TermMatrix_"+termMatrixType+".txt'").format(new Date());
        try {
            FileUtils.writeStringToFile(new File(outputFileName), tSetMod, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        svm_parameter svmParameter = getParameter();
        int nr_fold = 5;
        double[] target = new double[svmProblem.l];

        svm.svm_cross_validation(svmProblem, svmParameter, nr_fold, target);

        for (i = 0; i < svmProblem.l; i++)
        {
            if (target[i] == svmProblem.y[i]) {
                ++total_correct;
            }
    }
            System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct / svmProblem.l + "%\n");
    }

    public static void main(String[] args) {
        CrossValidator cv = new CrossValidator();
        cv.do_cross_validation("lung");
    }

}
