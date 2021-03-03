package cn.edu.sicau.pfdistribution.dataInteraction;

public class NetRouterMessage<T> {
    private String functionCode;
    private String typeCode;
    private T message;

    public NetRouterMessage() {
    }

    public NetRouterMessage(String functionCode, String typeCode, T message) {
        this.functionCode = functionCode;
        this.typeCode = typeCode;
        this.message = message;
    }
}
