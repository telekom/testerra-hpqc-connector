/*
 * Created on 20.08.2019
 */
package eu.tsystems.mms.tic.testframework.qcconnector.hook;

import com.google.common.eventbus.EventBus;
import eu.tsystems.mms.tic.testframework.hooks.ModuleHook;
import eu.tsystems.mms.tic.testframework.qcconnector.synchronize.QualityCenterTestResultSynchronizer;
import eu.tsystems.mms.tic.testframework.qcconnector.worker.QualityCenterAfterExecutionFilterWorker;
import eu.tsystems.mms.tic.testframework.qcconnector.worker.QualityCenterExecutionFilterWorker;
import eu.tsystems.mms.tic.testframework.qcrest.clients.RestConnector;
import eu.tsystems.mms.tic.testframework.report.TesterraListener;

/**
 * QualityCenterConnectorHook
 * <p>
 * Date: 20.08.2019
 * Time: 15:34
 *
 * @author erku
 */
public class QualityCenterConnectorHook implements ModuleHook {

    @Override
    public void init() {

        EventBus eventBus = TesterraListener.getEventBus();
        eventBus.register(new QualityCenterExecutionFilterWorker());
        eventBus.register(new QualityCenterAfterExecutionFilterWorker());
        eventBus.register(new QualityCenterTestResultSynchronizer());
    }

    @Override
    public void terminate() {
        // Logout.
        RestConnector.getInstance().logout();
    }
}
