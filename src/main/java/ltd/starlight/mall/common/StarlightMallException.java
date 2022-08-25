
package ltd.starlight.mall.common;

public class StarlightMallException extends RuntimeException {

    public StarlightMallException() {
    }

    public StarlightMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new StarlightMallException(message);
    }

}
