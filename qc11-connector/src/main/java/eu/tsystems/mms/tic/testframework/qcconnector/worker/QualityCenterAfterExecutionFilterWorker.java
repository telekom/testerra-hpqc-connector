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
package eu.tsystems.mms.tic.testframework.qcconnector.worker;

import com.google.common.eventbus.Subscribe;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.events.MethodEndEvent;
import eu.tsystems.mms.tic.testframework.qcconnector.constants.ErrorMessages;

/**
 * Created by pele on 19.01.2017.
 */
public class QualityCenterAfterExecutionFilterWorker implements MethodEndEvent.Listener {

    @Override
    @Subscribe
    public void onMethodEnd(MethodEndEvent event) {

        if (event.getTestMethod().isTest()) {
            final Throwable throwable = event.getTestResult().getThrowable();
            if (event.isSkipped()
                    && throwable != null
                    && throwable.getMessage() != null
                    && throwable.getMessage().contains(ErrorMessages.skippedByQcExecutionFilter())) {
                String filter = PropertyManager.getProperty("qc.test.execution.filter", "");
                event.getMethodContext().addPriorityMessage("Test didn't run. It has been filtered by your qc execution filter settings."
                        + filter);
            }
        }
    }
}
