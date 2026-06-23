package Entity;

import java.util.Optional;

 public class Car {
    private String plateNumber;
    public Car(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Optional<String> getPlateNumber() {
        return Optional.ofNullable(plateNumber);
    }
}