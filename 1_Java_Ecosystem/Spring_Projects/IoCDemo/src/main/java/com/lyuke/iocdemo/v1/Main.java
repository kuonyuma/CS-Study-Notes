package com.lyuke.iocdemo.v1;

public class Main {
    public static void main(String[] args) {

        Tire tire  = new Tire(19,"红色");
        CarChassis carChassis = new CarChassis(tire);
        CarBody carBody = new CarBody(carChassis);
        Car myCar = new Car(carBody);

        myCar.run();
    }
}
