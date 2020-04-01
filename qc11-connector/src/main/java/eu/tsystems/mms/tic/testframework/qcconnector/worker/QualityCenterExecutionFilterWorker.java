/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.qcconnector.worker;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.execution.testng.worker.MethodWorker;
import eu.tsystems.mms.tic.testframework.qcconnector.annotation.QCPathUtil;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterSyncUtils;
import eu.tsystems.mms.tic.testframework.qcrest.constants.QCProperties;
import eu.tsystems.mms.tic.testframework.qcrest.wrapper.TestSetTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.IInvokedMethod;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.lang.reflect.Method;

/**
 * Created by pele on 19.01.2017.
 */
public class QualityCenterExecutionFilterWorker extends MethodWorker {

    @Override
    public void run() {

        if (isTest()) {
            if (!this.checkExecutionFilter(this.testResult, this.invokedMethod)) {
                throw new SkipException(ErrorMessages.skippedByQcExecutionFilter());
            }
        }
    }

    /**
     * Check if the test should be skipped by the execution filter.
     *
     * @param testResult TestNg result object.
     * @param method     Invoked testmethod.
     *
     * @return true if test should be run, false otherwise.
     */
    private boolean checkExecutionFilter(ITestResult testResult, IInvokedMethod method) {

        final String filterProperty = PropertyManager.getProperty(QCProperties.QCEXECUTIONFILTER, null);
        if (StringUtils.isEmpty(filterProperty)) {
            return true;
        }

        if (method.isTestMethod()) {

            final String[] splittedProps = filterProperty.split(":");
            if (splittedProps.length < 3) {
                LOGGER.warn("Execution filter has not the expected format: e.g. exclude:status:passed");
                return true;
            }

            String qcTestsetForTestNGResult = QCPathUtil.getQCTestsetForTestNGResult(testResult);
            if (StringUtils.isEmpty(qcTestsetForTestNGResult)) {
                LOGGER.warn("Execution filter has not found a QC TestSet Path for the test "
                        + testResult.getTestName());
                return true;
            }

            final Class<?> clazz = testResult.getTestClass().getRealClass();
            final Method testMethod = testResult.getMethod().getConstructorOrMethod().getMethod();
            TestSetTest testSetTest = QualityCenterSyncUtils.getTestSetTestForAnnotation(clazz, testMethod,
                    testMethod.getName(), testResult);
            if (testSetTest == null) {
                LOGGER.error("No TestSetTest found in QC for QCTestSet annotation.");
                return true;
            }

            return QualityCenterSyncUtils.matchesExecutionFilter(testSetTest);
        }
        return true;
    }
}
