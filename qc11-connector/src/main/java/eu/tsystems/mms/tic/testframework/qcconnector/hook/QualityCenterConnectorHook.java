/*
 * Created on 20.08.2019
 */
package eu.tsystems.mms.tic.testframework.qcconnector.hook;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import eu.tsystems.mms.tic.testframework.events.TesterraEventService;
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

    private QualityCenterTestResultSynchronizer synchronizer;

    @Override
    public void init() {

        TesterraCommons.init();

        // Register QC Filter
        TesterraListener.registerBeforeMethodWorker(QualityCenterExecutionFilterWorker.class);
        TesterraListener.registerAfterMethodWorker(QualityCenterAfterExecutionFilterWorker.class);

        // Register TesterraListener Impl. (SYNC EVENT)
        this.synchronizer = new QualityCenterTestResultSynchronizer();
        TesterraEventService.addListener(this.synchronizer);
    }

    @Override
    public void terminate() {

        // Logout.
        RestConnector.getInstance().logout();
        TesterraEventService.removeListener(this.synchronizer);
    }
}
