package salesforce.core

import grpc.salesforce.core.SalesforceGrpcSubscriber
import grpc.salesforce.port.outgoing.ReplayTracker
import grpc.salesforce.port.outgoing.SalesforcePubSubPort
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.mockk


class SalesforceGrpcSubscriberTest : AnnotationSpec() {

    private val replayTracker: ReplayTracker = mockk()
    private val salesforcePubSubPort: SalesforcePubSubPort = mockk()
    private val serviceUnderTest = SalesforceGrpcSubscriber(replayTracker, salesforcePubSubPort)



}