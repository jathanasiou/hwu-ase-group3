package ase.cw.control;

import ase.cw.model.Order;
import ase.cw.model.OrderItem;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Thomas on 01.03.2019.
 */
public class Server implements OrderConsumer,Runnable{

    private final Queue<Order> orderQueue;
    private final OrderStatus orderStatus;
    private int processTime=1000;
    private Thread serverThread;
    private String name="Server";
    public Server( Queue<Order> queue,OrderStatus orderStatus){
        if(queue==null || orderStatus==null) throw new InvalidParameterException("Queue and orderStatus must be not null");
        this.orderQueue=queue;
        this.orderStatus=orderStatus;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            Order currentOrder;
            //We actually do not need this synchronized block, sinze our implementated orderQueue is already thread safe.
            //But to make things more robust and consitent(It is possible to pass a non thread safe queue to the Server, in this case we would need the synchronized block)
            synchronized (orderQueue){
                currentOrder = orderQueue.poll();
            }

            //If we poll get a null element from the queue, we know the queue is empty and we can stop the thread
            if(currentOrder==null){
                System.out.println("Order queue empty, stop Server thread="+this.toString());
                break;
            }

            //Tell listener, we proceed a new order
            orderStatus.orderTaken(currentOrder,this);

            List<OrderItem> items = currentOrder.getOrderItems();
            for(OrderItem orderItem : items) {
                //Porceed item
                orderStatus.itemTaken(currentOrder,orderItem,this);
                try {
                    Thread.sleep(processTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Item finished after processTime ms
                orderStatus.itemFinished(currentOrder,orderItem,this);

            }
            //Order finished
            orderStatus.orderFinished(currentOrder,this);

        }
    }
    @Override
    public void setName(String name) {
        this.name=name;
        if(serverThread!=null) {
            serverThread.setName(name);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    @Override
    public int getOrderProcessTime() {
        return this.processTime;
    }

    @Override
    public void startOrderProcess() {
        if(serverThread==null) {
            serverThread = new Thread(this);
            serverThread.setName(name);
            serverThread.start();
        } else{
            System.out.println(getName()+ "already started");
        }
    }

    @Override
    public void setOrderProcessTime(int processTime) {
        if(processTime<0)throw new InvalidParameterException("Processtime must be greater than 0");
        this.processTime=processTime;
    }

    @Override
    public String toString() {
        return "Server{" +
                "processTime=" + processTime +
                ", serverThread=" + serverThread +
                '}';
    }
}
