/*
 * Testerra
 *
 * (C) 2013, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.qcconnector.annotation;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Class QCPathUtil.
 *
 * @author pele
 */
public final class QCPathUtil {

    /**
     * Hidden constructor.
     */
    private QCPathUtil() {

    }

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QCPathUtil.class);

    /**
     * Liefert den QCTestsetPath zum Testng Result.
     *
     * @param result Testng result.
     * @return TestsetPath.
     */
    public static String getQCTestsetForTestNGResult(final ITestResult result) {

        final Class<?> clazz = result.getTestClass().getRealClass();
        final Method method = result.getMethod().getConstructorOrMethod().getMethod();
        return getQCTestsetPath(clazz, method, result);
    }

    /**
     * Looks for a qc testset path info. If information is not available, null is returned. If information is available,
     * it is stored into the info container and returned. If information is already stored in the container, this value
     * is returned (also null).
     *
     * @param clazz Class to check.
     * @param method Method to check.
     * @param result result containing testParameters
     * @return any value, also null
     */
    public static String getQCTestsetPath(final Class<?> clazz, final Method method, final ITestResult result) {

        final String packageAndClassName = clazz.getName();
        final String methodName = method.getName();
        String testSetPath = null;

        if (eu.tsystems.mms.tic.testframework.qcconnector.annotation.TMInfoContainer.hasInfo(packageAndClassName, methodName)) {
            testSetPath = eu.tsystems.mms.tic.testframework.qcconnector.annotation.TMInfoContainer.readPath(packageAndClassName, methodName);
        }

        String newTestSetPath = checkForQCTestsetInfo(clazz, method, result);
        if (testSetPath == null || newTestSetPath == null || !testSetPath.equals(newTestSetPath)) {
            testSetPath = newTestSetPath;
            TMInfoContainer.savePath(packageAndClassName, methodName, testSetPath);
        }
        if (testSetPath != null) {
            LOGGER.info("QC TestSet Path: " + testSetPath);
        }
        return testSetPath;
    }

    /**
     * Checks a method for a QCTestset annotation. Returns the test path. Stores the test path into the container.
     *
     * @param clazz .
     * @param method .
     * @param result .
     * @return .
     */
    private static String checkForQCTestsetInfo(Class<?> clazz, Method method, ITestResult result) {

        String testSetPath = null;

        // Get the annotation.
        Annotation annotation = null;
        if (clazz.isAnnotationPresent(QCTestset.class)) {
            annotation = clazz.getAnnotation(QCTestset.class);
        }

        if (method.isAnnotationPresent(QCTestset.class)) {
            annotation = method.getAnnotation(QCTestset.class);
        }

        // Get the test path info.
        if (annotation != null) {
            QCTestset qcTestset;
            qcTestset = (QCTestset) annotation;

            testSetPath = qcTestset.value();
            testSetPath = PropertyManager.getPropertiesParser().parseLine(testSetPath);
        }

        if (result == null) {
            LOGGER.info("QC Path: " + testSetPath);
        } else {
            LOGGER.info("QC Path for " + result.getMethod().getMethodName() + ": " + testSetPath);
        }

        return testSetPath;
    }
}
