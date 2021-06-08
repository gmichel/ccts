package dao;

import libsvm.svm_parameter;

/**
 * Created by VHACONMICHEG on 6/3/2017.
 * Optimum parameter set for support vector machine stored here
 */
public class SVMParameterDao {
    public svm_parameter getSVMParameterDao()
    {
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
}
