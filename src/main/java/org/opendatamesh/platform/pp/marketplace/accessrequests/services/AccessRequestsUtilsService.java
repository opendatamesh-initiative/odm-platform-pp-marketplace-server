package org.opendatamesh.platform.pp.marketplace.accessrequests.services;

import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.AccessRequest;
import org.opendatamesh.platform.pp.marketplace.accessrequests.entities.ExecutorResponse;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader.AccessRequestResultUploaderCommand;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader.AccessRequestResultUploaderFactory;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.resultuploader.AccessRequestResultUploaderPresenter;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter.AccessRequestSubmitterCommand;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter.AccessRequestSubmitterFactory;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.submitter.AccessRequestSubmitterPresenter;
import org.opendatamesh.platform.pp.marketplace.client.NotificationClient;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestMapper;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestRes;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.events.ExecutorResultReceivedEvent;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.events.ExecutorResultReceivedEventMapper;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseMapper;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRequestsUtilsService {

    private final AccessRequestSubmitterFactory submitterFactory;
    private final AccessRequestResultUploaderFactory resultUploaderFactory;
    private final AccessRequestMapper mapper;
    private final MarketplaceExecutorResponseMapper executorResponseMapper;
    private final ExecutorResultReceivedEventMapper executorResultReceivedEventMapper;
    private final NotificationClient notificationClient;

    @Autowired
    public AccessRequestsUtilsService(
            AccessRequestSubmitterFactory submitterFactory,
            AccessRequestResultUploaderFactory resultUploaderFactory,
            AccessRequestMapper mapper,
            MarketplaceExecutorResponseMapper executorResponseMapper,
            ExecutorResultReceivedEventMapper executorResultReceivedEventMapper, NotificationClient notificationClient
    ) {
        this.submitterFactory = submitterFactory;
        this.resultUploaderFactory = resultUploaderFactory;
        this.mapper = mapper;
        this.executorResponseMapper = executorResponseMapper;
        this.executorResultReceivedEventMapper = executorResultReceivedEventMapper;
        this.notificationClient = notificationClient;
    }

    public AccessRequestRes submitAccessRequest(AccessRequestRes request) {
        AccessRequestSubmitterCommand command = new AccessRequestSubmitterCommand();
        command.setAccessRequest(mapper.toEntity(request));

        AccessRequestRes requestResult = new AccessRequestRes();
        AccessRequestSubmitterPresenter presenter = buildSubmitterPresenter(requestResult);

        submitterFactory
                .buildAccessRequestSubmitter(command, presenter)
                .execute();

        return requestResult;
    }

    public void uploadAccessRequestResult(String accessRequestUuid, MarketplaceExecutorResponseRes response) {
        ExecutorResponse executorResponse = executorResponseMapper.toEntity(response);

        ExecutorResultReceivedEvent executorResultEvent = new ExecutorResultReceivedEvent();
        executorResultEvent.setAfterState(new ExecutorResultReceivedEvent.ExecutorResultReceivedEventState());

        AccessRequestResultUploaderCommand command = new AccessRequestResultUploaderCommand(accessRequestUuid, executorResponse);
        AccessRequestResultUploaderPresenter presenter = buildExecutorResultUploaderPresenter(executorResultEvent);
        //Upload the results
        resultUploaderFactory.buildAccessRequestResultUploader(
                command,
                presenter
        ).execute();
        //Notify event
        notificationClient.notifyEvent(executorResultEvent);
    }

    private AccessRequestResultUploaderPresenter buildExecutorResultUploaderPresenter(ExecutorResultReceivedEvent executorResultEvent) {
        return new AccessRequestResultUploaderPresenter() {
            @Override
            public void presentException(RuntimeException e) {
                throw e;
            }

            @Override
            public void presentAccessRequest(AccessRequest o) {
                executorResultEvent.getAfterState().setAccessRequest(
                        executorResultReceivedEventMapper.toEventAccessRequest(o)
                );
            }

            @Override
            public void presentExecutorResponse(ExecutorResponse o) {
                executorResultEvent.getAfterState().setExecutorResponse(
                        executorResultReceivedEventMapper.toEventExecutorResponse(o)
                );
            }
        };
    }

    private AccessRequestSubmitterPresenter buildSubmitterPresenter(AccessRequestRes requestResult) {
        return new AccessRequestSubmitterPresenter() {

            @Override
            public void presentException(RuntimeException e) {
                throw e;
            }

            @Override
            public void presentClientException(RuntimeException e) {
                //DO NOTHING
            }

            @Override
            public void presentAccessRequest(AccessRequest accessRequest) {
                mapper.updateResourceFromEntity(accessRequest, requestResult);
            }
        };
    }
}
