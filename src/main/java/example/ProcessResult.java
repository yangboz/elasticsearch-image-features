package example;

/**
 * Created by smartkit on 2016/12/23.
 */

public class ProcessResult
{
    String stdInp;
    String stdError;
    public String getStdInp() {
        return stdInp;
    }
    public void setStdInp(String stdInp) {
        this.stdInp = stdInp;
    }
    public String getStdError() {
        return stdError;
    }
    public void setStdError(String stdError) {
        this.stdError = stdError;
    }
    public ProcessResult(String stdInp, String stdError) {
        super();
        this.stdInp = stdInp;
        this.stdError = stdError;
    }

}
