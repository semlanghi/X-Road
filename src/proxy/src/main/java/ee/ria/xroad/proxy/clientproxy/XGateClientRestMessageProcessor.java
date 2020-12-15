package ee.ria.xroad.proxy.clientproxy;

import ee.ria.xroad.common.conf.serverconf.IsAuthenticationData;
import ee.ria.xroad.common.message.RestResponse;
import ee.ria.xroad.common.monitoring.MessageInfo;
import ee.ria.xroad.common.opmonitoring.OpMonitoringData;
import ee.ria.xroad.xgate.XGateProducer;
import org.apache.http.client.HttpClient;
import org.apache.kafka.streams.KeyValue;
import org.eclipse.jetty.server.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

public class XGateClientRestMessageProcessor extends AbstractClientMessageProcessor{

    private XGateProducer xGateProducer;

    protected XGateClientRestMessageProcessor(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                              HttpClient httpClient, IsAuthenticationData clientCert,
                                              OpMonitoringData opMonitoringData, XGateProducer xGateProducer) throws Exception {
        super(servletRequest, servletResponse, httpClient, clientCert, opMonitoringData);
        this.xGateProducer = xGateProducer;
    }

    @Override
    public void process() throws Exception {

        String test;
        if ("POST".equalsIgnoreCase(servletRequest.getMethod()))
        {
            test = servletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            this.xGateProducer.produce(new KeyValue<>(servletRequest.getRemoteHost(), test),"default");

        }else throw new IllegalStateException("Not a POST request.");
    }

    @Override
    public MessageInfo createRequestMessageInfo() {
        return null;
    }


}
