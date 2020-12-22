package ee.ria.xroad.xgate;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class XGateComponent {

    protected final CopyOnWriteArrayList<String> topics;

    public XGateComponent() {
        this.topics = new CopyOnWriteArrayList<>();
    }

    public List<String> getTopics() {
        return topics;
    }

    public void subscribeTo(String topic){
        topics.add(topic);
    }

    public void removeSub(String topic) {
        topics.remove(topic);
    }

}
