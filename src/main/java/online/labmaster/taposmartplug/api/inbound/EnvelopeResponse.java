package online.labmaster.taposmartplug.api.inbound;

public class EnvelopeResponse extends TapoResponse {

    public Result result;

    public class Result {
        public String response;
    }

}
