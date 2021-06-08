package utility;

import libsvm.*;

public class svmPractice {


    public static void main(String[] args) {
        svm_problem sp = new svm_problem();
        svm_node[][] x = new svm_node[4][2];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                x[i][j] = new svm_node();
            }
        }
        x[0][0].value = 0;
        x[0][1].value = 0;

        x[1][0].value = 1;
        x[1][1].value = 1;

        x[2][0].value = 0;
        x[2][1].value = 1;

        x[3][0].value = 1;
        x[3][1].value = 0;


        double[] labels = new double[]{-1,1};
        sp.x = x;
        sp.y =  new double[4];
        sp.l = 4;
        svm_parameter prm = new svm_parameter();
        prm.svm_type = svm_parameter.C_SVC;
        prm.kernel_type = svm_parameter.RBF;
        prm.C = 1000;
        prm.eps = 0.0000001;
        prm.gamma = 10;
        prm.probability = 1;
        prm.cache_size=1024;
        System.out.println("Param Check " + svm.svm_check_parameter(sp, prm));
        svm_model model = svm.svm_train(sp, prm);
        System.out.println(" PA "+ model.probA[0] );
        System.out.println(" PB " + model.probB[0] );
        System.out.println(model.sv_coef[0][0]);
        System.out.println(model.sv_coef[0][1]);
        System.out.println(model.sv_coef[0][2]);
        System.out.println(model.sv_coef[0][3]);
        System.out.println(model.SV[0][0].value + "\t" + model.SV[0][1].value);
        System.out.println(model.SV[1][0].value + "\t" + model.SV[1][1].value);
        System.out.println(model.SV[2][0].value + "\t" + model.SV[2][1].value);
        System.out.println(model.SV[3][0].value + "\t" + model.SV[3][1].value);
        System.out.println(model.label[0]);
        System.out.println(model.label[1]);
        svm_node[] test = new svm_node[]{new svm_node(), new svm_node()};
        test[0].value = 0;
        test[1].value = 0;
        double[] l = new double[2];
        double result_prob = svm.svm_predict_probability(model, test,l);
        double result_normal = svm.svm_predict(model, test);
        System.out.println("Result with prob " + result_prob);
        System.out.println("Result normal " + result_normal);
        System.out.println("Probability " + l[0] + "\t" + l[1]);
    }

}
