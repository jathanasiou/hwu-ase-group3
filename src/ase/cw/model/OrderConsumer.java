package ase.cw.model;

/**
 * Created by Thomas on 01.03.2019.
 */
public interface OrderConsumer {

    void setName(String time);

    String getName();


    OrderHandler getOrderHandler();

    /**
     * @param processTime the time how long it takes to proceed each item
     */
    void setOrderProcessTime(int processTime);

    int getOrderProcessTime();

    /**
     * Start a order Process
     */
    void startOrderProcess();

    /**
     * Stops the OrderConsumer and all attached threads
     */
    void stopOrderProcess();
}
